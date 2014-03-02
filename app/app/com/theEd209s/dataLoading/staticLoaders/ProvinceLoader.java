package com.theEd209s.dataLoading.staticLoaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import models.Province;
import play.Logger;
import play.Play;

import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.utils.CsvParser;

public class ProvinceLoader extends DataLoader
{
	/**
	 * This stores the relative path of the source file containing
	 * {@link Province} data.
	 * */
	public static final String RELATIVE_PROVINCE_SOURCE_FILE = "conf" + File.separatorChar + "provinces.csv";
	
	public ProvinceLoader()
	{
		super(Play.application().getFile(ProvinceLoader.RELATIVE_PROVINCE_SOURCE_FILE), false);
	}

	@Override
	protected int parseFile(File downloadedFile) throws Throwable 
	{
		int rowsInserted = 0;
		if ((downloadedFile != null) && downloadedFile.exists() && downloadedFile.isFile())
		{	
			BufferedReader br = null;
			InputStreamReader isr = null;
			FileInputStream fis = null;
			
			try
			{
				fis = new FileInputStream(downloadedFile);
				isr = new InputStreamReader(fis, "UTF8");
				br = new BufferedReader(isr);
				String readLine = null;
				int lineNumber =1;
				
				while ((readLine = br.readLine()) != null)
				{
					String[] results = CsvParser.parseLineS(readLine);
					
					if (results.length != 2)
					{
						Logger.debug("Badly formatted entry"+ Arrays.toString(results));
					} //ignore firstline header
					else if(lineNumber > 1)
					{
						Province prs = new Province();
						prs.provinceId = Integer.parseInt(results[0]);
						prs.abbreviation = results[1];
						prs.save();
						rowsInserted++;
					}
					lineNumber++;
				}

			} catch (IOException e)
			{
				Logger.error("Error parsing BuildingType File", e);
			} finally
			{
				if (fis != null)
				{
					try
					{
						fis.close();
					} catch (IOException ignore)
					{

					}
				}

				if (isr != null)
				{
					try
					{
						isr.close();
					} catch (IOException ignore)
					{

					}
				}

				if (br != null)
				{
					try
					{
						br.close();
					} 
					catch (IOException ignore)
					{

					}
				}
			}
		}
		return rowsInserted;
	}

	@Override
	protected void downloadFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void parseFailed() {
		// TODO Auto-generated method stub
		
	}

}
