import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.avaje.ebean.Query;

import models.City;
import base.BaseTestSuit;

public class CityTest extends BaseTestSuit
{
	
	@Override
	public void setupDelegate()
	{}
	
	/**
	 * Test that the city id alone returns c1
	 * @throws Exception
	 */
	@Test
	public void testCityIdFilter() throws Exception
	{
		Query<City> aQuery = City.find.query();
		City.addScgCodeToQuery(aQuery.where(), c1.cityId, -1, "");
		
		City c = aQuery.findUnique();
		assertEquals(c1, c);
		
	}
	
	/**
	 * Test that the city parent id alone returns c1
	 * @throws Exception
	 */
	@Test
	public void testCityParentIdFilter() throws Exception
	{
		Query<City> aQuery = City.find.query();
		City.addScgCodeToQuery(aQuery.where(), -1, c1.cityParentId, "");
		
		City c = aQuery.findUnique();
		assertEquals(c1, c);
		
	}
	
	/**
	 * Test that sending a -1 in to both cityId ad cityParentId will throw the correct exception
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyCity() throws Exception
	{
		Query<City> aQuery = City.find.query();
		
		City.addScgCodeToQuery(aQuery.where(), -1, -1, "");
		
		aQuery.findUnique();
	}
	
}
