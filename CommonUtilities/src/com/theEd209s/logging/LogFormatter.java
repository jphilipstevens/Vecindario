package com.theEd209s.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class can be used to format a log entry into the appropriate format. <br />
 * <br />
 * <b><u>Sample Records</u></b> <br />
 * <code>
 * <ul>
 * <li>yyyy-MM-dd hh:mm:ss [SEVERE] (Foo.bar) An error occurred. -- Socket was closed unexpectedly</li>
 * <li>yyyy-MM-dd hh:mm:ss [SEVERE] (Foo) An error occurred. -- Socket was closed unexpectedly</li>
 * <li>yyyy-MM-dd hh:mm:ss [INFO] (bar) Something good happened.</li>
 * </ul>
 * </code>
 * 
 * @author Matthew Weiler
 * */
public class LogFormatter extends Formatter
{	
	
	/* PUBLIC CONSTANT VARIABLES */
	/**
	 * This stores the total number of {@link Throwable} causes deep should be
	 * included in the logs.
	 * */
	public static final int MAX_EXCEPTION_CAUSE_DEPTH = 5;
	/**
	 * This stores the total number of {@link StackTraceElement}s that should be
	 * included in the logs for each {@link Throwable} cause.
	 * */
	public static final int MAX_EXCEPTION_CAUSE_STACK_DEPTH = 5;
	
	/* PUBLIC METHODS */
	/**
	 * This method will format the passed-in {@link LogRecord} into the
	 * {@link String} format that we want for our logs. <br />
	 * <br />
	 * <b><u>Sample Records</u></b> <br />
	 * <code>
	 * <ul>
	 * <li>yyyy-MM-dd hh:mm:ss [SEVERE] (Foo.bar) An error occurred. -- Socket was closed unexpectedly</li>
	 * <li>yyyy-MM-dd hh:mm:ss [SEVERE] (Foo) An error occurred. -- Socket was closed unexpectedly</li>
	 * <li>yyyy-MM-dd hh:mm:ss [INFO] (bar) Something good happened.</li>
	 * </ul>
	 * </code>
	 * 
	 * @param record
	 *            The {@link LogRecord} to be formatted.
	 * 
	 * @return The formatted {@link String}.
	 * */
	public String format(LogRecord record)
	{
		final StringBuilder outputFormat = new StringBuilder();
		try
		{
			if (record != null)
			{
				outputFormat.append(LogFormatter.getDateFormat().format(new Date(record.getMillis()))).append(" [").append(record.getLevel()).append("] ");
				// If the name of the logging class is set, include it.
				if ((record.getSourceClassName() != null) && (record.getSourceClassName().length() > 0))
				{
					// If the name of the logging method is set, include it.
					if ((record.getSourceMethodName() != null) && (record.getSourceMethodName().length() > 0))
					{
						outputFormat.append("(").append(record.getSourceClassName()).append(".").append(record.getSourceMethodName()).append(") ");
					}
					// If the name of the logging method is not set, only include the class name.
					else
					{
						outputFormat.append("(").append(record.getSourceClassName()).append(") ");
					}
				}
				// If the name of the logging method is set, include it.
				else if ((record.getSourceMethodName() != null) && (record.getSourceMethodName().length() > 0))
				{
					outputFormat.append("(").append(record.getSourceMethodName()).append(") ");
				}
				// Include the actual message being logged.
				outputFormat.append(record.getMessage());
				// If there is a throwable set, include its message.
				if (record.getThrown() != null)
				{
					Throwable thrown = record.getThrown();
					StackTraceElement[] stack = null;
					for (int n = 0; (thrown != null) && (n < LogFormatter.MAX_EXCEPTION_CAUSE_DEPTH); n++)
					{
						// Don't print a new-line for the base Throwable.
						if (n == 0)
						{
							outputFormat.append(" -- ").append(thrown.getMessage());
						}
						// Print a new-line for any Throwable which was a cause of the base Throwable.
						else
						{
							outputFormat.append(LogFormatter.getLineSep());
							outputFormat.append("     ").append(thrown.getMessage());
						}
						stack = thrown.getStackTrace();
						for (int k = 0; (k<stack.length) && ( k < LogFormatter.MAX_EXCEPTION_CAUSE_STACK_DEPTH ); k++)
						{
							outputFormat.append(LogFormatter.getLineSep());
							outputFormat.append("        ").append(stack[k].toString());
						}
						thrown = thrown.getCause();
					}
				}
				outputFormat.append(LogFormatter.getLineSep());
			}
		}
		catch (Exception e)
		{
			System.out.println(LogFormatter.getDateFormat().format(new Date()) + " [ERROR] (Formatter.format) Failed to format log.");
			e.printStackTrace();
		}
		return outputFormat.toString();
	}
	
	/* PRIVATE METHODS */
	/**
	 * This will get the {@link DateFormat} to be used for any dates in the
	 * logs.
	 * 
	 * @return The {@link DateFormat} to be used for any dates in the logs.
	 * */
	static private DateFormat getDateFormat()
	{
		return new SimpleDateFormat(LogFormatter.DATE_FORMAT);
	}
	
	/**
	 * This will get the line separator character to be used in the logs.
	 * 
	 * @return The line separator character to be used in the logs.
	 * */
	static private String getLineSep()
	{
		// If we upgrade to Java7+ we could use:
		// return System.lineSeparator();
		return System.getProperty("line.separator");
	}
	
	/* PRIVATE CONSTANT VARIABLES */
	/**
	 * This will store the date format {@link String} to be used for any dates
	 * in the logs.
	 * */
	static private final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
}
