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
 * This is a unit type model for a dwelling
 * 
 * @author JStevens
 * 
 */
@Entity
@Table(name = "unit_types")
public class UnitType extends Model
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unit_type_id")
	public int unitTypeId;
	
	/**
	 * A simple abbreviation for the unit type name
	 */
	@Column(name = "abbreviation", nullable = false, length = 2, unique = true)
	public String abbreviation;
	
	/**
	 * Finder class for interacting with the datasource
	 */
	public static Finder<Integer, UnitType> find = new Finder<Integer, UnitType>(Integer.class, UnitType.class);
	
	/**
	 * This method will be used to determine the
	 * abbreviation for a unit type entry name.
	 * 
	 * @param entryName
	 * The name of the {@link UnitType} entry.
	 * 
	 * @return
	 * The abbreviation for the specified
	 * {@link UnitType} entry.
	 * */
	public static String determineAbbr(String entryName)
	{
		if (!StringUtils.isNullOrEmpty(entryName))
		{
			final StringBuilder entryAbbr = new StringBuilder();
			entryName = entryName.trim().toLowerCase();
			if (entryName.contains("bachelor"))
			{
				entryAbbr.append("BA");
			}
			else if (entryName.contains("one bedroom"))
			{
				entryAbbr.append("B1");
			}
			else if (entryName.contains("two bedroom"))
			{
				entryAbbr.append("B2");
			}
			else if (entryName.contains("three bedroom"))
			{
				entryAbbr.append("B3");
			}
			else if (entryName.contains("four bedroom"))
			{
				entryAbbr.append("B4");
			}
			else if (entryName.contains("five bedroom"))
			{
				entryAbbr.append("B5");
			}
			else if (entryName.contains("six bedroom"))
			{
				entryAbbr.append("B6");
			}
			return entryAbbr.toString();
		}
		return null;
	}
	
	/**
	 * This will load all {@link UnitType} objects from the database.
	 * 
	 * @return
	 * All {@link UnitType} objects from the database.
	 * */
	public static List<UnitType> loadAllUnitTypes()
	{
		return UnitType.find.all();
	}
	
	/**
	 * Get the unique unit type by abbreviation
	 * @param abbr the abbreviation we want to fetch
	 * @return a unique abbr, or null if it does not exist
	 */
	public static UnitType getByAbbreviation(String abbr)
	{
		return find.where().eq("abbreviation", abbr).findUnique();
	}
	
}
