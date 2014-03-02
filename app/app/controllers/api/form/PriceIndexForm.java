package controllers.api.form;

import java.util.Map;

import play.Logger;
import play.data.validation.Constraints.Required;

/**
 * A Form used to represent a request for price index stats.
 * 
 * @author JStevens
 */
public class PriceIndexForm
{
	
	/**
	 * 
	 */
	@Required
	private int scgCode7;
	
	/**
	 * The int number for the scg code of your desired location
	 */
	@Required
	private int scgCode5;
	
	/**
	 * the price to convert
	 */
	@Required
	private double price;
	
	/**
	 * The year of the new house purchase
	 */
	@Required
	private int yearOfPurchase;
	
	public int getScgCode5()
	{
		return scgCode5;
	}
	
	public void setScgCode5(int scgCode)
	{
		this.scgCode5 = scgCode;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	public void setPrice(double price)
	{
		this.price = price;
	}
	
	public int getYearOfPurchase()
	{
		return yearOfPurchase;
	}
	
	public void setYearOfPurchase(int yearOfPurchase)
	{
		this.yearOfPurchase = yearOfPurchase;
	}
	
	public int getScgCode7()
	{
		return scgCode7;
	}
	
	public void setScgCode7(int scgCode7)
	{
		this.scgCode7 = scgCode7;
	}
	
	/**
	 * Binds the data to a new form
	 * 
	 * @param parameters
	 *            the parameters. The data here is an array but we only care
	 *            about the 0-index value...any others are ignored
	 * @return a new {@link PriceIndexForm} or null if any binding issues
	 *         happened
	 */
	public static PriceIndexForm bind(Map<String, String[]> parameters)
	{
		PriceIndexForm form = new PriceIndexForm();
		try
		{
			if (parameters.get("price") == null)
			{
				return null;
			}
			form.price = Double.parseDouble(parameters.get("price")[0]);
			
			if (parameters.get("scgCode5") == null)
			{
				return null;
			}
			form.scgCode5 = Integer.parseInt(parameters.get("scgCode5")[0]);
			
			if (parameters.get("scgCode7") == null)
			{
				return null;
			}
			form.scgCode7 = Integer.parseInt(parameters.get("scgCode7")[0]);
			
			if (parameters.get("yearOfPurchase") == null)
			{
				return null;
			}
			form.yearOfPurchase = Integer.parseInt(parameters.get("yearOfPurchase")[0]);
			
		}
		catch (Exception e)
		{
			Logger.debug("Could not parse form", e);
			return null;
		}
		return form;
	}
	
}
