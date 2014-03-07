package controllers.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import models.CSDIndex;
import models.CityVacancy;
import models.NewHousingPriceIndex;
import models.Province;
import models.RentalRate;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theEd209s.utils.SortUtils;
import com.theEd209s.utils.StringUtils;

import controllers.api.form.PriceIndexForm;
import controllers.api.form.RentalPricesForm;

/**
 * API to query the back-end and get JSON results
 * 
 * @author JStevens
 */
public class QueryApi extends Controller
{
	
	/**
	 * This enum has possible response codes we can return
	 * @author JStevens
	 *
	 */
	public enum ResponseStates
	{
		SUCCESS("success"), FAILURE("failure");
		private String statusCode;
		
		private ResponseStates(String s)
		{
			statusCode = s;
		}
		
		public String getStatusCode()
		{
			return statusCode;
		}
		
	}
	
	private static final int MAX_ROWS = 25;
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getLocationFromPlaceName(String name)
	{
		ObjectNode baseNode = null;
		if (!StringUtils.isNullOrEmpty(name))
		{
			baseNode = buildSuccessResponseObject("Results for Place Name Search");
			List<CSDIndex> results = CSDIndex.getPlacesByName(name, MAX_ROWS);
			baseNode.put("count", results.size());
			
			List<ObjectNode> jsSequence = new ArrayList<ObjectNode>();
			for (CSDIndex index : results)
			{
				Province prov = Province.getProvinceById(index.provinceCode);
				ObjectNode csdNode = Json.newObject();
				csdNode.put("name", index.placeName);
				csdNode.put("csdType", index.csdType);
				csdNode.put("province", prov.abbreviation);
				csdNode.put("scgCode5", CSDIndex.getSCGCode5(index));
				csdNode.put("scgCode7", CSDIndex.getSCGCode7(index));
				jsSequence.add(csdNode);
				
			}
			baseNode.put("places", Json.toJson(jsSequence));
		}
		else
		{
			baseNode = buildFailedResponseObject("You cannot serach on an empty string for places");
			baseNode.put("count", 0);
		}
		
		return ok(baseNode);
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getInvestorResult(final int scgcode)
	{
		// fetch the City's from the province
				Province pr = Province.getProvinceById(scgcode);
				
				// fetch the vacancy, rental rates and house pricing index for each city
				List<RentalRate> rentalRates = RentalRate.getRentalRateOnLocation(pr,MAX_ROWS);
				
				List<ObjectNode> jsSequence = new ArrayList<ObjectNode>();
				ObjectNode baseNode = null;
				
				if (rentalRates != null && !rentalRates.isEmpty())
				{
					baseNode = buildSuccessResponseObject("Results for Invester Search");
					baseNode.put("count", rentalRates.size());
					
					for (RentalRate rate : rentalRates) 
					{
						ObjectNode csdNode = Json.newObject();
						csdNode.put("year",rate.referenceYear);
						csdNode.put("rentalRate",rate.rentalRate);				
						
						CityVacancy vacancy = CityVacancy.getVacancyRateOnLocationAndYear(rate.city,rate.referenceYear);
						float vacancyRate =  vacancy != null ? vacancy.vacancyRate : 0;
						csdNode.put("vacancyRate",vacancyRate);				
						
						csdNode.put("city", rate.city.cityName);
						csdNode.put("province",rate.province.abbreviation);
						
						List<NewHousingPriceIndex> houseIndexes = NewHousingPriceIndex.
																  gethousePriceIndexOnyear(rate.referenceYear,
																  rate.city.cityId);
						
						csdNode.put("avgPriceIndex2007",  NewHousingPriceIndex.calculateYearlyAvgIndex(houseIndexes));
						
						csdNode.put("unitType", rate.unitType.abbreviation);
						csdNode.put("buildingType", rate.buildingType.abbreviation);
						
						jsSequence.add(csdNode);				
					}
					
					baseNode.put("rows", Json.toJson(jsSequence));
				}
				else
				{
					baseNode = buildFailedResponseObject("No invester information is available");
				}	
						
				return ok(baseNode);
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getRenterResult(final int scgCode5, final int scgCode7)
	{
		if ((scgCode5 < 0) && (scgCode7 < 0))
		{
			return badRequest(Json.stringify(buildFailedResponseObject("You must specify a valid scgCode5 and/or scgCode7.")));
		}
		final ObjectNode baseNode = buildSuccessResponseObject("Rental Rates for SCG: " + scgCode5 + " & " + scgCode7);
		final RentalPricesForm form = RentalPricesForm.bind(request().queryString());
		form.setScgCode5(scgCode5);
		form.setScgCode7(scgCode7);
		Map<Integer, List<RentalRate>> groupedRentalRates = RentalRate.getRentalRatesForRequest(form);
		if ((groupedRentalRates != null) && (groupedRentalRates.size() > 0))
		{
			int resultSize = 0;
			int yearCount = 0;
			final ArrayNode baseYearsNode = baseNode.arrayNode();
			ObjectNode tmpYearNode = null;
			ArrayNode tmpYearListNode = null;
			ObjectNode tmpRentalRateNode = null;
			
			for (Integer year : SortUtils.sortDesc(groupedRentalRates.keySet()))
			{
				resultSize += groupedRentalRates.get(year).size();
				yearCount++;
				tmpYearNode = Json.newObject();
				tmpYearListNode = tmpYearNode.arrayNode();
				for (RentalRate rentalRate : groupedRentalRates.get(year))
				{
					tmpRentalRateNode = Json.newObject();
					tmpRentalRateNode.put("buildingType", rentalRate.buildingType.abbreviation);
					tmpRentalRateNode.put("unitType", rentalRate.unitType.abbreviation);
					tmpRentalRateNode.put("rentalRate", rentalRate.rentalRate);
					tmpYearListNode.add(Json.toJson(tmpRentalRateNode));
				}
				tmpYearNode.put("year", year);
				tmpYearNode.put("rentalRates", tmpYearListNode);
				baseYearsNode.add(Json.toJson(tmpYearNode));
			}
			baseNode.put("resultSize", resultSize);
			baseNode.put("yearCount", yearCount);
			baseNode.put("years", Json.toJson(baseYearsNode));
		}
		else
		{
			baseNode.put("resultSize", 0);
		}
		return ok(Json.stringify(baseNode));
	}
	
	/**
	 * GET the price of a home corrected for the 2007 new price index
	 * @param price the price that the new house was purchased at
	 * @param scgCode the scg code for the location of the house
	 * @param yearOfPurchase the year the house was purchased
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getIndexedHosuePrice2007(double price, int scgCode5, int scgCode7, int yearOfPurchase)
	{
		if (price < 0)
		{
			return badRequest(Json.stringify(buildFailedResponseObject("You cannot have a price <= 0")));
		}
		
		if (scgCode5 <= 0 && scgCode7 <= 0)
		{
			return badRequest(Json.stringify(buildFailedResponseObject("You cannot have a scgCode < 0")));
		}
		
		if (yearOfPurchase < 0)
		{
			return badRequest(Json.stringify(buildFailedResponseObject("You cannot have a yearOfPurchase < 0")));
		}
		
		//Get the index for the year of purchase
		PriceIndexForm form = new PriceIndexForm();
		form.setPrice(price);
		form.setScgCode5(scgCode5);
		form.setYearOfPurchase(yearOfPurchase);
		
		List<NewHousingPriceIndex> indexes = NewHousingPriceIndex.getIndexForRequest(form);
		double avgYearlyindexForPurchaseYear = NewHousingPriceIndex.calculateYearlyAvgIndex(indexes);
		
		// get the index for the current year
		PriceIndexForm formCurrentYear = new PriceIndexForm();
		formCurrentYear.setYearOfPurchase(Calendar.getInstance().get(Calendar.YEAR));
		formCurrentYear.setScgCode5(form.getScgCode5());
		List<NewHousingPriceIndex> indexesCurrentYear = NewHousingPriceIndex.getIndexForRequest(form);
		double avgIndexCurrentYear = NewHousingPriceIndex.calculateYearlyAvgIndex(indexesCurrentYear);
		
		ObjectNode node = null;
		
		if (avgYearlyindexForPurchaseYear > 0 && avgIndexCurrentYear > 0)
		{
			node = buildSuccessResponseObject("Average Yearly Index");
			node.put("locationSGC", indexes.get(0).city.cityParentId);
			node.put("locationName", indexes.get(0).city.cityName);
			double currentHouseValue = form.getPrice();
			currentHouseValue *= avgYearlyindexForPurchaseYear;
			currentHouseValue *= avgIndexCurrentYear;
			node.put("adjustedPrice", currentHouseValue);
			node.put("avgYearlyIndex", (currentHouseValue / form.getPrice()) * 100);
		}
		else
		{
			node = buildFailedResponseObject("No Index information is available for your location");
			node.put("locationSGC", indexes.get(0).city.cityParentId);
			node.put("locationName", indexes.get(0).city.cityName);
			node.put("adjustedPrice", -1);
			node.put("avgYearlyIndex", -1);
		}
		
		return ok(Json.stringify(node));
	}
	
	/**
	 * Build a success object node
	 * @param message some message for the success
	 * @return a new Object node to represent a success
	 */
	private static ObjectNode buildSuccessResponseObject(String message)
	{
		ObjectNode node = Json.newObject();
		node.put("result", ResponseStates.SUCCESS.statusCode);
		node.put("message", message);
		return node;
	}
	
	/**
	 * Build a failed object node
	 * @param message some message for the success
	 * @return a new Object node to represent a success
	 */
	private static ObjectNode buildFailedResponseObject(String message)
	{
		ObjectNode node = Json.newObject();
		node.put("result", ResponseStates.FAILURE.statusCode);
		node.put("message", message);
		return node;
	}
	
}
