package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.theEd209s.utils.StringUtils;

import play.db.ebean.Model;

/**
 * A model to represent cities and their SGC codes
 * 
 * @author JStevens
 * 
 */
@Entity
@Table(name = "cities")
public class City extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "city_id", nullable = false)
	public int cityId;
	
	@Column(name = "province_id", nullable = false)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "province_id")
	public Province province;
	
	@Column(name = "city_parent_id", nullable = false)
	public int cityParentId;
	
	@Column(name = "city_name", nullable = false, length = 60)
	public String cityName;
	
	public static Finder<Integer, City> find = new Finder<Integer, City>(Integer.class, City.class);
	
	/**
	 * This will load all {@link City} objects from the database.
	 * 
	 * @return All {@link City} objects from the database.
	 * */
	public static List<City> loadAllCities()
	{
		return City.find.all();
	}
	
	/**
	 * Build the OR clause we need since we could ask for the cityID or the
	 * cityParentID
	 * 
	 * @param where
	 *            your where clause to add the expression to
	 * @param cityId
	 *            the city to filter. Note values <= 0 will result in a no-op
	 *            for this
	 * @param cityParentId
	 *            the cityParentId to filter. Note values <= 0 will result in a
	 *            no-op for this
	 * 
	 * @return the where clause with the updates
	 * 
	 * @exception throws an exception should the cityId or cityParentId both be -1
	 */
	public static <T> ExpressionList<T> addScgCodeToQuery(ExpressionList<T> where, int cityId, int cityParentId, String tableName) throws Exception
	{
		String columnPrefix = "";
		
		if (!StringUtils.isNullOrEmpty(tableName))
		{
			columnPrefix = tableName + ".";
		}
		
		if (cityId <= 0 && cityParentId <= 0)
		{
			throw new IllegalArgumentException("cityId and cityParentId cannot both be -1");
		}
		
		return where.or(Expr.eq(columnPrefix + "cityId", cityId), Expr.eq(columnPrefix + "cityParentId", cityParentId));
		
	}
	
	/**
	 * Check if a value exits and then save to avoid duplicate constrain
	 * exception
	 */
	public void checkAndSave()
	{
		boolean cityExists = (City.find.where().eq("cityId", this.cityId).findRowCount() >= 1) ? true : false;
		if (!cityExists)
		{
			this.save();
		}
	}
	
}
