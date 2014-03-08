package base;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.avaje.ebean.Ebean;

import play.test.FakeApplication;
import play.test.Helpers;
import models.City;
import models.Province;

/**
 * This is a base class that allows unit tests to easily setup data. Thanks to
 * http
 * ://blog.matthieuguillermin.fr/2012/03/unit-testing-tricks-for-play-2-0-and
 * -ebean/ for the setup
 * 
 * @author jono
 * 
 */
public abstract class BaseTestSuit
{

	public static FakeApplication app;
	public static String createDdl = "";
	public static String dropDdl = "";

	protected Province pr1;
	protected City c1;

	protected Province pr2;
	protected City c2;

	@BeforeClass
	public static void startApp() throws IOException
	{
		app = fakeApplication(inMemoryDatabase());
		Helpers.start(app);

		// Reading the evolution file
		String evolutionContent = FileUtils.readFileToString(app
				.getWrappedApplication().getFile(
						"conf/evolutions/default/1.sql"));

		// Splitting the String to get Create & Drop DDL
		String[] splittedEvolutionContent = evolutionContent
				.split("# --- !Ups");
		String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
		createDdl = upsDowns[0];
		dropDdl = upsDowns[1];
	}

	@AfterClass
	public static void stopApp()
	{
		Helpers.stop(app);
	}

	/**
	 * Adds 2 provinces and 2 cities, then call delegate setup
	 */
	@Before
	public void setup()
	{
		Ebean.execute(Ebean.createCallableSql(dropDdl));
		Ebean.execute(Ebean.createCallableSql(createDdl));

		pr1 = new Province();
		pr1.provinceId = 1;
		pr1.abbreviation = "P1";
		pr1.save();

		c1 = new City();
		c1.cityId = 11;
		c1.cityParentId = 1;
		c1.cityName = "Fake1";
		c1.province = pr1;
		c1.save();

		pr2 = new Province();
		pr2.provinceId = 2;
		pr2.abbreviation = "P2";
		pr2.save();

		c2 = new City();
		c2.cityId = 22;
		c2.cityParentId = 2;
		c2.cityName = "Fake2";
		c2.province = pr2;
		c2.save();

		setupDelegate();

	}

	/**
	 * This is called after the base setup so the cities and provinces are
	 * Guaranteed to be saved
	 */
	public abstract void setupDelegate();
}
