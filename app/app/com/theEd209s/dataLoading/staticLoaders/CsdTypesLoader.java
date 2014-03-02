package com.theEd209s.dataLoading.staticLoaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.persistence.NonUniqueResultException;

import models.CSDType;
import play.Logger;
import play.Play;

import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.utils.CsvParser;
import com.theEd209s.utils.StringUtils;

/**
 * This class will load the CSD types into the database.
 * 
 * @author Matthew Weiler
 * */
public class CsdTypesLoader extends DataLoader
{	
	
	/* PUBLIC CONSTANTS */
	/**
	 * This stores the relative path of the source file containing
	 * {@link CSDType} data.
	 * */
	public static final String RELATIVE_BUILDING_TYPES_SOURCE_FILE = "conf" + File.separatorChar + "csd_types.csv";
	
	/* CONSTRUCTORS */
	/**
	 * This will create a new instance of a {@link CsdTypesLoader}.
	 * */
	public CsdTypesLoader()
	{
		super(Play.application().getFile(CsdTypesLoader.RELATIVE_BUILDING_TYPES_SOURCE_FILE), false);
	}
	
	/* PROTECTED METHODS */
	@Override
	protected int parseFile(File downloadedFile) throws Throwable
	{
		int rowsInserted = 0;
		if ((downloadedFile != null) && downloadedFile.exists() && downloadedFile.isFile())
		{
			// Loop over the CSV file.
			BufferedReader br = null;
			InputStreamReader isr = null;
			FileInputStream fis = null;
			try
			{
				fis = new FileInputStream(downloadedFile);
				isr = new InputStreamReader(fis, "UTF-8");
				br = new BufferedReader(isr);
				String tmpLine = null;
				boolean inHeader = true;
				CSDType tmpType = null;
				while ((tmpLine = br.readLine()) != null)
				{
					if (!StringUtils.isNullOrEmpty(tmpLine))
					{
						if (!inHeader)
						{
							String[] results = CsvParser.parseLineS(tmpLine);
							if (results.length == 2)
							{
								try
								{
									tmpType = new CSDType();
									tmpType.accronym = new String(results[0].getBytes("UTF8"), "UTF8");
									tmpType.name = results[0];
									tmpType.save();
									rowsInserted++;
								}
								catch (NonUniqueResultException e)
								{
									// ignore ...
								}
							}
							else
							{
								Logger.debug("Badly formatted entry" + Arrays.toString(results));
							}
						}
					}
					inHeader = false;
				}
			}
			catch (Throwable thrown)
			{
				Logger.warn("Failed to read downloaded file.", thrown);
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
						Logger.warn("Failed to close FileInputStream.", ioe);
					}
				}
				if (isr != null)
				{
					try
					{
						isr.close();
					}
					catch (IOException ioe)
					{
						Logger.warn("Failed to close InputStreamReader.", ioe);
					}
				}
				if (br != null)
				{
					try
					{
						br.close();
					}
					catch (IOException ioe)
					{
						Logger.warn("Failed to close BufferedReader.", ioe);
					}
				}
			}
		}
		return rowsInserted;
	}
	
	@Override
	protected void downloadFailed()
	{
		
	}
	
	@Override
	protected void parseFailed()
	{
		
	}
	
}
