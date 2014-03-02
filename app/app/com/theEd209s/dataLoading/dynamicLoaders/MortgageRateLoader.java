package com.theEd209s.dataLoading.dynamicLoaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;

import javax.persistence.PersistenceException;

import models.MortgageRate;
import play.Logger;

import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.utils.CsvParser;
import com.theEd209s.utils.StringUtils;

/**
 * This class will load the mortgage rates into the database.
 * 
 * @author Matthew Weiler
 * */
public class MortgageRateLoader extends DataLoader
{	
	
	/* PUBLIC CONSTANTS */
	/**
	 * This stores the base CKAN url.
	 * */
	public static final String CKAN_URL = "http://data.gc.ca/data/en";
	/**
	 * This stores the CKAN data-set ID.
	 * */
	public static final String CKAN_DATASET_ID = "ae607e9a-2fce-4ed9-83e3-ba4cdbc24b8d";
	/**
	 * This stores the CKAN resource ID.
	 * */
	public static final String CKAN_RESOURCE_ID = "1f1e3876-1b45-4888-ad74-9e13bb60a57b";
	
	/* CONSTRUCTORS */
	/**
	 * This will create a new instance of a {@link DataLoader}.
	 * */
	public MortgageRateLoader()
	{
		super(MortgageRateLoader.CKAN_URL, MortgageRateLoader.CKAN_DATASET_ID, MortgageRateLoader.CKAN_RESOURCE_ID);
	}
	
	/* PROTECTED METHODS */
	@SuppressWarnings("deprecation")
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
				final CsvParser parser = new CsvParser();
				String tmpLine = null;
				String[] lineElements = null;
				
				int referenceYear = -1;
				int referenceMonth = -1;
				float mortgageRateValue = -1.0F;
				String[] tmpDateItems = null;
				
				while ((tmpLine = br.readLine()) != null)
				{
					if (!StringUtils.isNullOrEmpty(tmpLine))
					{
						lineElements = parser.parseLine(tmpLine);
						if ((lineElements != null) && (lineElements.length > 0))
						{
							if (lineElements.length == 5)
							{
								try
								{
									tmpDateItems = lineElements[0].trim().split("/");
									if ((tmpDateItems != null) && (tmpDateItems.length == 2))
									{
										referenceYear = Integer.parseInt(tmpDateItems[0]);
										referenceMonth = Integer.parseInt(tmpDateItems[1]);
										if ((referenceYear > 0) && (referenceMonth > 0))
										{
											mortgageRateValue = Float.parseFloat(lineElements[4]);
											final MortgageRate mortgageRate = new MortgageRate();
											mortgageRate.referenceDate = new Date(referenceYear, referenceMonth, 1);
											mortgageRate.rate = mortgageRateValue;
											mortgageRate.save();
											rowsInserted++;
										}
									}
								}
								catch (NumberFormatException nfe)
								{
									// ignore ...
								}
								catch (PersistenceException pe)
								{
									Logger.error("Failed to save mortgage rate to database.", pe);
								}
							}
							else
							{
								Logger.warn("Invalid number of fields in mortgage rate CSV file line: " + tmpLine);
							}
						}
					}
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
