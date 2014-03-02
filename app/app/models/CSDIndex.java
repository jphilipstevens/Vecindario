package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.ebean.Model;

/**
 * a representation of an agglomeration from the 2011 SGC
 * 
 * @author JStevens
 * 
 */
@Entity
@Table(name = "csd_indexes", uniqueConstraints = { @UniqueConstraint(columnNames = { "csd_type", "pr", "cd", "csd", "ca" }) })
public class CSDIndex extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public long id;
	
	@Column(name = "place_name", nullable = false, length = 60)
	public String placeName;
	
	@Column(name = "csd_type", length = 4)
	public String csdType;
	
	@Column(name = "pr", nullable = false)
	public int provinceCode;
	
	@Column(name = "cd", nullable = false)
	public int censusDivision;
	
	@Column(name = "csd", nullable = false)
	public int censusSubDivision;
	
	@Column(name = "ca", nullable = false)
	public int censusAgglomeration;
	
	public static Finder<Integer, CSDIndex> find = new Finder<Integer, CSDIndex>(Integer.class, CSDIndex.class);
	
	/**
	 * Gets place_name, PR,CD, CSD and CA which combine to firm 7 digit cityid 
	 * and 5 digit parentid for cities
	 * @return CSDIndex list with place_name, PR,CD, CSD and CA
	 */
	public static List<CSDIndex> getAllCityNamesAndIds()
	{
		return CSDIndex.find.select("placeName, provinceCode, censusDivision, censusSubDivision, censusAgglomeration").findList();
	}
	
	/**
	 * Search the names field for places
	 * @param name a string of names
	 * @param maxrows the max number of rows to fetch
	 * @return list of places
	 */
	public static List<CSDIndex> getPlacesByName(String name, int maxrows)
	{
		return find.where().icontains("placeName", name).setMaxRows(maxrows).findList();
	}
	
	/**
	 * Return the 7 digit code for SCG for this index
	 * @param index the index with all SCG codes
	 * @return the scg code or a blank string if the index is null or its scg codes are missing
	 */
	public static String getSCGCode7(CSDIndex index)
	{
		if (index != null && (index.provinceCode > 0 && index.censusDivision > 0 && index.censusSubDivision > 0))
		{
			return String.format("%02d", index.provinceCode) + "" + String.format("%02d", index.censusDivision) + "" + String.format("%03d", index.censusSubDivision);
		}
		return "";
	}
	
	/**
	 * Return the 7 digit code for SCG for this index
	 * @param index the index with all SCG codes
	 * @return the scg code or a blank string if the index is null or its scg codes are missing
	 */
	public static String getSCGCode5(CSDIndex index)
	{
		if (index != null && (index.provinceCode > 0 && index.censusAgglomeration > 0))
		{
			return String.format("%02d", index.provinceCode) + "" + String.format("%03d", index.censusAgglomeration);
		}
		return "";
	}
	
}
