package controllers.api.form;

import java.util.Map;

import play.Logger;
import play.data.validation.Constraints.Required;

/**
 * A Form used to represent a request rental prices.
 * 
 * @author Matthew Weiler
 */
public class RentalPricesForm
{
	
	/* PRIVATE VARIABLES */
	/**
	 * The int number for the scg5 code of your desired location.
	 */
	@Required
	private int scgCode5;
	
	/**
	 * The int number for the scg7 code of your desired location.
	 */
	@Required
	private int scgCode7;
	
	/* PUBLIC METHODS */
	/**
	 * This will get the number for the scg5 code of your desired location.
	 * 
	 * @return
	 * The number for the scg5 code of your desired location.
	 */
	public int getScgCode5()
	{
		return scgCode5;
	}
	
	/**
	 * This will set the number for the scg5 code of your desired location.
	 * 
	 * @param scgCode5
	 * The number for the scg5 code of your desired location.
	 */
	public void setScgCode5(int scgCode5)
	{
		this.scgCode5 = scgCode5;
	}
	
	/**
	 * This will get the number for the scg7 code of your desired location.
	 * 
	 * @return
	 * The number for the scg7 code of your desired location.
	 */
	public int getScgCode7()
	{
		return scgCode7;
	}
	
	/**
	 * This will set the number for the scg7 code of your desired location.
	 * 
	 * @param scgCode7
	 * The number for the scg7 code of your desired location.
	 */
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
	 * @return a new {@link RentalPricesForm} or null if any binding issues
	 *         happened
	 */
	public static RentalPricesForm bind(Map<String, String[]> parameters)
	{
		try
		{
			if ((parameters.get("scgCode5") != null) || (parameters.get("scgCode7") != null))
			{
				RentalPricesForm form = new RentalPricesForm();
				if (parameters.get("scgCode5") != null)
				{
					form.scgCode5 = Integer.parseInt(parameters.get("scgCode5")[0]);
				}
				else if (parameters.get("scgCode7") == null)
				{
					form.scgCode7 = Integer.parseInt(parameters.get("scgCode7")[0]);
				}
				return form;
			}

		} catch (Exception e)
		{
			Logger.debug("Could not parse form", e);
			return null;
		}
		return null;
	}
	
}
