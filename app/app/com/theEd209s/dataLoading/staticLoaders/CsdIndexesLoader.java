package com.theEd209s.dataLoading.staticLoaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import models.CSDIndex;
import models.CSDType;
import play.Logger;
import play.Play;

import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.utils.StringUtils;

/**
 * This class will load the CSD Indexes into the database.
 * 
 * @author Matthew Weiler
 * */
public class CsdIndexesLoader extends DataLoader
{
	
	/* PUBLIC CONSTANTS */
	/**
	 * This stores the relative path of the source file containing
	 * {@link CSDIndex} data.
	 * */
	public static final String RELATIVE_BUILDING_TYPES_SOURCE_FILE = "conf" + File.separatorChar + "Table8.txt";
	
	/* CONSTRUCTORS */
	/**
	 * This will create a new instance of a {@link CsdIndexesLoader}.
	 * */
	public CsdIndexesLoader()
	{
		super(Play.application().getFile(CsdIndexesLoader.RELATIVE_BUILDING_TYPES_SOURCE_FILE), false);
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
				CSDIndex tmpCsdValue = null;
				List<String> csdTypeAcronyms =  CSDType.getAllCSDAccronyms();
				while ((tmpLine = br.readLine()) != null)
				{
					Matcher m = CsdIndexesLoader.agglomerationCodesPattern.matcher(tmpLine);
					
					if (m.find())
					{
						String tmpCityName = tmpLine.substring(0, tmpLine.indexOf(m.group(2)));
						String cityName = refineCityName(tmpCityName,csdTypeAcronyms);
						String anAgglomerationCode = m.group(2);
						Logger.debug("MATCH: " + tmpLine + " found data " + cityName + " \"" + anAgglomerationCode + "\"");
						String csdType = CsdIndexesLoader.getCSDTypeFromName(tmpCityName);
						tmpCsdValue = new CSDIndex();
						tmpCsdValue.placeName = cityName;
						tmpCsdValue.csdType = csdType;
						CsdIndexesLoader.buildCSDIndex(tmpCsdValue, anAgglomerationCode);
						try
						{
							tmpCsdValue.save();
							rowsInserted++;
						}
						catch (PersistenceException e)
						{
							Logger.debug("If this is a unique error then the data we got was not well defined. We will ignore the failed duplicates.", e);
						}
						continue;
					}
					else
					{
						Logger.debug("NOMATCH For Value: " + tmpLine);
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
	
	private static String refineCityName(String name,List<String> csdTypeAcronyms)
	{
		if(!StringUtils.isNullOrEmpty(name))
		{
			 String[] nameTokens = name.split(" +");
			 String lastIndexStr = nameTokens[nameTokens.length-1];
			 
			 // this is a csd type so ignore this
			 if( csdTypeAcronyms.contains(lastIndexStr))
			 {
				 StringBuilder cleanedup = new StringBuilder();
				 
				 for (int i = 0; i < nameTokens.length-1; i++)
				 {
					 cleanedup.append(nameTokens[i]).append(" ");
				 }
				 
				 return cleanedup.toString().trim();
			 }
			 else
			 {
				 return name.trim();
			 }
		}
		
		return name;
	}
	
	/* PRIVATE METHODS */
	private static String getCSDTypeFromName(String cityName)
	{
		if (StringUtils.isNullOrEmpty(cityName))
		{
			return null;
		}
		final String[] cityWords = cityName.trim().split(" ");
		try
		{
			CSDType csdType = CSDType.getCSDByAccronym(cityWords[cityWords.length - 1]);
			return csdType == null ? null : csdType.accronym;
		}
		catch (PersistenceException e)
		{
			Logger.error(cityWords[cityWords.length - 1] + " ");
		}
		return null;
		
	}
	
	private static CSDIndex buildCSDIndex(CSDIndex csdIndex, String completecode)
	{
		final Matcher m = agglomerationCodePattern.matcher(completecode);
		try
		{
			if (m.matches())
			{
				csdIndex.provinceCode = Integer.parseInt(m.group(1));
				csdIndex.censusDivision = Integer.parseInt(m.group(2));
				csdIndex.censusSubDivision = Integer.parseInt(m.group(3));
				csdIndex.censusAgglomeration = Integer.parseInt(m.group(4));
			}
		}
		catch (Exception e)
		{
			Logger.error("CSD PARSING ERROR for string " + completecode, e);
		}
		
		return csdIndex;
	}
	
	/* PRIVATE CONSTANTS */
	private static final String agglomerationCodes = "(.)+(\\d{2} \\d{2} \\d{3} \\d{3})";
	private static final Pattern agglomerationCodesPattern = Pattern.compile(agglomerationCodes);
	
	private static final String agglomerationCode = "(\\d{2}) (\\d{2}) (\\d{3}) (\\d{3})";
	private static final Pattern agglomerationCodePattern = Pattern.compile(agglomerationCode);
	
}
