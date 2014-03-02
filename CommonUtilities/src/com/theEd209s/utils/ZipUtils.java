package com.theEd209s.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.theEd209s.logging.Logger;

/**
 * This class will allow for the creating of zip files and the extraction of the
 * contents of zip files to a specified directory. <br />
 * <br />
 * To create a zip file, use the
 * {@link ZipUtils#zip(boolean, String, String...) zip(boolean, String,
 * String...)} method. <br />
 * <br />
 * To extract the contents of a zip file to a directory, use the
 * {@link ZipUtils#unzip(boolean, String) unzip(boolean, String)} or
 * {@link ZipUtils#unzip(boolean, String, String) unzip(boolean, String,
 * String)} methods.
 * 
 * @author Matthew Weiler
 * */
public class ZipUtils
{	
	
	/* PUBLIC METHODS */
	/**
	 * This will extract and decompress the contents of a zip file to
	 * the destination directory specified.
	 * 
	 * @param overwrite
	 *        If a file already exists where one of the files from the zip file
	 *        is to be created, should it be overwritten?
	 *        <br />
	 *        <i>it should be noted that the overwriting will not delete files
	 *        that are in the directory but not in the zip file</i>
	 * @param zipFilePath
	 *        The full path to the zip file from which to read.
	 * 
	 * @note
	 *       a new directory will be created in the
	 *       same directory as the zip file is found in and it will
	 *       have the same name as the zip file (excluding the file extension)
	 * */
	public static void unzip(final boolean overwrite, final String zipFilePath) throws IOException
	{
		ZipUtils.unzip(overwrite, zipFilePath, null);
	}
	
	/**
	 * This will extract and decompress the contents of a zip file to
	 * the destination directory specified.
	 * 
	 * @param overwrite
	 *        If a file already exists where one of the files from the zip file
	 *        is to be created, should it be overwritten?
	 *        <br />
	 *        <i>it should be noted that the overwriting will not delete files
	 *        that are in the directory but not in the zip file</i>
	 * @param zipFilePath
	 *        The full path to the zip file from which to read.
	 * @param outputDirectoryPath
	 *        The full path to the folder in which to place the contents of the
	 *        zip file. <br />
	 *        <i>if none is specified, a new directory will be created in the
	 *        same directory as the zip file is found in and it will have the
	 *        same name as the zip file (excluding the file extension)</i>
	 * */
	public static void unzip(final boolean overwrite, String zipFilePath, String outputDirectoryPath) throws IOException
	{
		if ((zipFilePath != null) && (zipFilePath.trim().length() > 0))
		{
			zipFilePath = FileUtils.cleanPath(zipFilePath);
			// Create directory with the name of the zip file.
			if ((outputDirectoryPath == null) || (outputDirectoryPath.trim().length() == 0))
			{
				final int dotIndex = zipFilePath.lastIndexOf('.');
				if (dotIndex > 0)
				{
					outputDirectoryPath = zipFilePath.substring(0, dotIndex);
				}
			}
			if ((outputDirectoryPath != null) && (outputDirectoryPath.trim().length() > 0))
			{
				BufferedInputStream bis = null;
				BufferedOutputStream bos = null;
				try
				{
					ZipUtils.logger.info("unzip", "Extracting contents of (" + zipFilePath + ") into directory (" + outputDirectoryPath + ").");
					final File temp = new File(outputDirectoryPath);
					if (!temp.exists())
					{
						if (temp.mkdirs())
						{
							ZipUtils.logger.debug("unzip", "Created folder (" + outputDirectoryPath + ").");
						}
						else
						{
							throw new IOException("Failed to create folder (" + outputDirectoryPath + ").");
						}
					}
					File destinationFile = null;
					File tmpEmptyDirectory = null;
					ZipEntry entry = null;
					// Extract entries while creating required sub-directories.
					ZipFile zipFile = null;
					try
					{
						zipFile = new ZipFile(new File(zipFilePath));
						final Enumeration<? extends ZipEntry> e = zipFile.entries();
						while (e.hasMoreElements())
						{
							entry = e.nextElement();
							destinationFile = new File(outputDirectoryPath, entry.getName());
							boolean writeFile = true;
							if ((destinationFile.isFile()) && (destinationFile.exists()))
							{
								if (overwrite)
								{
									ZipUtils.logger.info("unzip", "File (" + destinationFile.getAbsolutePath() + ") already exists, it will be overwritten.");
								}
								else
								{
									writeFile = false;
								}
							}
							if (writeFile)
							{
								// Create directories if required.
								if (!destinationFile.getParentFile().exists())
								{
									destinationFile.getParentFile().mkdirs();
								}
								// If this entry is not a directory, extract it.
								if (!entry.isDirectory())
								{
									ZipUtils.logger.debug("unzip", "Extracting file (" + destinationFile + ").");
									// Get the InputStream for current entry of the
									// zip
									// file using
									// InputStream getInputStream(Entry entry)
									// method.
									bis = new BufferedInputStream(zipFile.getInputStream(entry));
									int b = -1;
									final byte buffer[] = new byte[ZipUtils.ZIP_READING_BUFFER_SIZE];
									// Read the current entry from the zip file,
									// extract
									// it and write the extracted file.
									bos = new BufferedOutputStream(new FileOutputStream(destinationFile), ZipUtils.ZIP_READING_BUFFER_SIZE);
									while ((b = bis.read(buffer, 0, ZipUtils.ZIP_READING_BUFFER_SIZE)) != -1)
									{
										bos.write(buffer, 0, b);
									}
									// Flush the output stream and close it.
									bos.flush();
									bos.close();
									// Close the input stream.
									bis.close();
								}
								// If this entry is a directory, create it.
								else
								{
									tmpEmptyDirectory = new File(FileUtils.cleanPath(outputDirectoryPath + File.separator + entry.getName()));
									if (!tmpEmptyDirectory.exists())
									{
										if (tmpEmptyDirectory.mkdirs())
										{
											ZipUtils.logger.debug("unzip", "Creating folder (" + tmpEmptyDirectory.getAbsolutePath() + ").");
										}
										else
										{
											throw new IOException("Failed to create folder (" + tmpEmptyDirectory.getAbsolutePath() + ").");
										}
									}
								}
							}
						}
						ZipUtils.logger.info("unzip", "Successfully extracted contents of (" + zipFilePath + ") into directory (" + outputDirectoryPath + ")");
					}
					finally
					{
						if (zipFile != null)
						{
							zipFile.close();
						}
					}
				}
				catch (final IOException e)
				{
					ZipUtils.logger.error("unzip", "Failed to extract contents of (" + zipFilePath + ") into directory (" + outputDirectoryPath + ")", e);
					// Flush the output stream and close it.
					if (bos != null)
					{
						try
						{
							bos.flush();
							bos.close();
						}
						catch (final Exception e1)
						{
							ZipUtils.logger.error("unzip", "Failed to flush and close the output stream: ", e1);
						}
					}
					// Close the input stream.
					if (bis != null)
					{
						try
						{
							bis.close();
						}
						catch (final Exception e1)
						{
							ZipUtils.logger.error("unzip", "Failed to close the input stream: ", e1);
						}
					}
					throw e;
				}
			}
			else
			{
				throw new IOException("Invalid output directory.");
			}
		}
		else
		{
			throw new IOException("Invalid source zip file.");
		}
	}
	
	/**
	 * This will create a new zip file, gathering and compressing the
	 * contents of the source directory into this new zip file.
	 * 
	 * @param overwrite
	 *        If a file already exists where the zip file is to be created,
	 *        should
	 *        it be overwritten?
	 * @param zipFilePath
	 *        The full path to the zip file. <br />
	 *        <i>if none is specified, a new zip file will be created with the
	 *        name
	 *        of the 1st files parent directory and within that parent directory
	 *        (adding ".zip" to the file name)</i>
	 * @param sourceFilePaths
	 *        The list of full file paths to add to the zip file. <br />
	 *        <i>this list of files do not have to reside in the same folder</i>
	 * @note
	 *       If the zipFilePath file path is found within one of the
	 *       sourceFilePaths, no entry
	 *       will be put into the zip file for itself as this would make no
	 *       sense.
	 * */
	public static void zip(final boolean overwrite, String zipFilePath, final String... sourceFilePaths) throws IOException
	{
		if ((sourceFilePaths != null) && (sourceFilePaths.length > 0))
		{
			// If no zip file path was specified, then extract the parent
			// directory path from the 1st file and append ".zip" to it
			// and use that as the zip file path.
			if ((zipFilePath == null) || (zipFilePath.trim().length() == 0))
			{
				File firstSourceFile = null;
				int sourceFileIndex = 0;
				while (((firstSourceFile == null) || (!firstSourceFile.exists())) && (sourceFilePaths.length > sourceFileIndex))
				{
					firstSourceFile = new File(FileUtils.cleanPath(sourceFilePaths[sourceFileIndex]));
					sourceFileIndex++;
				}
				if ((firstSourceFile != null) && (firstSourceFile.exists()))
				{
					// If there is more than just 1 file being added to the
					// zip, then extract the parent folder of the 1st source
					// file.
					if (sourceFilePaths.length > 1)
					{
						firstSourceFile = firstSourceFile.getParentFile();
					}
					if (firstSourceFile != null)
					{
						// If the first source file is a directory, just add
						// ".zip" to it's full path.
						if (firstSourceFile.isDirectory())
						{
							zipFilePath = firstSourceFile.getAbsolutePath() + ".zip";
						}
						// If the first source file is a file, replace it's
						// extension with "zip".
						else if (firstSourceFile.isFile())
						{
							final int dotIndex = firstSourceFile.getAbsolutePath().lastIndexOf('.');
							if (dotIndex > 0)
							{
								zipFilePath = firstSourceFile.getAbsolutePath().substring(0, dotIndex);
							}
							zipFilePath = firstSourceFile.getAbsolutePath() + ".zip";
						}
					}
				}
			}
			else
			{
				zipFilePath = FileUtils.cleanPath(zipFilePath);
			}
			// If a zip file path was specified or calculated, then continue.
			if ((zipFilePath != null) && (zipFilePath.trim().length() > 0))
			{
				boolean createZipFile = true;
				if ((new File(zipFilePath)).exists())
				{
					createZipFile = overwrite;
				}
				if (createZipFile)
				{
					ZipUtils.logger.info("zip", "Starting to create zip file (" + zipFilePath + ").");
					if ((new File(zipFilePath)).exists())
					{
						ZipUtils.logger.warning("zip", "A zip file already exists with this name, it will be overwritten.");
					}
					if (!zipFilePath.endsWith(".zip"))
					{
						ZipUtils.logger.warning("zip", "Zip file being created does not end with (.zip), this may be confusing.");
					}
					ZipOutputStream zout = null;
					try
					{
						// create object of ZipOutputStream from
						// FileOutputStream
						zout = new ZipOutputStream(new FileOutputStream(zipFilePath));
						File tmpFile = null;
						String sourceDir = null;
						String sourceFileRoot = null;
						for (int n = 0; n < sourceFilePaths.length; n++)
						{
							sourceDir = FileUtils.cleanPath(sourceFilePaths[n]);
							if ((sourceDir != null) && (sourceDir.trim().length() > 0))
							{
								tmpFile = new File(sourceDir);
								if ((tmpFile != null) && (tmpFile.exists()))
								{
									sourceFileRoot = null;
									if (tmpFile.getParentFile() != null)
									{
										sourceFileRoot = tmpFile.getParentFile().getAbsolutePath();
									}
									if (tmpFile.isDirectory())
									{
										ZipUtils.zip_addDirectory(zipFilePath, zout, tmpFile, sourceFileRoot);
									}
									else if (tmpFile.isFile())
									{
										ZipUtils.zip_addFile(zipFilePath, zout, tmpFile, sourceFileRoot);
									}
								}
								else
								{
									throw new IOException("File does not exist: " + sourceDir);
								}
							}
							else
							{
								throw new IOException("Invalid input file.");
							}
						}
						// Close the ZipOutputStream.
						zout.close();
						ZipUtils.logger.info("zip", "Successfully created zip file (" + zipFilePath + ").");
					}
					catch (final IOException e)
					{
						ZipUtils.logger.error("zip", "Failed to create zip file (" + zipFilePath + ").", e);
						// Close the ZipOutputStream.
						if (zout != null)
						{
							try
							{
								zout.close();
							}
							catch (final Exception e1)
							{
								ZipUtils.logger.error("zip", "Failed to close the zip output stream: ", e1);
							}
						}
						throw e;
					}
				}
				else
				{
					ZipUtils.logger.warning("zip", "A zip file already exists with this name, aborting process.");
					throw new IOException("A zip file already exists with this name, aborting process.");
				}
			}
			// If no zip file path was specified or calculated, then alert of
			// this.
			else
			{
				throw new IOException("Invalid zip file path.");
			}
		}
		else
		{
			throw new IOException("Invalid source files.");
		}
	}
	
	/**
	 * This method will extract specific files from the specified zipped {@link File}.
	 * 
	 * @param overwrite
	 * <code>true</code> to enable overwriting of existing {@link File}s; <code>false</code>
	 * otherwise.
	 * @param zipFile
	 * The {@link File} from which to extract.
	 * @param outputDirectory
	 * The {@link File} representing the directory to put the files into.
	 * @param flatten
	 * <code>true</code> if the files specified should all be flattened to the specified output
	 * directory.
	 * <br />
	 * <i>if <code>true</code> and more than one file in the specified list have the same
	 * name, either the 1st or the last file will be kept (depending on the overwrite flag)</i>
	 * @param relativeFilePaths
	 * The list of relative file paths (within the zip file) to be extracted.
	 * <br />
	 * <i>if any of the files specified represents a directory, it will be created but it's
	 * contents will <b>NOT</b> be recursively extracted</i>
	 * 
	 * @throws IOException
	 * If the extraction fails, this should explain why.
	 * */
	public static void extractFiles(final boolean overwrite, final File zippedFile, final File outputDirectory, final boolean flatten, final String... relativeFilePaths) throws IOException
	{
		if ((zippedFile != null) && zippedFile.exists() && zippedFile.isFile())
		{
			if ((outputDirectory != null) && ((outputDirectory.exists() && outputDirectory.isDirectory()) || (outputDirectory.mkdirs() && outputDirectory.isDirectory())))
			{
				if ((relativeFilePaths != null) && (relativeFilePaths.length > 0))
				{
					BufferedInputStream bis = null;
					BufferedOutputStream bos = null;
					try
					{
						ZipUtils.logger.info("extractFiles", "Extracting some files from (" + zippedFile.getAbsolutePath() + ") into directory (" + outputDirectory.getAbsolutePath() + ").");
						File destinationFile = null;
						File tmpEmptyDirectory = null;
						// Extract entries while creating required sub-directories.
						ZipFile zipFile = null;
						try
						{
							zipFile = new ZipFile(zippedFile);
							ZipEntry zipEntry = null;
							for (String relativeFilePath : relativeFilePaths)
							{
								relativeFilePath = relativeFilePath.replace("\\", "/");
								zipEntry = zipFile.getEntry(relativeFilePath);
								if (zipEntry != null)
								{
									if (flatten)
									{
										relativeFilePath = zipEntry.getName();
									}
									destinationFile = new File(outputDirectory, relativeFilePath);
									if (overwrite || (!destinationFile.exists()))
									{
										// If this entry is not a directory, extract it.
										if (!zipEntry.isDirectory())
										{
											ZipUtils.logger.debug("extractFiles", "Extracting file (" + destinationFile + ").");
											// Get the InputStream for current entry of the
											// zip
											// file using
											// InputStream getInputStream(Entry entry)
											// method.
											bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
											int b = -1;
											final byte buffer[] = new byte[ZipUtils.ZIP_READING_BUFFER_SIZE];
											// Read the current entry from the zip file,
											// extract
											// it and write the extracted file.
											bos = new BufferedOutputStream(new FileOutputStream(destinationFile), ZipUtils.ZIP_READING_BUFFER_SIZE);
											while ((b = bis.read(buffer, 0, ZipUtils.ZIP_READING_BUFFER_SIZE)) != -1)
											{
												bos.write(buffer, 0, b);
											}
											// Flush the output stream and close it.
											bos.flush();
											bos.close();
											// Close the input stream.
											bis.close();
										}
										// If this entry is a directory, create it.
										else
										{
											tmpEmptyDirectory = new File(FileUtils.cleanPath(outputDirectory.getAbsolutePath() + File.separator + zipEntry.getName()));
											if (!tmpEmptyDirectory.exists())
											{
												if (tmpEmptyDirectory.mkdirs())
												{
													ZipUtils.logger.debug("extractFiles", "Creating folder (" + tmpEmptyDirectory.getAbsolutePath() + ").");
												}
												else
												{
													throw new IOException("Failed to create folder (" + tmpEmptyDirectory.getAbsolutePath() + ").");
												}
											}
										}
									}
								}
								else
								{
									ZipUtils.logger.warning("extractFiles", "Nothing was found for file entry: " + relativeFilePath);
								}
							}
							ZipUtils.logger.info("extractFiles", "Successfully extracted specified files from (" + zippedFile.getAbsolutePath() + ") into directory (" + outputDirectory.getAbsolutePath() + ")");
						}
						finally
						{
							if (zipFile != null)
							{
								zipFile.close();
							}
						}
					}
					catch (final IOException e)
					{
						ZipUtils.logger.error("extractFiles", "Failed to extract specified files from (" + zippedFile.getAbsolutePath() + ") into directory (" + outputDirectory.getAbsolutePath() + ")", e);
						// Flush the output stream and close it.
						if (bos != null)
						{
							try
							{
								bos.flush();
								bos.close();
							}
							catch (final Exception e1)
							{
								ZipUtils.logger.error("extractFiles", "Failed to flush and close the output stream: ", e1);
							}
						}
						// Close the input stream.
						if (bis != null)
						{
							try
							{
								bis.close();
							}
							catch (final Exception e1)
							{
								ZipUtils.logger.error("extractFiles", "Failed to close the input stream: ", e1);
							}
						}
						throw e;
					}
				}
				else
				{
					throw new IOException("Failed to specify any relative file paths to extract.");
				}
			}
			else
			{
				throw new IOException("Failed to specify valid output directory.");
			}
		}
		else
		{
			throw new IOException("Failed to specify the input ZIP file.");
		}
	}
	
	/**
	 * This method will extract specific files & folders from the specified zipped {@link File}.
	 * 
	 * @param overwrite
	 * <code>true</code> to enable overwriting of existing {@link File}s; <code>false</code>
	 * otherwise.
	 * @param zippedFile
	 * The {@link File} from which to extract.
	 * @param outputDirectory
	 * The {@link File} representing the directory to put the files into.
	 * @param relativePathToTrim
	 * The relative path component that should be trimmed from each relative file path, when
	 * creating the files on the file-system.
	 * <br />
	 * <i>if <code>null</code> nothing will be trimmed</i>
	 * <br />
	 * <i>if flatten is <code>true</code>, this will be ignored</i>
	 * @param flatten
	 * <code>true</code> if the files specified should all be flattened to the specified output
	 * directory.
	 * <br />
	 * <i>if <code>true</code> and more than one file in the specified list have the same
	 * name, either the 1st or the last file will be kept (depending on the overwrite flag)</i>
	 * @param relativeFilePaths
	 * The list of relative file paths (within the zip file) to be extracted.
	 * <br />
	 * <i>if any of the files specified represents a directory, its entire contents will be
	 * recursively extracted</i>
	 * 
	 * @throws IOException
	 * If the extraction fails, this should explain why.
	 * */
	public static void extractFilesDirs(final boolean overwrite, final File zippedFile, final File outputDirectory, final String relativePathToTrim, final boolean flatten, final String... relativeFilePaths) throws IOException
	{
		if ((zippedFile != null) && zippedFile.exists() && zippedFile.isFile())
		{
			if (outputDirectory != null)
			{
				BufferedInputStream bis = null;
				BufferedOutputStream bos = null;
				try
				{
					ZipUtils.logger.info("extractFilesDirs", "Extracting contents of (" + zippedFile.getAbsolutePath() + ") into directory (" + outputDirectory.getAbsolutePath() + ").");
					if (!outputDirectory.exists())
					{
						if (outputDirectory.mkdirs())
						{
							ZipUtils.logger.debug("extractFilesDirs", "Created folder (" + outputDirectory.getAbsolutePath() + ").");
						}
						else
						{
							throw new IOException("Failed to create folder (" + outputDirectory.getAbsolutePath() + ").");
						}
					}
					File destinationFile = null;
					File tmpEmptyDirectory = null;
					ZipEntry entry = null;
					// Extract entries while creating required sub-directories.
					ZipFile zipFile = null;
					try
					{
						final boolean relativePathTrimming = (relativePathToTrim != null) && (relativePathToTrim.trim().length() > 0);
						zipFile = new ZipFile(zippedFile);
						final Enumeration<? extends ZipEntry> e = zipFile.entries();
						while (e.hasMoreElements())
						{
							entry = e.nextElement();
							final String entryName_inJar = entry.getName();
							boolean foundIt = false;
							for (String relativeFilePath : relativeFilePaths)
							{
								if (relativeFilePath.equals(entryName_inJar) || entryName_inJar.startsWith(relativeFilePath + "/"))
								{
									foundIt = true;
									break;
								}
							}
							if (foundIt)
							{
								String entryName_onFileSystem = entryName_inJar;
								if (relativePathTrimming && entryName_onFileSystem.startsWith(relativePathToTrim))
								{
									entryName_onFileSystem = entryName_onFileSystem.substring(relativePathToTrim.length());
									if (entryName_onFileSystem.startsWith("/"))
									{
										entryName_onFileSystem = entryName_onFileSystem.substring(1);
									}
								}
								destinationFile = new File(outputDirectory, entryName_onFileSystem);
								boolean writeFile = true;
								if ((destinationFile.isFile()) && (destinationFile.exists()))
								{
									if (overwrite)
									{
										ZipUtils.logger.info("extractFilesDirs", "File (" + destinationFile.getAbsolutePath() + ") already exists, it will be overwritten.");
									}
									else
									{
										writeFile = false;
									}
								}
								if (writeFile)
								{
									// Create directories if required.
									if (!destinationFile.getParentFile().exists())
									{
										destinationFile.getParentFile().mkdirs();
									}
									// If this entry is not a directory, extract it.
									if (!entry.isDirectory())
									{
										ZipUtils.logger.debug("extractFilesDirs", "Extracting file (" + destinationFile + ").");
										// Get the InputStream for current entry of the
										// zip
										// file using
										// InputStream getInputStream(Entry entry)
										// method.
										bis = new BufferedInputStream(zipFile.getInputStream(entry));
										int b = -1;
										final byte buffer[] = new byte[ZipUtils.ZIP_READING_BUFFER_SIZE];
										// Read the current entry from the zip file,
										// extract
										// it and write the extracted file.
										bos = new BufferedOutputStream(new FileOutputStream(destinationFile), ZipUtils.ZIP_READING_BUFFER_SIZE);
										while ((b = bis.read(buffer, 0, ZipUtils.ZIP_READING_BUFFER_SIZE)) != -1)
										{
											bos.write(buffer, 0, b);
										}
										// Flush the output stream and close it.
										bos.flush();
										bos.close();
										// Close the input stream.
										bis.close();
									}
									// If this entry is a directory, create it.
									else
									{
										tmpEmptyDirectory = new File(outputDirectory, entryName_onFileSystem);
										if (!tmpEmptyDirectory.exists())
										{
											if (tmpEmptyDirectory.mkdirs())
											{
												ZipUtils.logger.debug("extractFilesDirs", "Creating folder (" + tmpEmptyDirectory.getAbsolutePath() + ").");
											}
											else
											{
												throw new IOException("Failed to create folder (" + tmpEmptyDirectory.getAbsolutePath() + ").");
											}
										}
									}
								}
							}
						}
						ZipUtils.logger.info("extractFilesDirs", "Successfully extracted specified contents of (" + zippedFile.getAbsolutePath() + ") into directory (" + outputDirectory.getAbsolutePath() + ")");
					}
					finally
					{
						if (zipFile != null)
						{
							zipFile.close();
						}
					}
				}
				catch (final IOException e)
				{
					ZipUtils.logger.error("extractFilesDirs", "Failed to extract specified contents of (" + zippedFile.getAbsolutePath() + ") into directory (" + outputDirectory.getAbsolutePath() + ")", e);
					// Flush the output stream and close it.
					if (bos != null)
					{
						try
						{
							bos.flush();
							bos.close();
						}
						catch (final Exception e1)
						{
							ZipUtils.logger.error("extractFilesDirs", "Failed to flush and close the output stream: ", e1);
						}
					}
					// Close the input stream.
					if (bis != null)
					{
						try
						{
							bis.close();
						}
						catch (final Exception e1)
						{
							ZipUtils.logger.error("extractFilesDirs", "Failed to close the input stream: ", e1);
						}
					}
					throw e;
				}
			}
			else
			{
				throw new IOException("Invalid output directory.");
			}
		}
		else
		{
			throw new IOException("Invalid source zip file.");
		}
	}
	
	/* PRIVATE METHODS */
	/**
	 * This will add the specified directory to the specified
	 * {@link ZipOutputStream}.
	 * 
	 * @param zipFilePath
	 *        This is the absolute path of the zip file being created. <br />
	 *        <i>this is needed so that we won&#39;t add the zip file itself
	 *        into itself if it was passed as a parameter</i>
	 * @param zout
	 *        The {@link zipOutputStream} to which to write.
	 * @param sourceFile
	 *        The input directory {@link File} to write to the zip file.
	 * @param sourceFileRoot
	 *        The original parent directory for this {@link File}s zipping
	 *        ancestor.
	 * */
	private static void zip_addDirectory(final String zipFilePath, final ZipOutputStream zout, final File sourceFile, final String sourceFileRoot) throws IOException
	{
		if (zout != null)
		{
			if ((sourceFile != null) && (sourceFile.exists()))
			{
				// Get sub-folder/files list.
				final File[] files = sourceFile.listFiles();
				// If this folder does contain children, then we don't
				// have to explicitly add it; it will be implicitly added
				// when it's children are added.
				if ((files != null) && (files.length > 0))
				{
					// Loop over all files in the directory and process each
					// one.
					for (int i = 0; i < files.length; i++)
					{
						// If the file is directory, call the function
						// recursively.
						if (files[i].isDirectory())
						{
							ZipUtils.zip_addDirectory(zipFilePath, zout, files[i], sourceFileRoot);
						}
						// If the File is a file, call the zip_addFile()
						// function.
						else if (files[i].isFile())
						{
							ZipUtils.zip_addFile(zipFilePath, zout, files[i], sourceFileRoot);
						}
						// If the File is a directory, call the
						// zip_addDirectory() function.
						else
						{
							throw new IOException("Unknown type of file: " + files[i].getName());
						}
					}
				}
				// If this folder doesn't contain any children, then we
				// have to explicitly add it.
				else
				{
					// Determine what the relative path is for this directory.
					String relativePath = FileUtils.cleanPath(sourceFile.getName(), File.separator);
					final String absolutePath = sourceFile.getAbsolutePath();
					// If the absolute path of the file being added contains the sourceFileRoot,
					// then extract it's relative path from the absolute path relative to the
					// sourceFileRoot.
					if ((sourceFileRoot != null) && (absolutePath.startsWith(sourceFileRoot)))
					{
						relativePath = absolutePath.substring(sourceFileRoot.length());
					}
					// Ensure that we remove all leading File.separator characters.
					while (relativePath.startsWith(File.separator))
					{
						relativePath = relativePath.substring(File.separator.length());
					}
					// Ensure that we remove all trailing File.separator characters.
					while (relativePath.endsWith("\\") || relativePath.endsWith("/"))
					{
						// If the last character is not the 1st character, continue...
						if ((relativePath.length() - 1) > 0)
						{
							relativePath = relativePath.substring(0, relativePath.length() - 1);
						}
						// If the last character is the 1st character, set the
						// relative path to empty...
						else
						{
							relativePath = "";
						}
					}
					// If a relative path is set, then add it to the zip file.
					if (relativePath.length() > 0)
					{
						relativePath = relativePath + "/";
						ZipUtils.logger.debug("zip_addDirectory", " Adding directory: " + relativePath);
						zout.putNextEntry(new ZipEntry(relativePath));
						zout.closeEntry();
					}
				}
			}
			else
			{
				throw new IOException("Invalid file specified.");
			}
		}
		else
		{
			throw new IOException("No zip output stream specified.");
		}
	}
	
	/**
	 * This will add the specified {@link File} to the specified
	 * {@link ZipOutputStream}.
	 * 
	 * @param zipFilePath
	 *        This is the absolute path of the zip file being created. <br />
	 *        <i>this is needed so that we won&#39;t add the zip file itself
	 *        into itself if it was passed as a parameter</i>
	 * @param zout
	 *        The {@link zipOutputStream} to which to write.
	 * @param sourceFile
	 *        The input {@link File} to write to the zip file.
	 * @param sourceFileRoot
	 *        The original parent directory for this {@link File}s zipping
	 *        ancestor.
	 * */
	private static void zip_addFile(final String zipFilePath, final ZipOutputStream zout, final File sourceFile, final String sourceFileRoot) throws IOException
	{
		if (zout != null)
		{
			if ((sourceFile != null) && (sourceFile.exists()))
			{
				if (!(new File(zipFilePath)).equals(sourceFile))
				{
					FileInputStream fis = null;
					try
					{
						String relativePath = FileUtils.cleanPath(sourceFile.getName(), File.separator);
						// If the absolute path of the file being added contains the sourceFileRoot,
						// then extract it's relative path from the absolute path relative to the
						// sourceFileRoot.
						final String absolutePath = sourceFile.getAbsolutePath();
						if ((sourceFileRoot != null) && (absolutePath.startsWith(sourceFileRoot)))
						{
							relativePath = absolutePath.substring(sourceFileRoot.length());
						}
						// Ensure that we remove all leading File.separator characters.
						while (relativePath.startsWith(File.separator))
						{
							relativePath = relativePath.substring(File.separator.length());
						}
						ZipUtils.logger.debug("zip_addFile", "Adding file: " + relativePath);
						// Create byte buffer.
						final byte[] buffer = new byte[ZipUtils.ZIP_WRITING_BUFFER_SIZE];
						// Create object of FileInputStream.
						fis = new FileInputStream(sourceFile);
						zout.putNextEntry(new ZipEntry(relativePath));
						try
						{
							// After creating entry in the zip file, actually
							// write the
							// file.
							int length;
							while ((length = fis.read(buffer)) > 0)
							{
								zout.write(buffer, 0, length);
							}
							// After writing the file to ZipOutputStream, use
							// void
							// closeEntry()
							// method of ZipOutputStream class to close the
							// current
							// entry and
							// position the stream to write the next entry.
							zout.closeEntry();
						}
						catch (final IOException ioe)
						{
							zout.closeEntry();
							throw ioe;
						}
						// Close the input stream.
						fis.close();
					}
					catch (final IOException ioe)
					{
						// Close the input stream.
						if (fis != null)
						{
							try
							{
								fis.close();
							}
							catch (final Exception e)
							{
								ZipUtils.logger.error("zip_addFile", "Failed to close the input stream: ", e);
							}
						}
						throw ioe;
					}
				}
				// If the file being added is in fact the zip file being written
				// to, don't add it.
				else
				{
					ZipUtils.logger.warning("zip_addFile", "The file currently being archived/zipped to cannot be included in the zip; this just doesn't make sense.");
				}
			}
			else
			{
				throw new IOException("Invalid file specified.");
			}
		}
		else
		{
			throw new IOException("No zip output stream specified.");
		}
	}
	
	/* PRIVATE CONSTANTS */
	/**
	 * This will be used to log any activity in the {@link ZipUtils} class.
	 * */
	private static final Logger logger = new Logger(ZipUtils.class.getName());
	
	/* PRIVATE VARIABLES */
	/**
	 * This will be used when reading from a zip file.
	 * This number denotes the number of bytes to read in a
	 * single operation.
	 * */
	private final static int ZIP_READING_BUFFER_SIZE = 1024;
	/**
	 * This will be used when writing to a zip file.
	 * This number denotes the number of bytes to write in a
	 * single operation.
	 * */
	private final static int ZIP_WRITING_BUFFER_SIZE = 1024;
	
}
