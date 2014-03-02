package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

import play.db.ebean.Model;

/**
 * This is the model for representing a province
 * 
 * @author JStevens
 * 
 */
@Entity
@Table(name = "provinces")
public class Province extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "province_id", nullable = false)
	public int provinceId;
	
	@Column(name = "abbreviation", nullable = false, length = 2)
	public String abbreviation;
	
	public static Finder<Integer, Province> find = new Finder<Integer, Province>(
			Integer.class, Province.class);
	
	/**
	 * Gets the province item matching the id
	 * @param provinceid id to match
	 * @return
	 */
	public static Province getProvinceById(int provinceid)
	{
		return Province.find.where().eq("provinceId", provinceid).findUnique();
	}
	
}
