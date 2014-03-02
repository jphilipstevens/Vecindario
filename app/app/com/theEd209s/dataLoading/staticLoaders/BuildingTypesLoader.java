package com.theEd209s.dataLoading.staticLoaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.persistence.NonUniqueResultException;

import models.BuildingType;
import play.Logger;
import play.Play;

import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.utils.StringUtils;

/**
 * This class will load the building types into the database.
 * 
 * @author Matthew Weiler
 * */
public class BuildingTypesLoader extends DataLoader
{	
	
	/* PUBLIC CONSTANTS */
	/**
	 * This stores the relative path of the source file containing
	 * {@link BuildingType} data.
	 * */
	public static final String RELATIVE_BUILDING_TYPES_SOURCE_FILE = "conf" + File.separatorChar + "building_types.csv";
	
	/* CONSTRUCTORS */
	/**
	 * This will create a new instance of a {@link BuildingTypesLoader}.
	 * */
	public BuildingTypesLoader()
	{
		super(Play.application().getFile(BuildingTypesLoader.RELATIVE_BUILDING_TYPES_SOURCE_FILE), false);
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
				while ((tmpLine = br.readLine()) != null)
				{
					if (!StringUtils.isNullOrEmpty(tmpLine))
					{
						try
						{
							final BuildingType buildingType = new BuildingType();
							buildingType.abbreviation = tmpLine;
							buildingType.save();
							rowsInserted++;
						}
						catch (NonUniqueResultException e)
						{
							// ignore ...
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
