package com.theEd209s.dataLoading.staticLoaders;

import play.Logger;
import models.CSDIndex;
import models.City;
import models.Province;

import com.theEd209s.utils.StringUtils;

public class CitiesLoader 
{   
	public void populateCities()
	{	
		try
		{				
			final java.util.List<CSDIndex> csdIndexes=  CSDIndex.getAllCityNamesAndIds();
			
			if(csdIndexes !=null && csdIndexes.size() > 0)
			{			
				for(CSDIndex index: csdIndexes)
				{					
					String tmpProvinceCode = String.format("%02d",index.provinceCode);
					String tmpca = String.format("%03d",index.censusAgglomeration);
					String tmpcd = String.format("%02d",index.censusDivision);
					String tmpCsd = String.format("%03d",index.censusSubDivision);
					
					String tmpParentId = tmpProvinceCode + tmpca;
					String tmpcityId = tmpProvinceCode + tmpcd + tmpCsd;
					String tmpCityName = index.placeName;
					
					if(!StringUtils.isNullOrEmpty(tmpParentId) && 
					   !StringUtils.isNullOrEmpty(tmpcityId)&&
					   !StringUtils.isNullOrEmpty(tmpCityName))
					{
						final City city = new City();
						city.cityId = Integer.parseInt(tmpcityId);
						city.cityName = tmpCityName;
						city.cityParentId = Integer.parseInt(tmpParentId);
						city.province = Province.getProvinceById(index.provinceCode);
						city.checkAndSave();
					}
				}				
			}
		}
		catch (Exception e)
		{
			Logger.error("Error populating city table", e);
		} 
	}
}
