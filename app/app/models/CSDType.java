package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "csd_types")
public class CSDType extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public int id;
	
	@Column(name = "name", nullable = false)
	public String name;
	
	@Column(name = "accronym", nullable = false, length = 4)
	public String accronym;
	
	public static Finder<Integer, CSDType> find = new Finder<Integer, CSDType>(
			Integer.class, CSDType.class);
	
	/**
	 * 
	 * @param accronym
	 * @return
	 */
	public static CSDType getCSDByAccronym(String accronym)
	{
		return CSDType.find.where().eq("accronym", accronym).findUnique();
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<String> getAllCSDAccronyms()
	{
		List<String> acrnyms = new ArrayList<String>();
		List<CSDType> csdAcros = find.select("accronym").findList();
		for(CSDType acro: csdAcros)
		{
			acrnyms.add(acro.accronym);
		}
		return acrnyms;
	}	
	
}
