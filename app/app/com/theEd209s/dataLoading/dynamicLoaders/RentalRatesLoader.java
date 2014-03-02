package com.theEd209s.dataLoading.dynamicLoaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import models.BuildingType;
import models.City;
import models.RentalRate;
import models.UnitType;
import play.Logger;

import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.utils.CsvParser;
import com.theEd209s.utils.StringUtils;

/**
 * This class will load the rental rates into the database.
 * 
 * @author Matthew Weiler
 * */
public class RentalRatesLoader extends DataLoader
{	
	
	/* PUBLIC CONSTANTS */
	/**
	 * This stores the base CKAN url.
	 * */
	public static final String CKAN_URL = "http://data.gc.ca/data/en";
	/**
	 * This stores the CKAN data-set ID.
	 * */
	public static final String CKAN_DATASET_ID = "1146388b-a150-4e70-98ec-eb40cb9083c8";
	/**
	 * This stores the CKAN resource ID.
	 * */
	public static final String CKAN_RESOURCE_ID = "4a4cb88c-6128-4874-9220-63c8100349ec";
	
	/* CONSTRUCTORS */
	/**
	 * This will create a new instance of a {@link DataLoader}.
	 * */
	public RentalRatesLoader()
	{
		super(RentalRatesLoader.CKAN_URL, RentalRatesLoader.CKAN_DATASET_ID, RentalRatesLoader.CKAN_RESOURCE_ID);
	}
	
	/* PROTECTED METHODS */
	@Override
	protected int parseFile(File downloadedFile) throws Throwable
	{
		int rowsInserted = 0;
		if ((downloadedFile != null) && downloadedFile.exists() && downloadedFile.isFile())
		{
			// Load all building types, unit types and cities.
			final List<BuildingType> buildingTypes = BuildingType.loadAllBuildingTypes();
			if ((buildingTypes != null) && (buildingTypes.size() > 0))
			{
				final List<UnitType> unitTypes = UnitType.loadAllUnitTypes();
				if ((unitTypes != null) && (unitTypes.size() > 0))
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
							float rentalRateValue = -1.0F;
							int tmpGeoClassificationInt = -1;
							
							while ((tmpLine = br.readLine()) != null)
							{
								if (!StringUtils.isNullOrEmpty(tmpLine))
								{
									lineElements = parser.parseLine(tmpLine);
									if ((lineElements != null) && (lineElements.length > 0))
									{
										if (lineElements.length == 8)
										{
											try
											{
												referenceYear = Integer.parseInt(lineElements[0].trim());
												if (referenceYear > 0)
												{
													final String[] geoClassifications = lineElements[2].trim().split(",");
													if (geoClassifications.length > 0)
													{
														final String buildingAbbr = BuildingType.determineAbbr(lineElements[3].trim());
														if (!StringUtils.isNullOrEmpty(buildingAbbr))
														{
															for (BuildingType buildingType : buildingTypes)
															{
																if (buildingAbbr.equalsIgnoreCase(buildingType.abbreviation))
																{
																	final String unitAbbr = UnitType.determineAbbr(lineElements[4].trim());
																	if (!StringUtils.isNullOrEmpty(unitAbbr))
																	{
																		for (UnitType unitType : unitTypes)
																		{
																			if (unitAbbr.equalsIgnoreCase(unitType.abbreviation))
																			{
																				rentalRateValue = Float.parseFloat(lineElements[7].trim());
																				if (rentalRateValue > 0.0F)
																				{
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
																											final RentalRate rentalRate = new RentalRate();
																											rentalRate.buildingType = buildingType;
																											rentalRate.unitType = unitType;
																											rentalRate.referenceYear = referenceYear;
																											rentalRate.rentalRate = rentalRateValue;
																											rentalRate.city = city;
																											rentalRate.province = city.province;
																											rentalRate.save();
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
																							Logger.error("Failed to save rental rate to database.", pe);
																						}
																					}
																				}
																				break;
																			}
																		}
																	}
																	break;
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
											Logger.warn("Invalid number of fields in rental rate CSV file line: " + tmpLine);
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
					else
					{
						Logger.error("Failed to load all cities.");
					}
				}
				else
				{
					Logger.error("Failed to load all unit types.");
				}
			}
			else
			{
				Logger.error("Failed to load all building types.");
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
