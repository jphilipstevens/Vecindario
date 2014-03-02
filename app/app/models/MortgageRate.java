package models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "mortgage_rate")
public class MortgageRate extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The primary identifier for the entry
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mortgage_rate_id", nullable = false)
	public int mortgageRateId;
	
	/**
	 * the reference date for the rate
	 */
	@Column(name = "ref_date", nullable = false)
	public Date referenceDate;
	
	@Column(name = "rate", nullable = false)
	public float rate;
	
	public static Finder<Integer, MortgageRate> find = new Finder<Integer, MortgageRate>(
			Integer.class, MortgageRate.class);
	
}
