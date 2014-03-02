package com.theEd209s.dataLoading.dynamicLoaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import models.City;
import models.CityVacancy;

import org.h2.util.StringUtils;

import play.Logger;

import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.utils.CsvParser;

/**
 * Loads to parse the vacancy rates data and put them in db
 * 
 * @author Kiran
 */
public class VacancyRateLoader extends DataLoader
{
	
	/**
	 * Details of dataset1
	 * @author Kiran
	 *
	 */
	public final static class VacancyData1
	{
		/**
		 * This stores the base CKAN url.
		 * */
		public static final String CKAN_URL = "http://data.gc.ca/data/en";
		/**
		 * This stores the CKAN data-set ID.
		 * */
		public static final String CKAN_DATASET_ID = "bb74cfee-b1db-4fe2-9415-8472594ce61f";
		/**
		 * This stores the CKAN resource ID.
		 * */
		public static final String CKAN_RESOURCE_ID = "75849678-c9b5-42c7-aa63-79b1dcb1e715";
	}
	
	/**
	 * Details of dataset2 
	 * @author Kiran
	 *
	 */
	public final static class VacancyData2
	{
		/**
		 * This stores the base CKAN url.
		 * */
		public static final String CKAN_URL = "http://data.gc.ca/data/en";
		/**
		 * This stores the CKAN data-set ID.
		 * */
		public static final String CKAN_DATASET_ID = "17ef49cd-d0e2-4945-98f6-9ed7e153b4b0";
		/**
		 * This stores the CKAN resource ID.
		 * */
		public static final String CKAN_RESOURCE_ID = "a7bc9dbe-f53a-490c-8011-ba7933e5542b";
	}
	
	public VacancyRateLoader(final String ckanUrl, final String ckanDatesetId, final String ckanResourceId)
	{
		super(ckanUrl, ckanDatesetId, ckanResourceId);
	}
	
	@Override
	protected int parseFile(final File downloadedFile) throws Throwable
	{
		int rowsInserted = 0;
		if ((downloadedFile != null) && downloadedFile.exists() && downloadedFile.isFile())
		{
			if ((downloadedFile != null) && downloadedFile.exists() && downloadedFile.isFile())
			{
				final List<City> cities = City.loadAllCities();
				
				// check if cities exist
				if (cities != null && cities.size() > 0)
				{
					BufferedReader br = null;
					InputStreamReader isr = null;
					FileInputStream fis = null;
					
					try
					{
						fis = new FileInputStream(downloadedFile);
						isr = new InputStreamReader(fis, "UTF-8");
						br = new BufferedReader(isr);
						
						// parse the csv file
						final CsvParser parser = new CsvParser();
						String readLine = null;
						
						while ((readLine = br.readLine()) != null)
						{
							if (!StringUtils.isNullOrEmpty(readLine))
							{
								String[] lineElements = parser.parseLine(readLine);
								
								if (lineElements != null && lineElements.length > 0)
								{
									final String[] geoClassifications = lineElements[2].trim().split(",");
									
									// do this only if there is geo classification available
									if (geoClassifications != null && geoClassifications.length > 0)
									{
										for (String geoClassification : geoClassifications)
										{
											try
											{
												int tmpGeoClassificationInt = Integer.parseInt(geoClassification);
												
												if (tmpGeoClassificationInt > 0)
												{
													try
													{
														final float tmpValue = Float.parseFloat(lineElements[5].trim());
														final int tmpRefYear = Integer.parseInt(lineElements[0].trim());
														
														for (City city : cities)
														{
															if ((tmpGeoClassificationInt == city.cityId) || (tmpGeoClassificationInt == city.cityParentId))
															{
																final CityVacancy cityVacancy = new CityVacancy();
																cityVacancy.city = city;
																cityVacancy.province = city.province;
																cityVacancy.referenceYear = tmpRefYear;
																cityVacancy.vacancyRate = tmpValue;
																cityVacancy.save();
																rowsInserted++;
															}
														}
													}
													catch (NonUniqueResultException e)
													{
														// ignore ...
													}
													catch (NumberFormatException nex)
													{
														Logger.debug("Could not parse either value " + lineElements[5] + " or the year " + lineElements[0], nex);
													}
													catch (PersistenceException pe)
													{
														Logger.error("Failed to save rental rate to database.", pe);
													}
												}
											}
											catch (NumberFormatException nexOut)
											{
												// ignore ...
											}
										}
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
		}
		return rowsInserted;
	}
	
	@Override
	protected void downloadFailed()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void parseFailed()
	{
		// TODO Auto-generated method stub
		
	}
	
}
