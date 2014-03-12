import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import models.BuildingType;
import models.RentalRate;
import models.UnitType;
import controllers.api.form.RentalPricesForm;
import base.BaseTestSuit;
import static org.junit.Assert.*;

public class RenterUnitTests extends BaseTestSuit
{
	Map<Integer, List<RentalRate>> c1RatesMap;
	
	@Override
	public void setupDelegate()
	{
		c1RatesMap = new HashMap<Integer, List<RentalRate>>();
		for (int year = 2000; year <= 2014; year++)
		{
			List<RentalRate> c1Rates = new ArrayList<RentalRate>();
			RentalRate aRate = new RentalRate();
			aRate.city = c1;
			aRate.province = c1.province;
			aRate.referenceYear = year;
			aRate.rentalRate = 500;
			aRate.buildingType = BuildingType.getByAbbreviation("RA1");
			aRate.unitType = UnitType.getByAbbreviation("BA");
			aRate.save();
			c1Rates.add(aRate);
			
			aRate = new RentalRate();
			aRate.city = c1;
			aRate.province = c1.province;
			aRate.referenceYear = year;
			aRate.rentalRate = 500;
			aRate.buildingType = BuildingType.getByAbbreviation("RA2");
			aRate.unitType = UnitType.getByAbbreviation("B2");
			aRate.save();
			c1Rates.add(aRate);
			c1RatesMap.put(year, c1Rates);
		}
		
		for (int year = 2000; year <= 2014; year++)
		{
			RentalRate aRate = new RentalRate();
			aRate.city = c2;
			aRate.province = c2.province;
			aRate.referenceYear = year;
			aRate.rentalRate = 500;
			aRate.buildingType = BuildingType.getByAbbreviation("RA1");
			aRate.unitType = UnitType.getByAbbreviation("BA");
			aRate.save();
			
			aRate = new RentalRate();
			aRate.city = c2;
			aRate.province = c2.province;
			aRate.referenceYear = year;
			aRate.rentalRate = 500;
			aRate.buildingType = BuildingType.getByAbbreviation("RA2");
			aRate.unitType = UnitType.getByAbbreviation("B2");
			aRate.save();
		}
	}
	
	/**
	 * Test that getting the map of rates from a city will get the proper map
	 */
	@Test
	public void testRentalRate()
	{
		RentalPricesForm form = new RentalPricesForm();
		form.setScgCode5(c1.cityId);
		form.setScgCode7(c1.cityParentId);
		Map<Integer, List<RentalRate>> result = RentalRate.getRentalRatesForRequest(form);
		assertEquals(c1RatesMap, result);
	}
}
