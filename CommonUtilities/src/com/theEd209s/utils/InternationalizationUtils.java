package com.theEd209s.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

import com.theEd209s.logging.Logger;

/**
 * This class allows for the loading of internationalization
 * {@link File}s from the current classpath.
 * 
 * @author Matthew Weiler
 * */
public class InternationalizationUtils
{
	
	/* PUBLIC METHODS */
	/**
	 * This method will load the system default language code.
	 * 
	 * @return
	 * The system default language code.
	 * */
	public static String getSystemLanguageCode()
	{
		String languageCode = null;
		final Locale deviceLocale = Locale.getDefault(Locale.Category.DISPLAY);
		if (deviceLocale != null)
		{
			languageCode = deviceLocale.getLanguage();
		}
		return languageCode;
	}
	
	/**
	 * This will load the application language constants based on the desired language.
	 * 
	 * @param props
	 *            The {@link Properties} to populate.
	 * @param propertiesFileRelativePath
	 *            The classpath of the default language file.
	 * @param selectedLanguage
	 *            The desired language that the application should run in. <br />
	 *            <i>the 2 character representation of the language to use</i> <br />
	 *            <i>if this desired language is not supported, the default of
	 *            English will be used.</i>
	 * */
	public static void loadLanguageConstants(final Properties props, String propertiesFileRelativePath, String selectedLanguage) throws Throwable
	{
		// Since we load the language files from our source code, we
		// get an instance of the current ClassLoader.
		final ClassLoader classLoader = InternationalizationUtils.class.getClassLoader();
		if (classLoader != null)
		{
			if ((propertiesFileRelativePath != null) && (propertiesFileRelativePath.trim().length() > 0))
			{
				try
				{
					String propertiesFileRelativePath_part1 = null;
					String propertiesFileRelativePath_fileExt = null;
					propertiesFileRelativePath = propertiesFileRelativePath.replace("\\", "/");
					int lastIndexOfSlash = propertiesFileRelativePath.lastIndexOf('/');
					if (lastIndexOfSlash < 0)
					{
						lastIndexOfSlash = 0;
					}
					int lastIndexOfDot = propertiesFileRelativePath.lastIndexOf('.');
					if ((lastIndexOfDot > 0) && (lastIndexOfDot > lastIndexOfSlash) && ((lastIndexOfDot + 1) < propertiesFileRelativePath.length()))
					{
						propertiesFileRelativePath_part1 = propertiesFileRelativePath.substring(0, lastIndexOfDot);
						propertiesFileRelativePath_fileExt = propertiesFileRelativePath.substring(lastIndexOfDot + 1);
					}
					if ((propertiesFileRelativePath_part1 != null) && (propertiesFileRelativePath_part1.trim().length() > 0) && (propertiesFileRelativePath_fileExt != null) && (propertiesFileRelativePath_fileExt.trim().length() > 0))
					{
						// We break out of this while-loop when we've either found
						// and loaded a messages file or we can't find a matching
						// file.
						while (true)
						{
							String propertiesFilePath = null;
							if ((selectedLanguage != null) && (selectedLanguage.trim().length() > 0))
							{
								selectedLanguage = selectedLanguage.trim().toLowerCase();
								propertiesFilePath = propertiesFileRelativePath_part1 + "_" + selectedLanguage + "." + propertiesFileRelativePath_fileExt;
							}
							else
							{
								propertiesFilePath = propertiesFileRelativePath_part1 + "." + propertiesFileRelativePath_fileExt;
							}
							InternationalizationUtils.logger.info("loadLanguageConstants", "Attempting to load constants file for language: " + selectedLanguage + " (" + propertiesFilePath + ")");
							// Define the input file as a resource.
							InputStream is = null;
							InputStreamReader isr = null;
							try
							{
								is = classLoader.getResourceAsStream(propertiesFilePath);
								if (is != null)
								{
									try
									{
										isr = new InputStreamReader(is, "UTF-8");
										InternationalizationUtils.logger.info("loadLanguageConstants", "Loading messages for language: " + selectedLanguage);
										InternationalizationUtils.loadLanguageConstants(props, isr, selectedLanguage);
										break;
									}
									catch (IOException ioe)
									{
										InternationalizationUtils.logger.warning("loadLanguageConstants", "Failed to load messages for specific language: " + selectedLanguage, ioe);
									}
								}
								else
								{
									InternationalizationUtils.logger.warning("loadLanguageConstants", "Unable to find messages file for language: " + selectedLanguage);
								}
							}
							catch (Throwable thrown)
							{
								InternationalizationUtils.logger.warning("loadLanguageConstants", "Failed to load messages file for language: " + selectedLanguage);
							}
							finally
							{
								if (isr != null)
								{
									try
									{
										isr.close();
									}
									catch (IOException ioe)
									{
										InternationalizationUtils.logger.error("loadLanguageConstants", "Failed to close messages InputStreamReader.", ioe);
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
										InternationalizationUtils.logger.error("loadLanguageConstants", "Failed to close messages InputStream.", ioe);
									}
								}
							}
							if ((selectedLanguage != null) && (selectedLanguage.trim().length() > 0))
							{
								final int dashIndex = selectedLanguage.indexOf('-');
								if (dashIndex > 0)
								{
									// Just extract the language code from the language code String.
									// The convention is to either just specify the language code (en)
									// or the language code plus country (en-CA).
									selectedLanguage = selectedLanguage.substring(0, dashIndex);
								}
								else
								{
									// If the selected language code is not the same as the system language
									// code, then try the system language code.
									if (!selectedLanguage.equalsIgnoreCase(InternationalizationUtils.getSystemLanguageCode()))
									{
										selectedLanguage = InternationalizationUtils.getSystemLanguageCode();
									}
									// If the selected language code is the same as the system language code,
									// then just try the default language file.
									// This will be the last attempt.
									else
									{
										selectedLanguage = null;
									}
								}
							}
							else
							{
								// Since we've already tried the base messages file, we obviously
								// don't have a file that meets our criteria.
								throw new Exception("Failed to load messages.");
							}
						}
						InternationalizationUtils.logger.info("loadLanguageConstants", "Successfully loaded messages.");
					}
					else
					{
						throw new Exception("Failed to load messages; not able to detemine file name.");
					}
				}
				catch (Throwable thrown)
				{
					InternationalizationUtils.logger.error("loadLanguageConstants", "Failed to load messages.", thrown);
					throw thrown;
				}
			}
			else
			{
				InternationalizationUtils.logger.error("loadLanguageConstants", "Invalid input given.");
				throw new Exception("Invalid relative path specified.");
			}
		}
		else
		{
			InternationalizationUtils.logger.error("loadLanguageConstants", "Unable to load ClassLoader.");
			throw new Exception("Unable to load ClassLoader.");
		}
	}
	
	/* PRIVATE METHODS */
	/**
	 * This will load the application language constants based on the desired language.
	 * 
	 * @param props
	 *            The {@link Properties} to populate.
	 * @param isr
	 *            The {@link InputStreamReader} to read from.
	 * @param selectedLanguage
	 *            The desired language that the application should run in. <br />
	 *            <i>the 2 character representation of the language to use</i> <br />
	 *            <i>if this desired language is not supported, the default of
	 *            English will be used.</i>
	 * */
	private static void loadLanguageConstants(final Properties props, final InputStreamReader isr, String selectedLanguage) throws IOException
	{
		if (props != null)
		{
			if (isr != null)
			{
				props.load(isr);
			}
			else
			{
				throw new IOException("No InputStreamReader was specified.");
			}
		}
		else
		{
			throw new IOException("No InputStreamReader object was specified.");
		}
	}
	
	/* PRIVATE CONSTANTS */
	/**
	 * This will be used to log any errors to a log file.
	 * */
	private static final Logger logger = new Logger(InternationalizationUtils.class.getName());
	
}
