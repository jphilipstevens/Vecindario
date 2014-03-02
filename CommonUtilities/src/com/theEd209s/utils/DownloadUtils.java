package com.theEd209s.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Locale;

import com.theEd209s.Constants;
import com.theEd209s.logging.Logger;
import com.theEd209s.utils.DownloadUtils.Downloader.DownloadCancelledCmd;
import com.theEd209s.utils.DownloadUtils.Downloader.DownloadCompleteCmd;
import com.theEd209s.utils.DownloadUtils.Downloader.UpdateGuiDownloadProgressCmd;
import com.theEd209s.utils.ShutdownRequestedUtils.UserRequestedShutdownException;

/**
 * This class contains several methods for downloading.
 * 
 * @author Matthew Weiler
 * */
public class DownloadUtils
{	
	
	/* PUBLIC METHODS */
	/**
	 * This will get the initial downloading buffer byte-size.
	 * 
	 * @return
	 * The initial downloading buffer byte-size.
	 * */
	public static int getDownloadingBufferSize()
	{
		return Constants.DOWNLOAD_FILE_INITIAL_BUFFER_SIZE_DEFAULT;
	}
	
	/**
	 * This method will check if the specified remote address is accessible.
	 * @param remoteAddress
	 * The remote address to try to connect to.
	 * 
	 * @return
	 * <code>true</code> if the remote address is accessible; <code>false</code> otherwise.
	 * */
	public static boolean isAddressAccessible(final String remoteAddress)
	{
		if ((remoteAddress != null) && (remoteAddress.trim().length() > 0))
		{
			try
			{
				final URL url = new URL(remoteAddress);
				HttpURLConnection urlConn = null;
				try
				{
					urlConn = (HttpURLConnection) url.openConnection();
					urlConn.setConnectTimeout(Constants.TIMEOUT_CONNECTION);
					urlConn.setReadTimeout(Constants.TIMEOUT_READ);
					urlConn.setRequestMethod("HEAD");
					urlConn.setDoInput(true);
					urlConn.connect();
					int response = urlConn.getResponseCode();
					return 200 == response;
				}
				catch (IOException ioe)
				{
					DownloadUtils.logger.warning("isAddressAccessible", "Failed to establish connection with: " + remoteAddress, ioe);
				}
				finally
				{
					if (urlConn != null)
					{
						urlConn.disconnect();
					}
				}
			}
			catch (MalformedURLException e)
			{
				DownloadUtils.logger.warning("isAddressAccessible", "Invalid remote address given: " + remoteAddress, e);
			}
		}
		return false;
	}
	
	/**
	 * This method will download the MD5 file specified and return the MD5 hash code found within.
	 * 
	 * @param md5Url
	 * The {@link URL} of the MD5 file.
	 * 
	 * @return
	 * The MD5 hash code found within the specified file.
	 * */
	public static String extractMD5HashCode(final URL md5Url)
	{
		if ((md5Url != null) && DownloadUtils.isAddressAccessible(md5Url.toExternalForm()))
		{
			return DownloadUtils.downloadMD5Checksum(0, md5Url);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * This method will calculate the MD5 hash code from the {@link File} specified and return its MD5 hash code.
	 * 
	 * @param md5File
	 * The {@link File} of the MD5 file.
	 * 
	 * @return
	 * The MD5 hash code that was calculated from the specified {@link File}.
	 * */
	public static String extractMD5HashCode(final File md5File) throws Exception
	{
		if ((md5File != null) && md5File.exists() && md5File.canRead() && md5File.isFile())
		{
			InputStream fis = null;
			try
			{
				fis = new FileInputStream(md5File);
				final MessageDigest digest = MessageDigest.getInstance("MD5");
				byte[] buffer = new byte[1024];
				int numRead;
				while ((numRead = fis.read(buffer)) != -1)
				{
					digest.update(buffer, 0, numRead);
				}
				fis.close();
				final byte digestBytes[] = digest.digest();
				StringBuffer md5HashCode = new StringBuffer();
				for (byte digestByte : digestBytes)
				{
					md5HashCode.append(Integer.toString((digestByte & 0xff) + 0x100, 16).substring(1));
				}
				return md5HashCode.toString().toUpperCase(Locale.US);
			}
			finally
			{
				if (fis != null)
				{
					try
					{
						fis.close();
					}
					catch (IOException ioe)
					{
						// ignore...
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * This method will build a {@link Downloader}.
	 * 
	 * @param downloadCompleteCmd
	 * The {@link DownloadCompleteCmd} that should be fired once the download completes.
	 * @param downloadCancelledCmd
	 * The {@link DownloadCancelledCmd} that should be fired if the download is cancelled.
	 * @param updateGuiDownloadProgressCmd
	 * The {@link UpdateGuiDownloadProgressCmd} implementation that should be fired
	 * when the GUI should be updated.
	 * @param multiFileBytesDownloadedAlready
	 * The total number of bytes that have been downloaded so far (including other files).
	 * <br />
	 * This is useful if this download is part of a multi-file download and the
	 * visual progress should encapsulate all files.
	 * @param sourceUrl
	 * The {@link URL} representing the {@link File} to download.
	 * @param expectedSize
	 * The expected size of the source {@link File}.
	 * @param destinationFile
	 * The {@link File} that should be saved.
	 * @param expectedMd5HashCode
	 * The expected MD5 hash code of the file after downloaded.
	 * @param maxRetries
	 * The total number of times to try and download the {@link File}.
	 * <br />
	 * <i>if less-than 0 is specified, the default of
	 * {@link ShutdownRequestedUtils#DOWNLOAD_FILE_MAX_RETRIES_DEFAULT} will be used</i>
	 * @param maxChunkRetries
	 * The total number of times that a chunk of data should be retried.
	 * <br />
	 * <i>if less-than 0 is specified, the default of
	 * {@link ShutdownRequestedUtils#DOWNLOAD_FILE_MAX_CHUNK_RETRIES_DEFAULT} will be used</i>
	 * @param initialBufferSize
	 * The initial size of the download buffer.
	 * <br />
	 * <i>if less-than 1 is specified, the default of
	 * {@link ShutdownRequestedUtils#DOWNLOAD_FILE_INITIAL_BUFFER_SIZE_DEFAULT} will be used</i>
	 * @param diminishingBuffer
	 * <code>true</code> if each failed attempt to download should result
	 * in a smaller download buffer size.
	 * 
	 * @return
	 * The newly created {@link Downloader}.
	 * */
	public static Downloader getDownloader(final DownloadCompleteCmd downloadCompleteCmd, final DownloadCancelledCmd downloadCancelledCmd, final UpdateGuiDownloadProgressCmd updateGuiDownloadProgressCmd, final long multiFileBytesDownloadedAlready, final URL sourceUrl, final long expectedSize, final File destinationFile, final String expectedMd5HashCode, final int maxRetries, final int maxChunkRetries, final int initialBufferSize, final boolean diminishingBuffer)
	{
		return new Downloader(downloadCompleteCmd, downloadCancelledCmd, updateGuiDownloadProgressCmd, multiFileBytesDownloadedAlready, sourceUrl, expectedSize, destinationFile, expectedMd5HashCode, maxRetries, maxChunkRetries, initialBufferSize, diminishingBuffer);
	}
	
	/* PRIVATE METHODS */
	/**
	 * This method will download the file at the the specified {@link URL}, extract its MD5 checksum hash code and return it.
	 * 
	 * @param tryCount
	 * The total number of attempts made so far.
	 * @param md5ChecksumUrl
	 * The specified {@link URL}.
	 * 
	 * @return
	 * The MD5 checksum hash code from the specified remote file.
	 * */
	private static String downloadMD5Checksum(final int tryCount, final URL md5ChecksumUrl)
	{
		String remoteHashCode = null;
		if (md5ChecksumUrl != null)
		{
			HttpURLConnection conn = null;
			InputStream is = null;
			BufferedInputStream bis = null;
			try
			{
				conn = (HttpURLConnection) md5ChecksumUrl.openConnection();
				conn.setConnectTimeout(Constants.TIMEOUT_CONNECTION);
				conn.setReadTimeout(Constants.TIMEOUT_READ);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.connect();
				int response = conn.getResponseCode();
				if (200 == response)
				{
					is = conn.getInputStream();
				}
				if (is != null)
				{
					bis = new BufferedInputStream(is);
					final ArrayList<Byte> fullBytes = new ArrayList<Byte>();
					// Define after how many bytes read should the GUI be updated.
					byte data[] = new byte[DownloadUtils.getDownloadingBufferSize()];
					// This will store the number of bytes downloaded in each iteration of the loop.
					int bytesRead = -1;
					long bytesReadSoFar = 0L;
					// Loop to read bytes from the server until there are none left in the InputStream.
					while ((bytesRead = DownloadUtils.attemptRead(bis, data, DownloadUtils.getDownloadingBufferSize(), bytesReadSoFar, 0L, 0)) != -1)
					{
						bytesReadSoFar += bytesRead;
						for (int n = 0; n < bytesRead; n++)
						{
							fullBytes.add(data[n]);
						}
					}
					if ((fullBytes != null) && (fullBytes.size() > 0))
					{
						final byte[] byteValues = new byte[fullBytes.size()];
						for (int n = 0; n < fullBytes.size(); n++)
						{
							byteValues[n] = fullBytes.get(n).byteValue();
						}
						remoteHashCode = (new String(byteValues)).trim().toUpperCase(Locale.US);
						DownloadUtils.logger.debug("downloadMD5Checksum", "Read MD5 hash code \"" + remoteHashCode + "\" from: " + md5ChecksumUrl.toExternalForm());
					}
					else
					{
						DownloadUtils.logger.error("downloadMD5Checksum", "Failed to read any bytes from: " + md5ChecksumUrl.toExternalForm());
					}
				}
				else
				{
					DownloadUtils.logger.error("downloadMD5Checksum", "Failed to connect to: " + md5ChecksumUrl.toExternalForm());
				}
			}
			catch (IOException ioe)
			{
				DownloadUtils.logger.error("downloadMD5Checksum", "Failed to read remote file: " + md5ChecksumUrl.getFile(), ioe);
			}
			finally
			{
				if (bis != null)
				{
					try
					{
						bis.close();
					}
					catch (IOException ioe)
					{
						DownloadUtils.logger.error("downloadMD5Checksum", "Failed to close buffered input stream while reading hash code file: " + md5ChecksumUrl.getFile(), ioe);
					}
				}
				if (is != null)
				{
					try
					{
						is.close();
					}
					catch (IOException ioe)
					{
						logger.error("downloadMD5Checksum", "Failed to close input stream while reading hash code file: " + md5ChecksumUrl.getFile(), ioe);
					}
				}
				if (conn != null)
				{
					conn.disconnect();
				}
			}
		}
		if ((remoteHashCode != null) && (remoteHashCode.trim().length() > 0) && (tryCount < (Constants.DOWNLOAD_FILE_MAX_RETRIES_DEFAULT + 1)))
		{
			return DownloadUtils.downloadMD5Checksum(tryCount + 1, md5ChecksumUrl);
		}
		else
		{
			return remoteHashCode;
		}
	}
	
	/**
	 * This method will attempt to read bytes from the specified {@link InputStream}.
	 * 
	 * @param is
	 * The {@link InputStream} from which to read.
	 * @param buffer
	 * The buffer to read into.
	 * @param bufferSize
	 * The total number of bytes to read per attempt.
	 * @param totalBytesReadSoFar
	 * The total number of bytes read so far from this {@link File}.
	 * @param expectedSize
	 * The expected size of the source {@link File}.
	 * @param maxAttempts
	 * The maximum number of times to try and read.
	 * 
	 * @return
	 * The total number of bytes read into the specified buffer.
	 * */
	private static int attemptRead(final InputStream is, byte[] buffer, int bufferSize, final long totalBytesReadSoFar, final long expectedSize, final int maxAttempts) throws IOException
	{
		if (is != null)
		{
			try
			{
				try
				{
					// If the buffer size to read is greater than the total number of bytes
					// expected to be there, then lower the buffer size to match that expected
					// remaining number of bytes.
					if ((expectedSize > 0) && ((totalBytesReadSoFar + bufferSize ) > expectedSize))
					{
						bufferSize = (int)(expectedSize - totalBytesReadSoFar);
					}
					return is.read(buffer, 0, bufferSize);
				}
				// If an End-of-File exception is thrown, then just read the remaining bytes
				// one-by-one.
				catch (EOFException eofe)
				{
					byte[] tmpBuffer = new byte[1];
					int readIndex = 0;
					while (is.read(tmpBuffer, 0, 1) != -1)
					{
						buffer[readIndex] = tmpBuffer[0];
						readIndex++;
					}
					return readIndex;
				}
			}
			catch (IOException ioe)
			{
				if (maxAttempts > 1)
				{
					return DownloadUtils.attemptRead(is, buffer, bufferSize, totalBytesReadSoFar, expectedSize, maxAttempts - 1);
				}
				else
				{
					throw ioe;
				}
			}
		}
		else
		{
			throw new IOException("No valid InputStream specified.");
		}
	}
	
	/* PRIVATE CONSTANTS */
	/**
	 * This will be used to log any errors to a log file.
	 * */
	private static final Logger logger = new Logger(DownloadUtils.class.getName());
	
	/* PUBLIC CLASSES */
	/**
	 * This class can be used to perform the download of a {@link File}.
	 * <br />
	 * This class implements {@link Runnable} and should be run in its own
	 * {@link Thread}.
	 * 
	 * @author Matthew Weiler
	 * */
	public static class Downloader implements Runnable
	{
		
		/* CONSTRUCTORS */
		/**
		 * This will create a new instance of a {@link Downloader}.
		 * 
		 * @param downloadCompleteCmd
		 * The {@link DownloadCompleteCmd} that should be fired once the download completes.
		 * @param downloadCancelledCmd
		 * The {@link DownloadCancelledCmd} that should be fired if the download is cancelled.
		 * @param updateGuiDownloadProgressCmd
		 * The {@link UpdateGuiDownloadProgressCmd} implementation that should be fired
		 * when the GUI should be updated.
		 * @param multiFileBytesDownloadedAlready
		 * The total number of bytes that have been downloaded so far (including other files).
		 * <br />
		 * This is useful if this download is part of a multi-file download and the
		 * visual progress should encapsulate all files.
		 * @param sourceUrl
		 * The {@link URL} representing the {@link File} to download.
		 * @param expectedSize
		 * The expected size of the source {@link File}.
		 * @param destinationFile
		 * The {@link File} that should be saved.
		 * @param expectedMd5HashCode
		 * The expected MD5 hash code of the file after downloaded.
		 * @param maxRetries
		 * The total number of times to try and download the {@link File}.
		 * <br />
		 * <i>if less-than 0 is specified, the default of
		 * {@link Constants#DOWNLOAD_FILE_MAX_RETRIES_DEFAULT} will be used</i>
		 * @param maxChunkRetries
		 * The total number of times that a chunk of data should be retried.
		 * <br />
		 * <i>if less-than 0 is specified, the default of
		 * {@link Constants#DOWNLOAD_FILE_MAX_CHUNK_RETRIES_DEFAULT} will be used</i>
		 * @param initialBufferSize
		 * The initial size of the download buffer.
		 * <br />
		 * <i>if less-than 1 is specified, the default of
		 * {@link Constants#DOWNLOAD_FILE_INITIAL_BUFFER_SIZE_DEFAULT} will be used</i>
		 * @param diminishingBuffer
		 * <code>true</code> if each failed attempt to download should result
		 * in a smaller download buffer size.
		 * */
		public Downloader(final DownloadCompleteCmd downloadCompleteCmd, final DownloadCancelledCmd downloadCancelledCmd, final UpdateGuiDownloadProgressCmd updateGuiDownloadProgressCmd, final long multiFileBytesDownloadedAlready, final URL sourceUrl, final long expectedSize, final File destinationFile, final String expectedMd5HashCode, final int maxRetries, final int maxChunkRetries, final int initialBufferSize, final boolean diminishingBuffer)
		{
			this.downloadCompleteCmd = downloadCompleteCmd;
			this.downloadCancelledCmd = downloadCancelledCmd;
			this.updateGuiDownloadProgressCmd = updateGuiDownloadProgressCmd;
			this.multiFileBytesDownloadedAlready = multiFileBytesDownloadedAlready;
			this.sourceUrl = sourceUrl;
			this.expectedSize = expectedSize;
			this.destinationFile = destinationFile;
			this.expectedMd5HashCode = expectedMd5HashCode;
			this.maxRetries = maxRetries;
			this.maxChunkRetries = maxChunkRetries;
			this.currentBufferSize = initialBufferSize;
			this.diminishingBuffer = diminishingBuffer;
		}
		
		/* PUBLIC METHODS */
		/**
		 * This method will attempt to download the {@link File}, specified by
		 * its {@link URL}.
		 * <br />
		 * If the download fails, for any reason, a series of retries will be
		 * performed to ensure that the file is acquired, if possible.
		 * 
		 * @return
		 * <code>true</code> if the {@link File} specified by its {@link URL}
		 * is downloaded successfully; <code>false</code> otherwise.
		 * */
		@Override
		public void run()
		{
			try
			{
				this.downloading = true;
				// Ensure that the destination File is set.
				if (destinationFile != null)
				{
					final File tmpDownloadFile = new File(destinationFile.getParentFile(), destinationFile.getName() + "_tmpDownload");
					if ((!tmpDownloadFile.exists()) || tmpDownloadFile.delete())
					{
						try
						{
							if (this.maxRetries < 0)
							{
								this.maxRetries = Constants.DOWNLOAD_FILE_MAX_RETRIES_DEFAULT;
							}
							if (this.maxChunkRetries < 0)
							{
								this.maxChunkRetries = Constants.DOWNLOAD_FILE_MAX_CHUNK_RETRIES_DEFAULT;
							}
							if (this.currentBufferSize < 1)
							{
								this.currentBufferSize = DownloadUtils.getDownloadingBufferSize();
							}
							if (this.multiFileBytesDownloadedAlready < 0L)
							{
								this.multiFileBytesDownloadedAlready = 0L;
							}
							if (this.downloadFile(0, tmpDownloadFile))
							{
								if (tmpDownloadFile.exists())
								{
									if ((!destinationFile.exists()) || destinationFile.delete())
									{
										if (tmpDownloadFile.renameTo(destinationFile))
										{
											if (this.downloadCompleteCmd != null)
											{
												this.downloadCompleteCmd.downloadComplete(true);
											}
											return;
										}
										else
										{
											DownloadUtils.logger.error("downloadFile", "Failed to rename temporary file from (" + tmpDownloadFile + ") to (" + destinationFile + ").");
										}
									}
									else
									{
										DownloadUtils.logger.error("downloadFile", "Failed to delete existing file: " + destinationFile.getAbsolutePath());
									}
								}
								else
								{
									DownloadUtils.logger.error("downloadFile", "Failed to find temporary file: " + tmpDownloadFile.getAbsolutePath());
								}
							}
						}
						catch (UserRequestedShutdownException urse)
						{
							if (this.downloadCancelledCmd != null)
							{
								this.downloadCancelledCmd.downloadCancelled(false);
							}
							return;
						}
						catch (UserRequestedCancelledException urce)
						{
							if (this.downloadCancelledCmd != null)
							{
								this.downloadCancelledCmd.downloadCancelled(true);
							}
							return;
						}
					}
				}
				else
				{
					DownloadUtils.logger.error("downloadFile", "No valid destination file specified.");
				}
				if (this.downloadCompleteCmd != null)
				{
					this.downloadCompleteCmd.downloadComplete(false);
				}
			}
			finally
			{
				this.downloading = false;
			}
		}
		
		/**
		 * This method will request that this download be cancelled.
		 * */
		public void requestCancel()
		{
			this.cancelRequested = true;
		}
		
		/* GETTERS & SETTERS */
		/**
		 * This will check if this {@link Downloader} is currently downloading.
		 * 
		 * @return
		 * <code>true</code> if this {@link Downloader} is currently downloading;
		 * <code>false</code> if this {@link Downloader} either hasn't started
		 * yet or has completed.
		 * */
		public boolean isDownloading()
		{
			return this.downloading;
		}
		
		/* PRIVATE METHODS */
		/**
		 * This method will attempt to download the {@link File}, specified by its {@link URL}.
		 * <br />
		 * If the download fails, for any reason, a series of retries will be performed to ensure
		 * that the file is acquired, if possible.
		 * 
		 * @param attemptIndex
		 * The attempt index; the first time that this method is called, this should be 0.
		 * @param tmpDestinationFile
		 * The {@link File} to download to.
		 * 
		 * @return
		 * <code>true</code> if the {@link File} specified by its {@link URL} is downloaded successfully;
		 * <code>false</code> otherwise.
		 * 
		 * @throws UserRequestedShutdownException
		 * This will be thrown if the system is shutdown using the
		 * {@link ShutdownRequestedUtils#isShutdownRequested()} method returns true during the download.
		 * @throws UserRequestedCancelledException
		 * This will be thrown if the user requests that this {@link Downloader} be cancelled, using the
		 * {@link Downloader#requestCancel()} method, during the download.
		 * */
		private boolean downloadFile(int attemptIndex, final File tmpDestinationFile) throws UserRequestedShutdownException, UserRequestedCancelledException
		{
			// Ensure that the source URL is set.
			if (this.sourceUrl != null)
			{
				// Ensure that the destination File is set.
				if (tmpDestinationFile != null)
				{
					// Ensure that all of the input values are valid.
					if (attemptIndex < 0)
					{
						attemptIndex = 0;
					}
					// Initialize the download succeeded flag to false.
					boolean downloadSucceeded = false;
					long bytesReadSoFar = 0;
					InputStream is = null;
					OutputStream os = null;
					OutputStream fos = null;
					try
					{
						final URLConnection conn = this.sourceUrl.openConnection();
						conn.setConnectTimeout(Constants.TIMEOUT_CONNECTION);
						conn.setReadTimeout(Constants.TIMEOUT_READ);
						is = conn.getInputStream();
						fos = new FileOutputStream(tmpDestinationFile);
						os = new BufferedOutputStream(fos);
						byte[] buffer = new byte[this.currentBufferSize];
						int bytesRead = 0;
						int bytesReadSinceFlush = 0;
						int totalBytesReadSinceGuiUpdate = 0;
						while ((bytesRead = DownloadUtils.attemptRead(is, buffer, this.currentBufferSize, bytesReadSoFar, this.expectedSize, this.maxChunkRetries)) > 0)
						{
							os.write(buffer, 0, bytesRead);
							bytesReadSoFar += bytesRead;
							bytesReadSinceFlush += bytesRead;
							totalBytesReadSinceGuiUpdate += bytesRead;
							if (bytesReadSinceFlush >= Constants.DOWNLOAD_FILE_FLUSH_THRESHOLD)
							{
								bytesReadSinceFlush = 0;
								os.flush();
							}
							if (totalBytesReadSinceGuiUpdate >= Constants.DOWNLOAD_GUI_UPDATE_THRESHOLD)
							{
								totalBytesReadSinceGuiUpdate = 0;
								if (this.updateGuiDownloadProgressCmd != null)
								{
									this.updateGuiDownloadProgressCmd.updateGuiDownloadProgress(this.multiFileBytesDownloadedAlready + bytesReadSoFar);
								}
							}
							if (bytesReadSoFar >= Constants.DOWNLOAD_CANCEL_CHECK_THRESHOLD)
							{
								if (ShutdownRequestedUtils.isShutdownRequested())
								{
									throw new UserRequestedShutdownException();
								}
								if (this.cancelRequested)
								{
									throw new UserRequestedCancelledException();
								}
							}
						}
						downloadSucceeded = true;
					}
					catch (UserRequestedShutdownException | UserRequestedCancelledException ure)
					{
						throw ure;
					}
					catch (Throwable thrown)
					{
						if (attemptIndex < this.maxRetries)
						{
							DownloadUtils.logger.warning("downloadFile", "Failed to download file: " + sourceUrl.toExternalForm() + ".   Trying again ...", thrown);
						}
						else
						{
							DownloadUtils.logger.error("downloadFile", "Failed to download file: " + sourceUrl.toExternalForm(), thrown);
						}
					}
					finally
					{
						if (os != null)
						{
							try
							{
								os.flush();
							}
							catch (IOException e)
							{
								DownloadUtils.logger.warning("downloadFile", "Failed to flush OutputStream.", e);
							}
							try
							{
								os.close();
							}
							catch (IOException e)
							{
								DownloadUtils.logger.warning("downloadFile", "Failed to close OutputStream.", e);
							}
						}
						if (is != null)
						{
							try
							{
								is.close();
							}
							catch (IOException e)
							{
								DownloadUtils.logger.warning("downloadFile", "Failed to close InputStream.", e);
							}
						}
					}
					// Compare the MD5 of the downloaded file to that of the expected value.
					// If they match, then mark the download as succeeded.
					if (downloadSucceeded)
					{
						try
						{
							if ((this.expectedMd5HashCode != null) && (this.expectedMd5HashCode.trim().length() > 0) && !this.expectedMd5HashCode.equalsIgnoreCase(DownloadUtils.extractMD5HashCode(tmpDestinationFile)))
							{
								downloadSucceeded = false;
							}
							if ((this.expectedSize > 0L) && (this.expectedSize != bytesReadSoFar))
							{
								downloadSucceeded = false;
							}
						}
						catch (Exception e)
						{
							downloadSucceeded = false;
							DownloadUtils.logger.error("downloadFile", "Failed to extract MD5 from file: " + tmpDestinationFile.getAbsolutePath(), e);
						}
					}
					// If the download was not successful, then we may try again... if we have any retries left.
					if (!downloadSucceeded)
					{
						if (attemptIndex < this.maxRetries)
						{
							if (this.diminishingBuffer)
							{
								this.currentBufferSize = this.currentBufferSize / 4;
								if (((this.maxRetries - attemptIndex) <= 1) || (this.currentBufferSize < 1))
								{
									this.currentBufferSize = 1;
								}
							}
							return this.downloadFile(attemptIndex + 1, tmpDestinationFile);
						}
					}
					else
					{
						return true;
					}
				}
				else
				{
					DownloadUtils.logger.error("downloadFile", "No valid temporary destination file specified.");
				}
			}
			else
			{
				DownloadUtils.logger.error("downloadFile", "No valid source URL specified.");
			}
			return false;
		}
		
		/* PRIVATE VARIABLES */
		/**
		 * This will store the flag to denote if this {@link Downloader} is
		 * currently downloading.
		 * */
		private boolean downloading = false;
		/**
		 * This will store the flag to denote if the user requested that
		 * this download be cancelled.
		 * */
		private boolean cancelRequested = false;
		/**
		 * This will store the {@link DownloadCompleteCmd} that will be called
		 * if the download completes.
		 * */
		private DownloadCompleteCmd downloadCompleteCmd = null;
		/**
		 * This will store the {@link DownloadCancelledCmd} that will be called
		 * if the download is cancelled.
		 * */
		private DownloadCancelledCmd downloadCancelledCmd = null;
		/**
		 * This will store the {@link UpdateGuiDownloadProgressCmd} that will be called
		 * whenever progress should be given to the GUI.
		 * */
		private UpdateGuiDownloadProgressCmd updateGuiDownloadProgressCmd = null;
		/**
		 * This will store the total number of bytes that have been downloaded so far
		 * (including other files).
		 * <br />
		 * This is useful if this download is part of a multi-file download and the
		 * visual progress should encapsulate all files.
		 * */
		private long multiFileBytesDownloadedAlready = 0L;
		/**
		 * This will store the {@link URL} representing the {@link File} to download.
		 * */
		private URL sourceUrl = null;
		/**
		 * This will store the expected size of the source {@link File}.
		 * */
		private long expectedSize = 0L;
		/**
		 * This will store the {@link File} that should be saved.
		 * */
		private File destinationFile = null;
		/**
		 * This will store the expected MD5 hash code of the file after downloaded.
		 * <br />
		 * If this is empty, no MD5 verification will be done.
		 * */
		private String expectedMd5HashCode = null;
		/**
		 * This will store the total number of times to try and download the {@link File}.
		 * <br />
		 * <i>if less-than 0 is specified, the default of
		 * {@link Constants#DOWNLOAD_FILE_MAX_RETRIES_DEFAULT} will be used</i>.
		 * */
		private int maxRetries = 0;
		/**
		 * This will store the total number of times that a chunk of data should be retried.
		 * <br />
		 * <i>if less-than 0 is specified, the default of
		 * {@link Constants#DOWNLOAD_FILE_MAX_CHUNK_RETRIES_DEFAULT} will be used</i>.
		 * */
		private int maxChunkRetries = 0;
		/**
		 * This will store the initial size of the download buffer.
		 * <br />
		 * <i>if less-than 1 is specified, the default of
		 * {@link Constants#DOWNLOAD_FILE_INITIAL_BUFFER_SIZE_DEFAULT} will be used</i>.
		 * */
		private int currentBufferSize = 0;
		/**
		 * This will store the flag which denotes if each failed attempt to download should result
		 * in a smaller download buffer size.
		 * */
		private boolean diminishingBuffer = true;
		
		/* PUBLIC CLASSES */
		/**
		 * This class will be used to signify that the user requested that the download in
		 * question be cancelled.
		 * 
		 * @author Matthew Weiler
		 * */
		public static class UserRequestedCancelledException extends Exception
		{
			
			private static final long serialVersionUID = 1L;
			
			/**
			 * This will create a new instance of a
			 * {@link UserRequestedShutdownException}.
			 * */
			public UserRequestedCancelledException()
			{
				super("User requested cancel.");
			}
			
		}
		
		/* PUBLIC INTERFACES */
		/**
		 * This interface can be used to update the GUI while downloading.
		 * */
		public static interface UpdateGuiDownloadProgressCmd
		{
			
			/**
			 * This method will be fired when an update to the GUI is to be done after enough bytes
			 * have been downloaded.
			 * @param totalBytesDownloaded
			 * The total number of bytes that have been downloaded so far.
			 * */
			public void updateGuiDownloadProgress(final long totalBytesDownloaded);
			
		}
		
		/**
		 * This interface can be used to indicate that a download completed.
		 * 
		 * @author Matthew Weiler
		 * */
		public static interface DownloadCompleteCmd
		{
			
			/**
			 * This method will be fired when an update to the GUI is to be done
			 * after enough bytes
			 * have been downloaded.
			 * 
			 * @param downloadSuccessful
			 * <code>true</code> if the download was successful; <code>false</code>
			 * otherwise.
			 * */
			public void downloadComplete(final boolean downloadSuccessful);
			
		}
		
		/**
		 * This interface can be used to indicate that a download was cancelled.
		 * 
		 * @author Matthew Weiler
		 * */
		public static interface DownloadCancelledCmd
		{
			
			/**
			 * This method will be fired when an update to the GUI is to be done
			 * after enough bytes
			 * have been downloaded.
			 * 
			 * @param userRequested
			 * <code>true</code> if the download was cancelled due to a user request;
			 * <code>false</code> otherwise.
			 * */
			public void downloadCancelled(final boolean userRequested);
			
		}
		
	}
	
}
