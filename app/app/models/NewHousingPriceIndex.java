package models;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

import controllers.api.form.PriceIndexForm;

@Entity
@Table(name = "new_house_price_indexes")
public class NewHousingPriceIndex extends Model
{

	private static final long serialVersionUID = 1L;

	/**
	 * The primary identifier for the entry
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "new_house_price_index_id", nullable = false)
	public int newHousePriceIndexId;

	/**
	 * The city id for the new house price index.
	 */
	@Column(name = "city_id", nullable = false)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "city_id")
	public City city;

	/**
	 * The province id for the new house price index.
	 */
	@Column(name = "province_id", nullable = false)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "province_id")
	public Province province;

	/**
	 * The reference date for the new house price index.
	 */
	@Column(name = "ref_date", nullable = false)
	public Date referenceDate;

	/**
	 * The price index for the new house price index.
	 */
	@Column(name = "price_index", nullable = false)
	public float priceIndex;

	public static Finder<Integer, NewHousingPriceIndex> find = new Finder<Integer, NewHousingPriceIndex>(
			Integer.class, NewHousingPriceIndex.class);

	/**
	 * given the index form request get us the corresponding index
	 * 
	 * @param request
	 *            the request to filter for
	 * @return the first NewHousingPriceIndex, if request is null then null is
	 *         returned
	 */
	public static List<NewHousingPriceIndex> getIndexForRequest(
			PriceIndexForm request)
	{
		if (request == null)
		{
			return null;
		}
		try
		{
			Query<NewHousingPriceIndex> query = find.query().fetch("city", "*",
					new FetchConfig().query());
			City.addScgCodeToQuery(query.where(), request.getScgCode5(),
					request.getScgCode7(), "city");

			Calendar cal = Calendar.getInstance();
			cal.set(request.getYearOfPurchase(), 0, 1);
			
			long startTime = cal.getTime().getTime();
			
			cal.set(request.getYearOfPurchase(), 11, 31);
			long endTime = cal.getTime().getTime();
			query.where().in("referenceDate", new Date(startTime),
					new Date(endTime));

			List<NewHousingPriceIndex> result = query.findList();
			return result == null ? new ArrayList<NewHousingPriceIndex>()
					: result;
		} catch (Exception e)
		{
			Logger.error("", e);
		}
		return null;
	}

	/**
	 * Get the average yearly index for the list
	 * 
	 * @param indexes
	 *            the list of indexes for a year
	 * @return 0 if the indexes are not available or the avg index over the time
	 *         period
	 */
	public static double calculateYearlyAvgIndex(
			List<NewHousingPriceIndex> indexes)
	{
		if (indexes == null || indexes.size() == 0)
		{
			return 0;
		}
		double result = 0;

		for (NewHousingPriceIndex anIndex : indexes)
		{
			result += anIndex.priceIndex;
		}

		return (result / indexes.size());
	}
}
