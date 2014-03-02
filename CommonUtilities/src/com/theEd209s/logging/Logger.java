package com.theEd209s.logging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * This class will allow for logging to be done
 * <br />
 * Any class that wishes to use this {@link Logger} should create
 * a static instance of a {@link Logger} class.
 * <br />
 * <br />
 * Once the application is running, calling the
 * {@link Logger#setLogHandler(String)} method can be used to
 * assign a logging {@link File} location.
 * 
 * @author Matthew Weiler
 * */
public class Logger
{	
	
	/* PUBLIC CONSTANT VARIABLES */
	/**
	 * This stores the maximum file size in bytes that any log file can get.
	 * */
	public static final int LOGGING_MAX_FILE_SIZE = 5242880;
	/**
	 * This store the maximum number of log files to store for each type of log.
	 * */
	public static final int LOGGING_MAX_FILE_COUNT = 10;
	
	/* CONSTRUCTORS */
	/**
	 * This will build a new instance of a {@link Logger} object and set
	 * the name of the class doing the logging.
	 * 
	 * @param className
	 *            The name of the class doing the logging.
	 * */
	public Logger(final String className)
	{
		this.className = className;
		this.logger = java.util.logging.Logger.getLogger(this.className);
		Logger.allLoggers.add(this);
		if (Logger.logHandler != null)
		{
			this.setLocalLogHandler(Logger.logHandler);
		}
	}
	
	/* PUBLIC METHODS */
	/**
	 * This will set the {@link Handler} to use for all {@link Logger}s.
	 * 
	 * @param filePath
	 * The full path for the {@link File} that all logs will be delivered to.
	 * */
	public static void setLogHandler(final String filePath)
	{
		if ((filePath != null) && (filePath.trim().length() > 0))
		{
			final File tmpFile = new File(filePath);
			if ((tmpFile != null) && (tmpFile.getParentFile() != null))
			{
				if (tmpFile.getParentFile().exists() || tmpFile.getParentFile().mkdirs())
				{
					if ((!tmpFile.exists()) || tmpFile.isFile())
					{
						try
						{
							Logger.setLogHandler(new FileHandler(filePath, Logger.LOGGING_MAX_FILE_SIZE, Logger.LOGGING_MAX_FILE_COUNT, true));
						}
						catch (SecurityException e)
						{
							System.out.println("(Logger.setLogHandler) Failed to create FileHandler for: " + filePath);
							System.out.println("(Logger.setLogHandler) This failure was due to a security exception (we likely don't have access to this file path).");
							e.printStackTrace();
						}
						catch (IOException e)
						{
							System.out.println("(Logger.setLogHandler) Failed to create FileHandler for: " + filePath);
							e.printStackTrace();
						}
					}
					else
					{
						System.out.println("(Logger.setLogHandler) Failed to create FileHandler since the location specified was that of a directory not a file: " + filePath);
					}
				}
				else
				{
					System.out.println("(Logger.setLogHandler) Failed to create FileHandler since we were unable to create the parent directory: " + tmpFile.getParentFile().getAbsolutePath());
				}
			}
			else
			{
				System.out.println("(Logger.setLogHandler) Failed to create FileHandler since no valid file path was specified.");
			}
		}
		else
		{
			System.out.println("(Logger.setLogHandler) Failed to create FileHandler since no valid file path was specified.");
		}
	}
	
	/**
	 * This will set the {@link Handler} to use for all {@link Logger}s.
	 * 
	 * @param logHandler
	 * The {@link Handler} to which all logs will be delivered.
	 * */
	public static void setLogHandler(final Handler logHandler)
	{
		if (logHandler != null)
		{
			Logger.logHandler = logHandler;
			if (Logger.allLoggers.size() > 0)
			{
				for (Logger logger : Logger.allLoggers)
				{
					logger.setLocalLogHandler(Logger.logHandler);
				}
			}
		}
		else
		{
			System.out.println("(Logger.setLogHandler) Failed to create log Handler since no valid Handler was specified.");
		}
	}
	
	/**
	 * This will set the {@link Formatter} that all {@link Logger}s
	 * will be passed through.
	 * 
	 * @param logFormatter
	 * The {@link Formatter} which all logs will be passed through.
	 * */
	public static void setLogFormatter(final Formatter logFormatter)
	{
		if (logFormatter != null)
		{
			Logger.logFormatter = logFormatter;
		}
		else
		{
			System.out.println("(Logger.setLogFormatter) Failed to assign log Formatter since no valid Formatter was specified.");
		}
	}
	
	/**
	 * This method will assign the appropriate handler to this
	 * {@link Logger}.
	 * 
	 * @param logHandler
	 *            The {@link Handler} already held by the parent.
	 * */
	public void setLocalLogHandler(Handler fileHandler)
	{
		// remove existing handlers
		java.util.logging.Logger tmp = this.getLogger();
		while (tmp != null)
		{
			Handler[] handlers = tmp.getHandlers();
			for (Handler handler : handlers)
			{
				tmp.removeHandler(handler);
			}
			tmp = tmp.getParent();
		}
		// add new console handlers
		if (fileHandler == null)
		{
			fileHandler = new ConsoleHandler();
		}
		this.getLogger().addHandler(fileHandler);
		fileHandler.setFormatter(Logger.logFormatter);
	}
	
	/**
	 * This will log a <code>debug</code> level message.
	 * 
	 * @param message
	 *            The actual message to log.
	 * */
	public void debug(final String message)
	{
		this.log(Level.CONFIG, message);
	}
	
	/**
	 * This will log an <code>debug</code> level message.
	 * 
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void debug(final String message, final Throwable thrown)
	{
		this.log(Level.CONFIG, message, thrown);
	}
	
	/**
	 * This will log an <code>debug</code> level message.
	 * 
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * */
	public void debug(final String methodName, final String message)
	{
		this.log(Level.CONFIG, methodName, message);
	}
	
	/**
	 * This will log an <code>debug</code> level message.
	 * 
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void debug(final String methodName, final String message, final Throwable thrown)
	{
		this.log(Level.CONFIG, methodName, message, thrown);
	}
	
	/**
	 * This will log an <code>info</code> level message.
	 * 
	 * @param message
	 *            The actual message to log.
	 * */
	public void info(final String message)
	{
		this.log(Level.INFO, message);
	}
	
	/**
	 * This will log an <code>info</code> level message.
	 * 
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void info(final String message, final Throwable thrown)
	{
		this.log(Level.INFO, message, thrown);
	}
	
	/**
	 * This will log an <code>info</code> level message.
	 * 
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * */
	public void info(final String methodName, final String message)
	{
		this.log(Level.INFO, methodName, message);
	}
	
	/**
	 * This will log an <code>info</code> level message.
	 * 
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void info(final String methodName, final String message, final Throwable thrown)
	{
		this.log(Level.INFO, methodName, message, thrown);
	}
	
	/**
	 * This will log a <code>warning</code> level message.
	 * 
	 * @param message
	 *            The actual message to log.
	 * */
	public void warning(final String message)
	{
		this.log(Level.WARNING, message);
	}
	
	/**
	 * This will log an <code>warning</code> level message.
	 * 
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void warning(final String message, final Throwable thrown)
	{
		this.log(Level.WARNING, message, thrown);
	}
	
	/**
	 * This will log an <code>warning</code> level message.
	 * 
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * */
	public void warning(final String methodName, final String message)
	{
		this.log(Level.WARNING, methodName, message);
	}
	
	/**
	 * This will log an <code>warning</code> level message.
	 * 
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void warning(final String methodName, final String message, final Throwable thrown)
	{
		this.log(Level.WARNING, methodName, message, thrown);
	}
	
	/**
	 * This will log an <code>error</code> level message.
	 * 
	 * @param message
	 *            The actual message to log.
	 * */
	public void error(final String message)
	{
		this.log(Level.SEVERE, message);
	}
	
	/**
	 * This will log an <code>error</code> level message.
	 * 
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void error(final String message, final Throwable thrown)
	{
		this.log(Level.SEVERE, message, thrown);
	}
	
	/**
	 * This will log an <code>error</code> level message.
	 * 
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * */
	public void error(final String methodName, final String message)
	{
		this.log(Level.SEVERE, methodName, message);
	}
	
	/**
	 * This will log an <code>error</code> level message.
	 * 
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void error(final String methodName, final String message, final Throwable thrown)
	{
		this.log(Level.SEVERE, methodName, message, thrown);
	}
	
	/**
	 * This will log an info level message.
	 * 
	 * @param level
	 *            The {@link Level} of the message being logged.
	 * @param message
	 *            The actual message to log.
	 * */
	public void log(final Level level, final String message)
	{
		this.logger.log(level, message);
	}
	
	/**
	 * This will log an info level message.
	 * 
	 * @param level
	 *            The {@link Level} of the message being logged.
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void log(final Level level, final String message, final Throwable thrown)
	{
		this.logger.log(level, message, thrown);
	}
	
	/**
	 * This will log an info level message.
	 * 
	 * @param level
	 *            The {@link Level} of the message being logged.
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * */
	public void log(final Level level, final String methodName, final String message)
	{
		this.logger.logp(level, this.className, methodName, message);
	}
	
	/**
	 * This will log an info level message.
	 * 
	 * @param level
	 *            The {@link Level} of the message being logged.
	 * @param methodName
	 *            The name of the method doing the logging.
	 * @param message
	 *            The actual message to log.
	 * @param thrown
	 *            The {@link Throwable} associated with this log message.
	 * */
	public void log(final Level level, final String methodName, final String message, final Throwable thrown)
	{
		this.logger.logp(level, this.className, methodName, message, thrown);
	}
	
	/* GETTERS & SETTERS */
	/**
	 * This will get the {@link Logger#className className} of this
	 * {@link Logger} instance.
	 * 
	 * @return The {@link Logger#className className} of this
	 *         {@link Logger} instance.
	 * */
	public String getClassName()
	{
		return this.className;
	}
	
	/**
	 * This will get the {@link Logger} object that is being used behind the
	 * scenes.
	 * 
	 * @return The {@link Logger} object that is being used behind the scenes.
	 * */
	public java.util.logging.Logger getLogger()
	{
		return this.logger;
	}
	
	/* PRIVATE VARIABLES */
	/**
	 * This will store the list of {@link Logger}s that have been created.
	 * */
	private static final List<Logger> allLoggers = new ArrayList<Logger>();
	/**
	 * This will store a reference to the {@link Handler} that all {@link Logger}s
	 * will be configured to use.
	 * */
	private static Handler logHandler = null;
	/**
	 * This will store a reference to the {@link Formatter} that should be used
	 * by all {@link Logger}s.
	 * */
	private static Formatter logFormatter = new LogFormatter();
	/**
	 * This will store the name of the class doing the logging.
	 * */
	private String className = null;
	/**
	 * This will store a reference to the {@link Logger} object being used.
	 * */
	private java.util.logging.Logger logger = null;
	
}
