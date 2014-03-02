package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "city_vacancies")
public class CityVacancy extends Model
{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * This is the primary identifier for the entry
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "city_vacancy_id")
	public int cityVacancyId;
	
	/**
	 * The city for this vacancy
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
	 * The year that the vacancy references
	 */
	@Column(name = "ref_year", nullable = false)
	public int referenceYear;
	
	/**
	 * the actual amount paid for the rental
	 */
	@Column(name = "vacancy_rate", nullable = false)
	public float vacancyRate;
	
	public static Finder<Integer, CityVacancy> find = new Finder<Integer, CityVacancy>(
			Integer.class, CityVacancy.class);
	
}
