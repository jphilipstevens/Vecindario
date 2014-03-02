package com.theEd209s.dataLoading;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.CkanRevision;
import play.Logger;

import com.theEd209s.utils.DownloadUtils;
import com.theEd209s.utils.DownloadUtils.Downloader;
import com.theEd209s.utils.DownloadUtils.Downloader.DownloadCancelledCmd;
import com.theEd209s.utils.DownloadUtils.Downloader.DownloadCompleteCmd;
import com.theEd209s.utils.DownloadUtils.Downloader.UpdateGuiDownloadProgressCmd;
import com.theEd209s.utils.FileUtils;
import com.theEd209s.utils.StringUtils;
import com.theEd209s.utils.ZipUtils;

import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanResource;

/**
 * This class can be used in implement standard data loading logic. <br />
 * This class will handle the downloading of the data file and the managing of
 * the temporary downloaded file. <br />
 * <br />
 * After instantiating an instance of this class, just call the
 * {@link DataLoader#parseFile() parseFile()} method.
 * 
 * @author Matthew Weiler
 * */
public abstract class DataLoader
{
	
	/* CONSTRUCTORS */
	/**
	 * This will create a new instance of a {@link DataLoader}.
	 * 
	 * @param sourceFile
	 * The source {@link File}.
	 * @param allowDeleteFile
	 * <code>true</code> if the specified {@link File} can be deleted once
	 * done; <code>false</code> if the specified {@link File} can not be
	 * deleted once processed.
	 * */
	public DataLoader(final File sourceFile, final boolean allowDeleteFile)
	{
		this.localFile = sourceFile;
		this.allowDeleteFile = allowDeleteFile;
	}
	
	/**
	 * This will create a new instance of a {@link DataLoader}.
	 * 
	 * @param ckanUrl
	 *            The base CKAN url.
	 * @param ckanDatesetId
	 *            The CKAN data-set ID.
	 * @param ckanResourceId
	 *            The CKAN resource ID.
	 * */
	public DataLoader(final String ckanUrl, final String ckanDatesetId, final String ckanResourceId)
	{
		if (!StringUtils.isNullOrEmpty(ckanUrl))
		{
			Logger.info("CKAN URL: " + ckanUrl);
			if (!StringUtils.isNullOrEmpty(ckanDatesetId))
			{
				Logger.info("CKAN Dataset ID: " + ckanDatesetId);
				if (!StringUtils.isNullOrEmpty(ckanResourceId))
				{
					Logger.info("CKAN Resource ID: " + ckanResourceId);
					final CkanClient client = new CkanClient(ckanUrl);
					if (client != null)
					{
						final CkanDataset dataset = client.getDataset(ckanDatesetId);
						if (dataset != null)
						{
							final List<CkanResource> resources = dataset.getResources();
							if ((resources != null) && (resources.size() > 0))
							{
								for (CkanResource resource : resources)
								{
									if (ckanResourceId.equalsIgnoreCase(resource.getId()))
									{
										long localRevisionTime = -1L;
										CkanRevision ckanRevision = CkanRevision.getCkanRevision(ckanDatesetId, ckanResourceId);
										if (ckanRevision != null)
										{
											localRevisionTime = ckanRevision.lastRevisionTime;
										}
										else
										{
											ckanRevision = new CkanRevision();
											ckanRevision.datasetId = ckanDatesetId;
											ckanRevision.resourceId = ckanResourceId;
										}
										long remoteRevisionTime = -1L;
										final String remoteRevisionTimestamp = resource.getRevisionTimestamp();
										if (!StringUtils.isNullOrEmpty(remoteRevisionTimestamp))
										{
											SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
											try
											{
												final Date remoteRevisionDate = formatter.parse(remoteRevisionTimestamp.replace("T", " "));
												remoteRevisionTime = remoteRevisionDate.getTime();
											}
											catch (ParseException e)
											{	
												
											}
										}
										if (remoteRevisionTime > -1L)
										{
											ckanRevision.lastRevisionTime = remoteRevisionTime;
											this.sourceExtName = resource.getFormat();
											if (localRevisionTime >= 0L)
											{
												if (remoteRevisionTime > localRevisionTime)
												{
													this.sourceUrl = resource.getUrl();
													this.ckanRevision = ckanRevision;
												}
											}
											else
											{
												this.sourceUrl = resource.getUrl();
												this.ckanRevision = ckanRevision;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/* PUBLIC METHODS */
	/**
	 * This method will download the {@link DataLoader#sourceUrl sourceUrl} and
	 * parse it using the {@link DataLoader#parseDownloadedFile(File)
	 * parseDownloadedFile(File)} method. <br />
	 * <br />
	 * If the download fails or is cancelled, the
	 * {@link DataLoader#downloadFailed(File) downloadFailed(File)} method will
	 * be called.
	 * */
	public void parseFile()
	{
		if (!StringUtils.isNullOrEmpty(this.sourceUrl))
		{
			try
			{
				final URL sourceUrl = new URL(this.sourceUrl);
				String fileExt = "";
				final int lastSlashIndex = this.sourceUrl.lastIndexOf('/');
				if (lastSlashIndex > 0)
				{
					final int lastDotIndex = this.sourceUrl.lastIndexOf('.');
					if ((lastDotIndex > lastSlashIndex) && (lastDotIndex < (this.sourceUrl.length() - 1)))
					{
						fileExt = this.sourceUrl.substring(lastDotIndex);
					}
				}
				final File destinationFile = new File(System.getProperty("java.io.tmpdir"), "dataLoading_" + (new Date()).getTime() + fileExt);
				final DownloadCompleteCmd downloadCompleteCmd = new DownloadCompleteCmd()
				{
					@Override
					public void downloadComplete(boolean downloadSuccessful)
					{
						if (downloadSuccessful)
						{
							DataLoader.this.parseDownloadedFile(destinationFile);
						}
						else
						{
							DataLoader.this.downloadFailed(destinationFile);
						}
					}
				};
				final DownloadCancelledCmd downloadCancelledCmd = new DownloadCancelledCmd()
				{
					@Override
					public void downloadCancelled(boolean userRequested)
					{
						DataLoader.this.downloadFailed(destinationFile);
					}
				};
				final UpdateGuiDownloadProgressCmd updateGuiDownloadProgressCmd = null;
				final Downloader downloader = DownloadUtils.getDownloader(downloadCompleteCmd, downloadCancelledCmd, updateGuiDownloadProgressCmd, 0L, sourceUrl, 0L, destinationFile, null, -1, -1, 0, true);
				final Thread downloaderThread = new Thread(downloader);
				downloaderThread.start();
				// Wait until the loading completes ...
				downloaderThread.join();
			}
			catch (MalformedURLException e)
			{
				Logger.error("Failed to start downloader.", e);
				this.downloadFailed((File) null);
			}
			catch (InterruptedException e)
			{
				Logger.error("Failed to wait for downloader thread.", e);
			}
		}
		else if ((this.localFile != null) && this.localFile.exists() && this.localFile.isFile())
		{
			this.parseDownloadedFile(this.localFile);
		}
		else
		{
			Logger.info("No valid source url was specified for the data loading (we likely already have the latest version).");
		}
	}
	
	/**
	 * This method will get the source url {@link String}.
	 * 
	 * @return The source url {@link String}.
	 * */
	public String getSourceUrl()
	{
		return this.sourceUrl;
	}
	
	/* PROTECTED METHODS */
	/**
	 * This method will attempt to parse the {@link File} which was downloaded.
	 * 
	 * @param downloadedFile
	 *            The {@link File} that was downloaded. <br />
	 *            This {@link File} will be deleted after this method completes
	 *            its execution; there is no need to delete it here.
	 * 
	 * @return
	 * The total number of rows inserted.
	 * */
	protected abstract int parseFile(final File downloadedFile) throws Throwable;
	
	/**
	 * This method will be called if the downloading of the {@link File} fails
	 * for some reason.
	 * */
	protected abstract void downloadFailed();
	
	/**
	 * This method will be called if the parsing of the {@link File} fails for
	 * some reason.
	 * */
	protected abstract void parseFailed();
	
	/* PRIVATE METHODS */
	/**
	 * This method will attempt to parse the {@link File} which was downloaded. <br />
	 * <br />
	 * Providing the {@link File} is verified to exist, the
	 * {@link DataLoader#parseFile(File) parseFile(File)} method will be called. <br />
	 * <br />
	 * If the {@link File} does not exist, the
	 * {@link DataLoader#downloadFailed(File)} method will be called.
	 * 
	 * @param downloadedFile
	 *            The {@link File} that was downloaded.
	 * */
	private void parseDownloadedFile(final File downloadedFile)
	{
		if ((downloadedFile != null) && downloadedFile.isFile() && downloadedFile.exists())
		{
			this.localFile = null;
			File unzippedDir = null;
			try
			{
				if (downloadedFile.getName().toLowerCase().endsWith(".zip"))
				{
					unzippedDir = new File(downloadedFile.getParentFile(), downloadedFile.getName() + "_unzippedDir");
					try
					{
						ZipUtils.unzip(true, downloadedFile.getAbsolutePath(), unzippedDir.getAbsolutePath());
						final File[] unzippedFiles = unzippedDir.listFiles(new FilenameFilter()
						{
							@Override
							public boolean accept(File dir, String name)
							{
								if (!StringUtils.isNullOrEmpty(DataLoader.this.sourceExtName))
								{
									return name.trim().toLowerCase().endsWith("." + DataLoader.this.sourceExtName.toLowerCase());
								}
								else
								{
									return true;
								}
							}
						});
						if ((unzippedFiles != null) && (unzippedFiles.length > 0))
						{
							this.localFile = unzippedFiles[0];
						}
					}
					catch (IOException ioe)
					{
						if ((unzippedDir != null) && unzippedDir.exists() && unzippedDir.isDirectory())
						{
							FileUtils.recursiveDelete(unzippedDir, false);
						}
						throw ioe;
					}
					finally
					{
						if ((downloadedFile != null) && downloadedFile.exists() && downloadedFile.isFile() && (!downloadedFile.delete()))
						{
							Logger.warn("Failed to delete temporary downloaded file: " + downloadedFile.getAbsolutePath());
						}
					}
				}
				else
				{
					this.localFile = downloadedFile;
				}
				if ((this.localFile != null) && this.localFile.exists() && this.localFile.isFile())
				{
					final int recordsInserted = this.parseFile(this.localFile);
					if (recordsInserted > 0)
					{
						if (this.ckanRevision != null)
						{
							this.ckanRevision.save();
						}
						Logger.info("Successfully inserted " + recordsInserted + " records.");
					}
					else
					{
						Logger.info("No records inserted.");
					}
				}
				else
				{
					this.downloadFailed(downloadedFile);
				}
			}
			catch (Throwable thrown)
			{
				if (this.localFile != null)
				{
					Logger.error("Failed to parse the downloaded file: " + this.localFile.getAbsolutePath(), thrown);
				}
				else
				{
					Logger.error("Failed to parse the downloaded file: " + downloadedFile.getAbsolutePath(), thrown);
				}
				this.parseFailed(this.localFile);
			}
			finally
			{
				if (this.allowDeleteFile && (this.localFile != null) && this.localFile.isFile() && this.localFile.exists())
				{
					this.localFile.delete();
				}
				if (this.allowDeleteFile && (unzippedDir != null) && unzippedDir.exists() && unzippedDir.isDirectory())
				{
					FileUtils.recursiveDelete(unzippedDir, false);
				}
			}
		}
		else
		{
			this.downloadFailed(downloadedFile);
		}
	}
	
	/**
	 * This method will be called if the downloading of the {@link File} fails
	 * for some reason. <br />
	 * <br />
	 * Once the specified {@link File} has been deleted, the
	 * {@link DataLoader#downloadFailed() downloadFailed()} method will be
	 * called.
	 * 
	 * @param downloadedFile
	 *            The {@link File} that was supposed to be downloaded.
	 * */
	private void downloadFailed(final File downloadedFile)
	{
		Logger.error("Failed to download the remote file.");
		if (this.allowDeleteFile && (downloadedFile != null) && (downloadedFile.exists() && (!downloadedFile.delete())))
		{
			Logger.error("Failed to delete the temporary file: " + downloadedFile.getAbsolutePath());
		}
		this.downloadFailed();
	}
	
	/**
	 * This method will be called if the parsing of the {@link File} fails for
	 * some reason. <br />
	 * <br />
	 * Once the specified {@link File} has been deleted, the
	 * {@link DataLoader#parseFailed() parseFailed()} method will be called.
	 * 
	 * @param downloadedFile
	 *            The {@link File} that was downloaded.
	 * */
	private void parseFailed(final File downloadedFile)
	{
		Logger.error("Failed to parse the downloaded file.");
		if (this.allowDeleteFile && (downloadedFile != null) && (downloadedFile.exists() && (!downloadedFile.delete())))
		{
			Logger.error("Failed to delete the temporary file: " + downloadedFile.getAbsolutePath());
		}
		this.parseFailed();
	}
	
	/* PROTECTED VARIABLES */
	/**
	 * The localFile that needs to be parsed
	 */
	protected File localFile = null;
	
	/* PRIVATE VARIABLES */
	/**
	 * This will store the flag to denote if the local file can be deleted once
	 * its done being processed.
	 * */
	private boolean allowDeleteFile = true;
	/**
	 * The url {@link String} of the file to download.
	 * */
	private String sourceUrl = null;
	/**
	 * The expected format of the file to parse.
	 * */
	private String sourceExtName = null;
	/**
	 * This will store a reference to the {@link CkanRevision} object that
	 * should be saved to the database after the {@link DataLoader#parseFile()
	 * parseFile()} method is fired.
	 * */
	private CkanRevision ckanRevision = null;
	
}
