package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.Logger;
import play.db.ebean.Model;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;

import controllers.api.form.RentalPricesForm;

/**
 * A model representation of a rental rate
 * 
 * @author JStevens
 * 
 */
@Entity
@Table(name = "rental_rate")
public class RentalRate extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rental_rate_id")
	public int rentalRateId;
	
	/**
	 * The building type that the rental rate is for
	 */
	@Column(name = "building_type_id", nullable = false)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "building_type_id")
	public BuildingType buildingType;
	
	/**
	 * The unit type this rental rate is for
	 */
	@Column(name = "unit_type_id", nullable = false)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "unit_type_id")
	public UnitType unitType;
	
	/**
	 * The city that this represents
	 */
	@Column(name = "city_id", nullable = false)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "city_id")
	public City city;
	
	/**
	 * The province that this represents
	 */
	@Column(name = "province_id", nullable = false)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "province_id")
	public Province province;
	
	/**
	 * The year that the rental rate references
	 */
	@Column(name = "ref_year", nullable = false)
	public int referenceYear;
	
	/**
	 * the actual amount paid for the rental
	 */
	@Column(name = "rental_rate", nullable = false)
	public float rentalRate;
	
	public static Finder<Integer, RentalRate> find = new Finder<Integer, RentalRate>(Integer.class, RentalRate.class);
	
	/**
	 * This will return all {@link RentalRate}s that are found
	 * specified by the {@link RentalPricesForm}.
	 * 
	 * @param request
	 * The {@link RentalPricesForm} to use for filtering.
	 * 
	 * @return
	 * The {@link Map} of {@link List}s of all {@link RentalRate}s that are
	 * found specified by the {@link RentalPricesForm}.
	 * <br />
	 * The {@link Map} key is the reference year and the {@link Map} value is
	 * the list of {@link RentalRate}s for that year.
	 * */
	public static Map<Integer, List<RentalRate>> getRentalRatesForRequest(final RentalPricesForm request)
	{
		if (request != null)
		{
			try
			{
				final Query<RentalRate> query = RentalRate.find.query().fetch("city", "*", new FetchConfig().query());
				City.addScgCodeToQuery(query.where(), request.getScgCode5(), request.getScgCode7(), "city");
				query.orderBy().desc("referenceYear");
				final List<RentalRate> allResults = query.findList();
				final Map<Integer, List<RentalRate>> groupedResults = new HashMap<Integer, List<RentalRate>>();
				if ((allResults != null) && (allResults.size() > 0))
				{
					for (RentalRate rentalRateResult : allResults)
					{
						if (!groupedResults.containsKey(rentalRateResult.referenceYear))
						{
							groupedResults.put(rentalRateResult.referenceYear, new ArrayList<RentalRate>());
						}
						groupedResults.get(rentalRateResult.referenceYear).add(rentalRateResult);
					}
				}
				return groupedResults == null ? new HashMap<Integer, List<RentalRate>>() : groupedResults;
			}
			catch (Exception e)
			{
				Logger.error("Failed to get all rental rate stats for: " + request.getScgCode5() + " & " + request.getScgCode7(), e);
			}
		}
		return null;
	}
	
}
