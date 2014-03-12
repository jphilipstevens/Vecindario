import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import models.City;
import models.NewHousingPriceIndex;

import org.junit.Test;

import base.BaseTestSuit;
import static org.junit.Assert.*;
import controllers.api.form.PriceIndexForm;
import static org.hamcrest.CoreMatchers.*;

/**
 * This is some unit testing for the Buyer Models
 * 
 * @author jono
 * 
 */
public class BuyerModelTests extends BaseTestSuit
{

	private List<NewHousingPriceIndex> indexesToTest;
	private List<NewHousingPriceIndex> invalidIndexes;
	private int TEST_YEAR = 2014;

	/**
	 * Adding my test data
	 */
	@Override
	public void setupDelegate()
	{
		indexesToTest = createIndexes(TEST_YEAR, c1);

		invalidIndexes = new ArrayList<>();
		invalidIndexes.addAll(createIndexes((TEST_YEAR - 1), c1));
		invalidIndexes.addAll(createIndexes((TEST_YEAR), c2));

	}

	/**
	 * Build a set of indexes, save them to the DB for a specific year
	 * 
	 * @param year
	 *            the reference year tp make the index for
	 * @return the list of indexes
	 */
	private List<NewHousingPriceIndex> createIndexes(int year, City city)
	{
		List<NewHousingPriceIndex> indexes = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		float pIndex = 0.0f;
		for (int i = 0; i < 12; i++)
		{
			NewHousingPriceIndex index = new NewHousingPriceIndex();
			index.city = city;
			index.province = city.province;

			cal.set(year, 0, 1);

			index.referenceDate = new java.sql.Date(cal.getTime().getTime());

			index.priceIndex = (pIndex + 0.1f);
			pIndex += 0.1f;

			index.save();

			indexes.add(index);
		}
		return indexes;
	}

	
	/**
	 * Test that the getIndexByRequest works as we expect
	 */
	@Test
	public void testBuyer()
	{

		PriceIndexForm form = new PriceIndexForm();
		form.setScgCode5(c1.cityId);
		form.setScgCode7(c1.cityParentId);
		form.setYearOfPurchase(TEST_YEAR);
		List<NewHousingPriceIndex> testResult = NewHousingPriceIndex
				.getIndexForRequest(form);

		assertEquals(indexesToTest, testResult);
	}

	/**
	 * Test that the getIndexByRequest works as we expect
	 */
	@Test
	public void testIndexAverage()
	{

		PriceIndexForm form = new PriceIndexForm();
		form.setScgCode5(c1.cityId);
		form.setScgCode7(c1.cityParentId);
		form.setYearOfPurchase(TEST_YEAR);
		List<NewHousingPriceIndex> testResult = NewHousingPriceIndex
				.getIndexForRequest(form);

		double testAvg = NewHousingPriceIndex
				.calculateYearlyAvgIndex(testResult);
		
		assertThat(Math.round(testAvg * 100.0) / 100.0, is(0.65));
	}
}
