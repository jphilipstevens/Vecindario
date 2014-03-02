package com.theEd209s.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.theEd209s.logging.Logger;

/**
 * This class contains several methods for working with {@link File}s.
 * 
 * @author Matthew Weiler
 * */
public class FileUtils
{	
	
	/* PUBLIC METHODS */
	/**
	 * This will clean the passed-in file path ensuring that the correct file
	 * separators are used and that we don't have any double instances of
	 * file-path-separators.
	 * 
	 * @note The {@link File#separator} will be used as the file-path-separator.
	 * 
	 * @param inputPath
	 *            The input file path.
	 * 
	 * @return The cleaned file path.
	 * */
	public static String cleanPath(final String inputPath)
	{
		return FileUtils.cleanPath(inputPath, null);
	}
	
	/**
	 * This will clean the passed-in file path ensuring that the correct file
	 * separators are used and that we don't have any double instances of
	 * file-path-separators.
	 * 
	 * @param inputPath
	 *            The input file path.
	 * @param filePathSeparator
	 *            The file path separator for this system. <br />
	 *            The {@link File#separator} string can be used if you are
	 *            unsure.
	 * 
	 * @return The cleaned file path.
	 * */
	public static String cleanPath(final String inputPath, String filePathSeparator)
	{
		String outputPath = inputPath;
		if ((filePathSeparator == null) || (filePathSeparator.trim().length() == 0))
		{
			filePathSeparator = File.separator;
		}
		String goodSeparator = filePathSeparator;
		String badSeparator = null;
		// Determine which file-path-separator should be the
		// "bad" one and later be replaced.
		if ("/".compareTo(goodSeparator) == 0)
		{
			badSeparator = "\\";
		}
		else
		{
			badSeparator = "/";
		}
		// Replace all instances of the "bad" file-path-separator
		// characters with the "good" file-path-separator.
		int badCharIndex = -1;
		// This variable will be used as a safety check in-case the
		// filePathSeparator input parameter containing the default
		// bad separator character "/"; this will ensure that we
		// won't get into an endless loop.
		int lastEndingIndex = 0;
		// Loop over all characters in the output path string and
		// replace any instances of the bad separator character
		// with the good.
		while ((badCharIndex = outputPath.indexOf(badSeparator, lastEndingIndex)) >= 0)
		{
			// If the bad separator character is found at the first
			// index of the string...
			if (badCharIndex == 0)
			{
				// If there are any valid characters after the bad
				// separator character found, then keep those characters
				// too.
				if ((badSeparator.length()) < outputPath.length())
				{
					outputPath = goodSeparator + outputPath.substring(badSeparator.length());
				}
				// If there are no valid character found after the bad
				// separator character, then just add the good separator
				// character in place of the bad.
				else
				{
					outputPath = goodSeparator;
				}
			}
			// If the bad separator character is found beyond the
			// first index of the string...
			else
			{
				// If there are any valid characters after the bad
				// separator character found, then keep those characters
				// too.
				if ((badCharIndex + badSeparator.length()) < outputPath.length())
				{
					outputPath = outputPath.substring(0, badCharIndex) + goodSeparator + outputPath.substring(badCharIndex + (badSeparator.length()));
				}
				// If there are no valid character found after the bad
				// separator character, then just add the good separator
				// character in place of the bad.
				else
				{
					outputPath = outputPath.substring(0, badCharIndex) + goodSeparator;
				}
			}
			lastEndingIndex = badCharIndex + goodSeparator.length();
		}
		int doubleFoundAt = -1;
		lastEndingIndex = 0;
		// Find if there is an double instance of a "good"
		// file-path-separator character and if found, remove the
		// 1st instance.
		while ((doubleFoundAt = outputPath.indexOf(goodSeparator + goodSeparator, lastEndingIndex)) >= 0)
		{
			// If the double file-path-separators are at the very
			// start of the string...
			if (doubleFoundAt == 0)
			{
				outputPath = outputPath.substring(goodSeparator.length());
			}
			// If the double file-path-separators are not at the very
			// start of the string...
			else
			{
				// Since we know that they will be 1 more character
				// after the 1st file-path-separator character
				// (the 2nd file-path-separator character), we don't
				// have to do any additional checking.
				outputPath = outputPath.substring(0, doubleFoundAt) + outputPath.substring(doubleFoundAt + goodSeparator.length());
			}
			lastEndingIndex = doubleFoundAt + goodSeparator.length();
		}
		// Return the cleaned input file path.
		return outputPath;
	}
	
	/**
	 * This method will ensure that the inputPath specified does not end with a
	 * slash.
	 * 
	 * @param inputPath
	 *            The input path to clean.
	 * 
	 * @return The cleaned path.
	 * */
	public static String removeTrailingSlash(String inputPath)
	{
		if ((inputPath != null) && (inputPath.trim().length() > 0))
		{
			while ((inputPath != null) && (inputPath.endsWith("/") || inputPath.endsWith("\\")))
			{
				if (inputPath.length() > 1)
				{
					inputPath = inputPath.substring(0, inputPath.length() - 1);
				}
				else
				{
					inputPath = "";
				}
			}
		}
		return inputPath;
	}
	
	/**
	 * This will recursively delete all files within the given
	 * <b>deletePath</b>. <br />
	 * <i>if the given <b>deletePath</b> is a <code>file</code> and not a
	 * directory, it will just be deleted</i>
	 * 
	 * @param deletePath
	 *            The full path of the file/directory to delete.
	 * @param deleteOnJvmExit
	 *            <code>true</code> if the delete should be recursive; otherwise
	 *            <code>false</code> to delete immediately.
	 * 
	 * @return If the deletion was set for now (not on JVM exit) and if the
	 *         deletion was successful, <code>true</code> will be returned;
	 *         otherwise <code>false</code>.
	 * */
	public static boolean recursiveDelete(final String deletePath, final boolean deleteOnJvmExit)
	{
		if ((deletePath != null) && (deletePath.trim().length() > 0))
		{
			return FileUtils.recursiveDelete(new File(deletePath), deleteOnJvmExit);
		}
		return false;
	}
	
	/**
	 * This will recursively delete all files within the given
	 * <b>deleteFile</b>. <br />
	 * <i>if the given <b>deleteFile</b> is a <code>file</code> and not a
	 * directory, it will just be deleted</i>
	 * 
	 * @param deleteFile
	 *            The {@link File} object to delete.
	 * @param deleteOnJvmExit
	 *            <code>true</code> if the delete should be recursive; otherwise
	 *            <code>false</code> to delete immediately.
	 * 
	 * @return If the deletion was set for now (not on JVM exit) and if the
	 *         deletion was successful, <code>true</code> will be returned;
	 *         otherwise <code>false</code>.
	 * */
	public static boolean recursiveDelete(final File deleteFile, final boolean deleteOnJvmExit)
	{
		boolean allDeleted = false;
		if ((deleteFile != null) && deleteFile.exists())
		{
			allDeleted = true;
			if (deleteFile.isFile())
			{
				if (deleteOnJvmExit)
				{
					deleteFile.deleteOnExit();
				}
				else
				{
					allDeleted = allDeleted && deleteFile.delete();
				}
			}
			else
			{
				final File[] filesList = deleteFile.listFiles();
				if (filesList != null)
				{
					for (int n = 0; n < filesList.length; n++)
					{
						allDeleted = allDeleted && FileUtils.recursiveDelete(filesList[n], deleteOnJvmExit);
					}
				}
				if (deleteOnJvmExit)
				{
					deleteFile.deleteOnExit();
				}
				else
				{
					allDeleted = allDeleted && deleteFile.delete();
				}
			}
		}
		return allDeleted;
	}
	
	/**
	 * This method will copy the specified file into the destination directory
	 * path.
	 * 
	 * @param sourceFilePath
	 *            The full path to the source file.
	 * @param destinationDirectoryPath
	 *            The full path to the destination directory.
	 * 
	 * @return <code>true</code> if the copy was successful; <code>false</code>
	 *         otherwise.
	 * */
	public static boolean copyFile(final String sourceFilePath, final String destinationDirectoryPath)
	{
		if ((sourceFilePath != null) && (sourceFilePath.trim().length() > 0) && (destinationDirectoryPath != null) && (destinationDirectoryPath.trim().length() > 0))
		{
			final File inFile = new File(sourceFilePath);
			if ((inFile != null) && inFile.exists() && inFile.isFile())
			{
				File outFile = new File(destinationDirectoryPath);
				if ((outFile != null) && (!outFile.exists()))
				{
					outFile.mkdirs();
				}
				outFile = new File(outFile, inFile.getName());
				if (outFile != null)
				{
					if (outFile.exists() && outFile.isFile())
					{
						outFile.delete();
					}
					InputStream in = null;
					FileOutputStream out = null;
					try
					{
						in = new FileInputStream(sourceFilePath);
						out = new FileOutputStream(outFile.getAbsolutePath());
						byte[] buf = new byte[1024];
						int len;
						while ((len = in.read(buf)) > 0)
						{
							out.write(buf, 0, len);
						}
						out.flush();
						return true;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						if (out != null)
						{
							try
							{
								out.flush();
								out.close();
							}
							catch (IOException ioe)
							{
								ioe.printStackTrace();
							}
						}
						if (in != null)
						{
							try
							{
								in.close();
							}
							catch (IOException ioe)
							{
								ioe.printStackTrace();
							}
						}
					}
					
				}
			}
			else
			{
				FileUtils.logger.warning("Not a valid file: " + sourceFilePath);
			}
		}
		return false;
	}
	
	/**
	 * This method will copy the specified file into the destination directory
	 * path.
	 * 
	 * @param sourceFileStream
	 *            The {@link InputStream} of the source file.
	 * @param destinationFileStream
	 *            The {@link OutputStream} of the destination file.
	 * 
	 * @return <code>true</code> if the copy was successful; <code>false</code>
	 *         otherwise.
	 * @throws IOException
	 *             if any {@link IOException}s occur during the copy.
	 * */
	public static boolean copyFile(final InputStream sourceFileStream, final OutputStream destinationFileStream) throws IOException
	{
		if ((sourceFileStream != null) && (destinationFileStream != null))
		{
			byte[] buf = new byte[1024];
			int len;
			while ((len = sourceFileStream.read(buf)) > 0)
			{
				destinationFileStream.write(buf, 0, len);
			}
			destinationFileStream.flush();
			return true;
		}
		return false;
	}
	
	/**
	 * This method will return the byte size of the specified file/directory.
	 * 
	 * @param filePath
	 *            The full path to the file/directory.
	 * 
	 * @return The byte size of the specified file/directory.
	 * */
	public static final long getFileSize(final String filePath)
	{
		long fileSize = 0L;
		if ((filePath != null) && (filePath.trim().length() > 0))
		{
			return FileUtils.getFileSize(new File(filePath));
		}
		return fileSize;
	}
	
	/**
	 * This method will return the byte size of the specified {@link File}.
	 * 
	 * @param file
	 *            The {@link File}.
	 * 
	 * @return The byte size of the specified {@link File}.
	 * */
	public static final long getFileSize(final File file)
	{
		if ((file != null) && (file.exists()))
		{
			if (file.isDirectory())
			{
				long directorySize = 0L;
				final File[] subFiles = file.listFiles();
				for (int n = 0; n < subFiles.length; n++)
				{
					directorySize += FileUtils.getFileSize(subFiles[n]);
				}
				return directorySize;
			}
			else if (file.isFile())
			{
				return file.length();
			}
		}
		return 0L;
	}
	
	/**
	 * This method will convert a number of bytes into a human readable
	 * {@link String}. <br />
	 * <br />
	 * <i> <u>Examples</u>: <br />
	 * 421 => 421B <br />
	 * 1024 => 1KB <br />
	 * 1034 => 1KB <br />
	 * 65478 => 63KB <br />
	 * 107374182 => 102MB <br />
	 * 107374182654 => 100GB <br />
	 * 107374182654000 => 97TB <br />
	 * 107374182654000000 => 95PB </i>
	 * 
	 * @param fileByteSize
	 *            The number of bytes.
	 * @param separateParts
	 *            If there should be a space between the number and the file
	 * 
	 * @return The human readable {@link String} representation of the number of
	 *         bytes.
	 * */
	public static String getHumanFileSize(long fileByteSize, final boolean separateParts)
	{
		int sizeThreshold = 0;
		while ((sizeThreshold < 8) && (fileByteSize >= 1024))
		{
			sizeThreshold++;
			fileByteSize = fileByteSize / 1024;
		}
		String separateChar = "";
		if (separateParts)
		{
			separateChar = " ";
		}
		String humanFileSize = fileByteSize + "";
		switch (sizeThreshold)
		{
			case 0:
				humanFileSize += separateChar + "B";
				break;
			case 1:
				humanFileSize += separateChar + "KB";
				break;
			case 2:
				humanFileSize += separateChar + "MB";
				break;
			case 3:
				humanFileSize += separateChar + "GB";
				break;
			case 4:
				humanFileSize += separateChar + "TB";
				break;
			case 5:
				humanFileSize += separateChar + "PB";
				break;
			case 6:
				humanFileSize += separateChar + "EB";
				break;
			case 7:
				humanFileSize += separateChar + "ZB";
				break;
			case 8:
				humanFileSize += separateChar + "YB";
				break;
		}
		return humanFileSize;
	}

	/**
	 * This method will replace any system invalid file separator characters and
	 * replace them with valid ones.
	 * 
	 * @param input
	 *            The {@link String} to check.
	 * 
	 * @return The input {@link String} with the replacements done.
	 * */
	public static String onlyContainSystemSpecificFileSeparatorChars(final String input)
	{
		if ((input != null) && (input.trim().length() > 0))
		{
			if (File.separatorChar == '\\')
			{
				return input.replace('/', File.separatorChar);
			}
			else
			{
				return input.replace('\\', File.separatorChar);
			}
		}
		return input;
	}
	
	/* PRIVATE CONSTANTS */
	/**
	 * This will be used to log any activity in the {@link FileHelper} class.
	 * */
	private static final Logger logger = new Logger(FileUtils.class.getName());
	
}
