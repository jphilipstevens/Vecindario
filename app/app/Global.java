import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.persistence.Table;

import models.BuildingType;
import models.CSDIndex;
import models.CSDType;
import models.City;
import models.CityVacancy;
import models.CkanRevision;
import models.MortgageRate;
import models.NewHousingPriceIndex;
import models.Province;
import models.RentalRate;
import models.UnitType;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.theEd209s.dataLoading.DataLoader;
import com.theEd209s.dataLoading.dynamicLoaders.MortgageRateLoader;
import com.theEd209s.dataLoading.dynamicLoaders.NewHousingPriceIndexLoader;
import com.theEd209s.dataLoading.dynamicLoaders.RentalRatesLoader;
import com.theEd209s.dataLoading.dynamicLoaders.VacancyRateLoader;
import com.theEd209s.dataLoading.dynamicLoaders.VacancyRateLoader.VacancyData1;
import com.theEd209s.dataLoading.dynamicLoaders.VacancyRateLoader.VacancyData2;
import com.theEd209s.dataLoading.staticLoaders.BuildingTypesLoader;
import com.theEd209s.dataLoading.staticLoaders.CitiesLoader;
import com.theEd209s.dataLoading.staticLoaders.CsdIndexesLoader;
import com.theEd209s.dataLoading.staticLoaders.CsdTypesLoader;
import com.theEd209s.dataLoading.staticLoaders.ProvinceLoader;
import com.theEd209s.dataLoading.staticLoaders.UnitTypesLoader;
import com.theEd209s.utils.StringUtils;

/**
 * Global object is called on the app start. Therefore be careful about any code
 * you add here. It is run every time the server starts!
 * 
 * @author JStevens
 */
public class Global extends GlobalSettings
{

	@Override
	public void onStart(Application app)
	{

		Global.attachCommonUtilsLogger();
		if (!app.isTest())
		{
			/* TRUNCATION */
			if (play.Play.application().configuration()
					.getBoolean("truncateAllStaticTables", false))
			{
				InitData.truncateAllStaticTables();
			}
			if (play.Play.application().configuration()
					.getBoolean("truncateAllDynamicTables", false))
			{
				InitData.truncateAllDynamicTables();
				;
			}

			/* STATIC LOADERS */
			if (play.Play.application().configuration()
					.getBoolean("populateBuildingType", false))
			{
				InitData.checkAndPopulateBuildingType();
			}
			if (play.Play.application().configuration()
					.getBoolean("populateUnitType", false))
			{
				InitData.checkAndPopulateUnitType();
			}
			if (play.Play.application().configuration()
					.getBoolean("populateCSDType", false))
			{
				InitData.checkAndPopulateCSDType();
			}
			if (play.Play.application().configuration()
					.getBoolean("populateCSDIndex", false))
			{
				InitData.checkAndPopulateCSDIndex();
			}
			if (play.Play.application().configuration()
					.getBoolean("populateProvinces", false))
			{
				InitData.checkAndPopulateProvinces();
			}
			if (play.Play.application().configuration()
					.getBoolean("populateCities", false))
			{
				InitData.checkAndPopulateCities();
			}

			/* DYNAMIC LOADING */
			if (play.Play.application().configuration()
					.getBoolean("populateMortgageRates", false))
			{
				InitData.checkAndPopulateMortgageRates();
			}
			if (play.Play.application().configuration()
					.getBoolean("populateRentalRates", false))
			{
				InitData.checkAndPopulateRentalRates();
			}
			if (play.Play.application().configuration()
					.getBoolean("populateNewHousePriceIndexes", false))
			{
				InitData.checkAndPopulateNewHousePriceIndexes();
			}
			if (play.Play.application().configuration()
					.getBoolean("populateVacancyRates", false))
			{
				InitData.checkAndPopulateVacancyRates();
			}
		}
	}

	/* PRIVATE METHODS */
	/**
	 * This method will attach the Common Utilities
	 * {@link com.theEd209s.logging.Logger} to the Play {@link Logger}.
	 * */
	private static void attachCommonUtilsLogger()
	{
		com.theEd209s.logging.Logger.setLogHandler(new Handler()
		{
			@Override
			public void publish(final LogRecord record)
			{
				if ((record != null) && (!Level.OFF.equals(record.getLevel())))
				{
					final Throwable logThrowable = record.getThrown();
					if (Level.CONFIG.equals(record.getLevel())
							|| Level.FINE.equals(record.getLevel())
							|| Level.FINER.equals(record.getLevel())
							|| Level.FINEST.equals(record.getLevel()))
					{
						if (logThrowable != null)
						{
							Logger.debug(record.getMessage(), logThrowable);
						} else
						{
							Logger.debug(record.getMessage());
						}
					} else if (Level.INFO.equals(record.getLevel()))
					{
						if (logThrowable != null)
						{
							Logger.info(record.getMessage(), logThrowable);
						} else
						{
							Logger.info(record.getMessage());
						}
					} else if (Level.WARNING.equals(record.getLevel()))
					{
						if (logThrowable != null)
						{
							Logger.warn(record.getMessage(), logThrowable);
						} else
						{
							Logger.warn(record.getMessage());
						}
					} else if (Level.SEVERE.equals(record.getLevel()))
					{
						if (logThrowable != null)
						{
							Logger.error(record.getMessage(), logThrowable);
						} else
						{
							Logger.error(record.getMessage());
						}
					}
				}
			}

			@Override
			public void flush()
			{

			}

			@Override
			public void close() throws SecurityException
			{

			}
		});

		com.theEd209s.logging.Logger.setLogFormatter(new Formatter()
		{
			@Override
			public String format(LogRecord record)
			{
				if (record != null)
				{
					final StringBuilder loggerMessage = new StringBuilder();
					loggerMessage.append(new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss").format(new Date(record
							.getMillis())));
					// If the name of the logging class is set, include it.
					if ((record.getSourceClassName() != null)
							&& (record.getSourceClassName().length() > 0))
					{
						// If the name of the logging method is set, include it.
						if ((record.getSourceMethodName() != null)
								&& (record.getSourceMethodName().length() > 0))
						{
							loggerMessage.append(" (")
									.append(record.getSourceClassName())
									.append(".")
									.append(record.getSourceMethodName())
									.append(") ");
						}
						// If the name of the logging method is not set, only
						// include the class name.
						else
						{
							loggerMessage.append(" (")
									.append(record.getSourceClassName())
									.append(") ");
						}
					}
					// If the name of the logging method is set, include it.
					else if ((record.getSourceMethodName() != null)
							&& (record.getSourceMethodName().length() > 0))
					{
						loggerMessage.append(" (")
								.append(record.getSourceMethodName())
								.append(") ");
					}
					// Include the actual message being logged.
					loggerMessage.append(record.getMessage());
					return loggerMessage.toString();
				}
				return null;
			}
		});
	}

	/* PRIVATE CLASSES */
	/**
	 * This class contains all methods for initializing data.
	 * */
	private static class InitData
	{

		/* STATIC LOADING */
		public static void truncateAllStaticTables()
		{
			Logger.info("Truncating all static tables ....");
			long startTime = (new Date()).getTime();
			InitData.truncateTable(BuildingType.class);
			InitData.truncateTable(BuildingType.class);
			InitData.truncateTable(UnitType.class);
			InitData.truncateTable(City.class);
			InitData.truncateTable(Province.class);
			InitData.truncateTable(CSDType.class);
			InitData.truncateTable(CSDIndex.class);
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Truncating all static tables completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateBuildingType()
		{
			Logger.info("Truncating building types ....");
			long startTime = (new Date()).getTime();
			InitData.truncateTable(BuildingType.class);
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Truncating building types completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
			Logger.info("Importing building types ....");
			final DataLoader loader = new BuildingTypesLoader();
			startTime = (new Date()).getTime();
			loader.parseFile();
			endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported building types completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateUnitType()
		{
			Logger.info("Truncating unit types ....");
			long startTime = (new Date()).getTime();
			InitData.truncateTable(UnitType.class);
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Truncating unit types completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
			Logger.info("Importing unit types ....");
			final DataLoader loader = new UnitTypesLoader();
			startTime = (new Date()).getTime();
			loader.parseFile();
			endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported unit types completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateCSDType()
		{
			Logger.info("Truncating CSD types ....");
			long startTime = (new Date()).getTime();
			InitData.truncateTable(CSDType.class);
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Truncating CSD types completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
			Logger.info("Importing CSD types ....");
			final DataLoader loader = new CsdTypesLoader();
			startTime = (new Date()).getTime();
			loader.parseFile();
			endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported CSD types completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateCSDIndex()
		{
			Logger.info("Truncating CSD indexes ....");
			long startTime = (new Date()).getTime();
			InitData.truncateTable(CSDIndex.class);
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Truncating CSD indexes completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
			Logger.info("Importing CSD indexes ....");
			final DataLoader loader = new CsdIndexesLoader();
			startTime = (new Date()).getTime();
			loader.parseFile();
			endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported CSD indexes completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateProvinces()
		{
			Logger.info("Truncating Provinces ....");
			long startTime = (new Date()).getTime();
			InitData.truncateTable(Province.class);
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Truncating Provinces completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
			Logger.info("Importing Provinces ....");
			final ProvinceLoader loader = new ProvinceLoader();
			startTime = (new Date()).getTime();
			loader.parseFile();
			endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported Provinces completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateCities()
		{
			Logger.info("Truncating Cities ....");
			long startTime = (new Date()).getTime();
			InitData.truncateTable(City.class);
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Truncating Cities completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}

			Logger.info("Importing cities table ....");

			startTime = (new Date()).getTime();
			CitiesLoader loader = new CitiesLoader();
			loader.populateCities();

			endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported new cities completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		/* DYNAMIC LOADING */
		public static void truncateAllDynamicTables()
		{
			Logger.info("Truncating all dynamic tables ....");
			long startTime = (new Date()).getTime();
			InitData.truncateTable(MortgageRate.class);
			InitData.truncateTable(RentalRate.class);
			InitData.truncateTable(NewHousingPriceIndex.class);
			InitData.truncateTable(CityVacancy.class);
			InitData.truncateTable(CkanRevision.class);
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Truncating all dynamic tables completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateRentalRates()
		{
			Logger.info("Importing rental rates ....");
			final DataLoader loader = new RentalRatesLoader();
			long startTime = (new Date()).getTime();
			loader.parseFile();
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported rental rates completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateMortgageRates()
		{
			Logger.info("Importing mortgage rates ....");
			final DataLoader loader = new MortgageRateLoader();
			long startTime = (new Date()).getTime();
			loader.parseFile();
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported mortgage rates completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateNewHousePriceIndexes()
		{
			Logger.info("Importing new house price indexes ....");
			final DataLoader loader = new NewHousingPriceIndexLoader();
			long startTime = (new Date()).getTime();
			loader.parseFile();
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported new house price indexes completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		public static void checkAndPopulateVacancyRates()
		{
			Logger.info("Importing new vacancy rates 1 ....");
			DataLoader loader = new VacancyRateLoader(VacancyData1.CKAN_URL,
					VacancyData1.CKAN_DATASET_ID, VacancyData1.CKAN_RESOURCE_ID);
			long startTime = (new Date()).getTime();
			loader.parseFile();
			long endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported new vacancy rates 1 completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
			Logger.info("Importing new vacancy rates 2 ....");
			loader = new VacancyRateLoader(VacancyData2.CKAN_URL,
					VacancyData2.CKAN_DATASET_ID, VacancyData2.CKAN_RESOURCE_ID);
			startTime = (new Date()).getTime();
			loader.parseFile();
			endTime = (new Date()).getTime();
			try
			{
				Logger.info("Imported new vacancy rates 2 completed: "
						+ StringUtils.humanReadableElapsedTime(endTime
								- startTime));
			} catch (Exception e)
			{

			}
		}

		/* PRIVATE METHODS */
		/* PRIVATE METHODS */
		/**
		 * This method will truncate the table referenced by the specified
		 * class.
		 * 
		 * @param entityClass
		 *            The {@link Model} class to truncate.
		 * */
		private static void truncateTable(Class<? extends Model> entityClass)
		{
			if (entityClass != null)
			{
				if (entityClass.getAnnotation(Table.class) != null)
				{
					Table table = entityClass.getAnnotation(Table.class);
					final String tableName = table.name();
					if (!StringUtils.isNullOrEmpty(tableName))
					{
						final Transaction tran = Ebean.beginTransaction();
						Connection conn = null;
						try
						{
							conn = tran.getConnection();
							conn.createStatement().executeUpdate(
									"truncate " + tableName + " cascade");
							Ebean.commitTransaction();
						} catch (SQLException e)
						{
							Logger.error("Error truncating table" + tableName,
									e);
						} finally
						{
							Ebean.endTransaction();
							if (conn != null)
							{
								try
								{
									conn.close();
								} catch (SQLException e)
								{
									Logger.warn("Failed to close connection.",
											e);
								}
							}
						}
						Ebean.getServerCacheManager().clearAll();
					}
				}
			}
		}

	}

}
