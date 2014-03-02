package com.theEd209s.dataLoading.dynamicLoaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import models.City;
import models.NewHousingPriceIndex;
import play.Logger;

import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.utils.CsvParser;
import com.theEd209s.utils.StringUtils;

/**
 * This class will load the new housing price index into the database.
 * 
 * @author Matthew Weiler
 * */
public class NewHousingPriceIndexLoader extends DataLoader
{
	
	/* PUBLIC CONSTANTS */
	/**
	 * This stores the base CKAN url.
	 * */
	public static final String CKAN_URL = "http://data.gc.ca/data/en";
	/**
	 * This stores the CKAN data-set ID.
	 * */
	public static final String CKAN_DATASET_ID = "f3c1eab9-fce2-4cb7-8ffd-3979edaaa286";
	/**
	 * This stores the CKAN resource ID.
	 * */
	public static final String CKAN_RESOURCE_ID = "cccb66c2-da74-4be8-b0b3-baac67668b70";
	
	/* CONSTRUCTORS */
	/**
	 * This will create a new instance of a {@link NewHousingPriceIndexLoader}.
	 * */
	public NewHousingPriceIndexLoader()
	{
		super(NewHousingPriceIndexLoader.CKAN_URL, NewHousingPriceIndexLoader.CKAN_DATASET_ID, NewHousingPriceIndexLoader.CKAN_RESOURCE_ID);
	}
	
	/* PROTECTED METHODS */
	@Override
	protected int parseFile(File downloadedFile) throws Throwable
	{
		int rowsInserted = 0;
		if ((downloadedFile != null) && downloadedFile.exists() && downloadedFile.isFile())
		{
			final List<City> cities = City.loadAllCities();
			if ((cities != null) && (cities.size() > 0))
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
					float newHousingPriceIndexValue = -1.0F;
					int tmpGeoClassificationInt = -1;
					String[] tmpDateItems = null;
					
					Calendar cal = Calendar.getInstance();

					while ((tmpLine = br.readLine()) != null)
					{
						if (!StringUtils.isNullOrEmpty(tmpLine))
						{
							lineElements = parser.parseLine(tmpLine);
							if ((lineElements != null) && (lineElements.length > 0))
							{
								if (lineElements.length == 7)
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
												final String[] geoClassifications = lineElements[2].trim().split(",");
												if (geoClassifications.length > 0)
												{
													newHousingPriceIndexValue = Float.parseFloat(lineElements[6]);
													for (String geoClassification : geoClassifications)
													{
														try
														{
															tmpGeoClassificationInt = Integer.parseInt(geoClassification);
															if (tmpGeoClassificationInt > 0)
															{
																for (City city : cities)
																{
																	try
																	{
																		if ((tmpGeoClassificationInt == city.cityId) || (tmpGeoClassificationInt == city.cityParentId))
																		{
																			final NewHousingPriceIndex newHousingPriceIndex = new NewHousingPriceIndex();
																			
																			cal.set(referenceYear, referenceMonth, 1);
																			newHousingPriceIndex.referenceDate = new java.sql.Date(cal.getTime().getTime());
																			newHousingPriceIndex.city = city;
																			newHousingPriceIndex.province = city.province;
																			newHousingPriceIndex.priceIndex = newHousingPriceIndexValue;
																			newHousingPriceIndex.save();
																			rowsInserted++;
																		}
																	}
																	catch (NonUniqueResultException e)
																	{
																		// ignore ...
																	}
																}
															}
														}
														catch (NumberFormatException nfe)
														{
															// ignore ...
														}
														catch (PersistenceException pe)
														{
															Logger.error("Failed to save new housing price index to database.", pe);
														}
													}
												}
											}
										}
									}
									catch (NumberFormatException nfe)
									{
										// ignore ...
									}
								}
								else
								{
									Logger.warn("Invalid number of fields in new housing price index CSV file line: " + tmpLine);
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
