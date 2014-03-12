package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

import com.theEd209s.utils.StringUtils;

/**
 * Model for building types
 * 
 * @author JStevens
 * 
 */
@Entity
@Table(name = "building_types")
public class BuildingType extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The primary identifier for the object
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "building_type_id")
	public int buildingTypeId;
	
	/**
	 * A simple abbreviation for the building type name
	 */
	@Column(name = "abbreviation", nullable = false, length = 3)
	public String abbreviation;
	
	/**
	 * Finder class for interacting with the datasource
	 */
	public static Finder<Integer, BuildingType> find = new Finder<Integer, BuildingType>(Integer.class, BuildingType.class);
	
	/**
	 * This method will be used to determine the
	 * abbreviation for a building type entry name.
	 * 
	 * @param entryName
	 * The name of the {@link BuildingType} entry.
	 * 
	 * @return
	 * The abbreviation for the specified
	 * {@link BuildingType} entry.
	 * */
	public static String determineAbbr(String entryName)
	{
		if (!StringUtils.isNullOrEmpty(entryName))
		{
			final StringBuilder entryAbbr = new StringBuilder();
			entryName = entryName.trim().toLowerCase();
			if (entryName.contains("row and appartment"))
			{
				entryAbbr.append("RA");
			}
			if (entryName.contains("row structures"))
			{
				entryAbbr.append("RS");
			}
			if (entryName.contains("appartment structures"))
			{
				entryAbbr.append("AS");
			}
			if (entryName.contains("one unit"))
			{
				entryAbbr.append("1");
			}
			else if (entryName.contains("two unit"))
			{
				entryAbbr.append("2");
			}
			else if (entryName.contains("three unit"))
			{
				entryAbbr.append("3");
			}
			else if (entryName.contains("four unit"))
			{
				entryAbbr.append("4");
			}
			else if (entryName.contains("five unit"))
			{
				entryAbbr.append("5");
			}
			else if (entryName.contains("six unit"))
			{
				entryAbbr.append("6");
			}
			return entryAbbr.toString();
		}
		return null;
	}
	
	/**
	 * This will load all {@link BuildingType} objects from the database.
	 * 
	 * @return
	 * All {@link BuildingType} objects from the database.
	 * */
	public static List<BuildingType> loadAllBuildingTypes()
	{
		return BuildingType.find.all();
	}
	
	/**
	 * Get the building type entry based on the abbreviation
	 * @param abbr
	 * @return
	 */
	public static BuildingType getByAbbreviation(String abbr)
	{
		return find.where().eq("abbreviation", abbr).findUnique();
	}
	
}
