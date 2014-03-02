package com.theEd209s.utils;

import com.theEd209s.logging.Logger;

/**
 * This class contains several methods for working with {@link String}s.
 * 
 * @author Matthew Weiler
 * */
public class StringUtils
{	
	
	/**
	 * This method will determine if the passed-in {@link String} is
	 * <code>null</code> or an empty {@link String} (after trimming).
	 * 
	 * @param str
	 *            The {@link String} to check.
	 * 
	 * @return If the passed-in {@link String} is null or an emtpy
	 *         {@link String} (after trimming), this will return
	 *         <code>true</code>; otherwise <code>false</code>.
	 * */
	public static boolean isNullOrEmpty(String str)
	{
		return StringUtils.isNullOrEmpty(str, true);
	}
	
	/**
	 * Check a string for is null or empty
	 * @param str the string to check
	 * @param trim whether to trim the string (this is legacy here to account for legacy reason and is configurable
	 * @return true if the string is null or empty
	 */
	public static boolean isNullOrEmpty(String str, boolean trim)
	{
		if (trim)
		{
			return str == null || str.trim().length() == 0;
		}
		else
		{
			return str == null || str.trim().length() == 0;
		}
	}
	
	/**
	 * This method will reverse the input {@link String}.
	 * 
	 * @param inputString
	 *            The {@link String} to reverse.
	 * 
	 * @return The reversed {@link String}.
	 * */
	public static String reverse(final String inputString)
	{
		if (!StringUtils.isNullOrEmpty(inputString))
		{
			StringBuilder outputString = new StringBuilder();
			// We can't use StringBuilder.reverse() because GWT doesn't implement it :(
			for (int n = (inputString.length() - 1); n >= 0; n--)
			{
				outputString.append(inputString.charAt(n));
			}
			return outputString.toString();
		}
		return inputString;
	}
	
	/**
	 * This method will perform a <code>Null-Value-Replace</code>. <br />
	 * If the desiredStr value is not null, and is not empty, the desiredStr
	 * will be returned, otherwise the backupStr will be returned.
	 * 
	 * @param desiredStr
	 *            The {@link String} to check if it's null or empty.
	 * @param backupStr
	 *            The {@link String} to be returned if the desiredStr is null or
	 *            empty.
	 * 
	 * @return If the desiredStr value is not null, and is not empty, the
	 *         desiredStr String will be returned, otherwise the backupStr will
	 *         be returned.
	 * */
	public static String nvl(final String desiredStr, final String backupStr)
	{
		if (StringUtils.isNullOrEmpty(desiredStr))
		{
			return backupStr;
		}
		return desiredStr;
	}
	
	/**
	 * This method will left-pad the given input {@link String} with the padding
	 * {@link String} up to a maximum length. <br />
	 * <br />
	 * <b><u>Examples</u>:</b> <br />
	 * <ol>
	 * <li><code>lpad(&quot;abc&quot;, 5, &quot;xyz&quot;, true)</code> =
	 * &quot;xyabc&quot;</li>
	 * <li><code>lpad(&quot;abc&quot;, 3, &quot;xyz&quot;, true)</code> =
	 * &quot;abc&quot;</li>
	 * <li><code>lpad(&quot;abc&quot;, 2, &quot;xyz&quot;, false)</code> =
	 * &quot;ab&quot;</li>
	 * <li><code>lpad(&quot;abc&quot;, 2, &quot;xyz&quot;, true)</code> =
	 * &quot;abc&quot;</li>
	 * <li><code>lpad(&quot;abc&quot;, 7, &quot;xyz&quot;, true)</code> =
	 * &quot;xyzxabc&quot;</li>
	 * </ol>
	 * 
	 * @param inputString
	 *            The {@link String} to pad.
	 * @param desiredLength
	 *            The expected length of the output {@link String}.
	 * @param paddingStr
	 *            The {@link String} to use as padding.
	 * @param allowExistingExcess
	 *            If the input {@link String} is already larger than the desired
	 *            length, should it be kept or trimmed? <br />
	 *            <i><code>true</code> means to keep the existing excess;
	 *            <code>false</code> means to trim the existing excess</i>
	 * 
	 * @return The padded {@link String}.
	 * */
	public static String lpad(final String inputString, final int desiredLength, String paddingStr, final boolean allowExistingExcess)
	{
		if (!StringUtils.isNullOrEmpty(inputString))
		{
			if (inputString.length() < desiredLength)
			{
				paddingStr = StringUtils.reverse(paddingStr);
				StringBuilder outputString = new StringBuilder(StringUtils.reverse(inputString));
				String tmpPad = null;
				int tmpRequiredPadLength = -1;
				while (outputString.toString().length() < desiredLength)
				{
					tmpRequiredPadLength = desiredLength - outputString.toString().length();
					if (tmpRequiredPadLength >= paddingStr.length())
					{
						tmpPad = paddingStr;
					}
					else
					{
						tmpPad = paddingStr.substring(paddingStr.length() - tmpRequiredPadLength);
					}
					outputString.append(tmpPad);
				}
				return StringUtils.reverse(outputString.toString());
			}
			else if ((inputString.length() > desiredLength) && (!allowExistingExcess))
			{
				return inputString.substring(0, desiredLength);
			}
		}
		return inputString;
	}
	
	/**
	 * This method will right-pad the given input {@link String} with the
	 * padding {@link String} up to a maximum length. <br />
	 * <br />
	 * <b><u>Examples</u>:</b> <br />
	 * <ol>
	 * <li><code>rpad(&quot;abc&quot;, 5, &quot;xyz&quot;, true)</code> =
	 * &quot;abcxy&quot;</li>
	 * <li><code>rpad(&quot;abc&quot;, 3, &quot;xyz&quot;, true)</code> =
	 * &quot;abc&quot;</li>
	 * <li><code>rpad(&quot;abc&quot;, 2, &quot;xyz&quot;, false)</code> =
	 * &quot;ab&quot;</li>
	 * <li><code>rpad(&quot;abc&quot;, 2, &quot;xyz&quot;, true)</code> =
	 * &quot;abc&quot;</li>
	 * <li><code>rpad(&quot;abc&quot;, 7, &quot;xyz&quot;, true)</code> =
	 * &quot;abcxyzx&quot;</li>
	 * </ol>
	 * 
	 * @param inputString
	 *            The {@link String} to pad.
	 * @param desiredLength
	 *            The expected length of the output {@link String}.
	 * @param paddingStr
	 *            The {@link String} to use as padding.
	 * @param allowExistingExcess
	 *            If the input {@link String} is already larger than the desired
	 *            length, should it be kept or trimmed? <br />
	 *            <i><code>true</code> means to keep the existing excess;
	 *            <code>false</code> means to trim the existing excess</i>
	 * 
	 * @return The padded {@link String}.
	 * */
	public static String rpad(final String inputString, final int desiredLength, final String paddingStr, final boolean allowExistingExcess)
	{
		if (!StringUtils.isNullOrEmpty(inputString))
		{
			if (inputString.length() < desiredLength)
			{
				final StringBuilder outputString = new StringBuilder(inputString);
				String tmpPad = null;
				int tmpRequiredPadLength = -1;
				while (outputString.toString().length() < desiredLength)
				{
					tmpRequiredPadLength = desiredLength - outputString.toString().length();
					if (tmpRequiredPadLength >= paddingStr.length())
					{
						tmpPad = paddingStr;
					}
					else
					{
						tmpPad = paddingStr.substring(0, tmpRequiredPadLength);
					}
					outputString.append(tmpPad);
				}
				return outputString.toString();
			}
			else if ((inputString.length() > desiredLength) && (!allowExistingExcess))
			{
				return inputString.substring(0, desiredLength);
			}
		}
		return inputString;
	}
	
	/**
	 * This method will converts the total number of milliseconds into a human
	 * readable {@link String} and return that {@link String}. <br />
	 * <i>it should be noted that for the purposes of these calculations, a year
	 * will consist of 364 days</i>
	 * 
	 * @param msecs
	 *            The total number of milliseconds for the duration.
	 * 
	 * @return The human readable {@link String}.
	 * */
	public static String humanReadableElapsedTime(long msecs) throws Exception
	{
		return StringUtils.humanReadableElapsedTime(msecs, null, null, false);
	}
	
	/**
	 * This method will converts the total number of milliseconds into a human
	 * readable {@link String} and return that {@link String}. <br />
	 * <i>it should be noted that for the purposes of these calculations, a year
	 * will consist of 364 days</i>
	 * 
	 * @param msecs
	 *            The total number of milliseconds for the duration.
	 * @param accuracyLimitLower
	 *            The {@link ElapsedTimeStage} object representing the minimum
	 *            level of accuracy that should be achieved. <br />
	 *            <i>if a lower level accuracy of
	 *            {@link ElapsedTimeStage#MINUTES} is desired, then any time
	 *            less than 0 minute will result in an output of 0 mins (no
	 *            seconds or milliseconds will be displayed)</i> <br />
	 *            <i>if this is <code>null</code>, the maximum level of accuracy
	 *            will be dynamically determined</i>
	 * @param accuracyLimitUpper
	 *            The {@link ElapsedTimeStage} object representing the maximum
	 *            level of accuracy that should be achieved. <br />
	 *            <i>if an upper level accuracy of
	 *            {@link ElapsedTimeStage#MINUTES} is desired, then any time
	 *            greater than 60 minute will result in an output of x mins (no
	 *            hours or days will be displayed)</i> <br />
	 *            <i>if this is <code>null</code>, the upper limit level of
	 *            accuracy will be dynamically determined</i>
	 * @param strictUpperLimit
	 *            If the upper limit should be displayed regardless of 0 values,
	 *            set this to <code>true</code>; otherwise <code>false</code>.
	 * 
	 * @return The human readable {@link String}.
	 * */
	public static String humanReadableElapsedTime(long msecs, ElapsedTimeStage accuracyLimitLower, final ElapsedTimeStage accuracyLimitUpper, final boolean strictUpperLimit) throws Exception
	{
		if ((accuracyLimitLower != null) && (accuracyLimitUpper != null) && (accuracyLimitUpper.ordinal() < accuracyLimitLower.ordinal()))
		{
			throw new Exception("Upper accuracy limit must be greater-than-or-equal-to the lower accuracy level.");
		}
		StringBuilder humanOutput = new StringBuilder();
		// Initialize all of the time increments.
		long secs = 0L;
		long mins = 0L;
		long hours = 0L;
		long days = 0L;
		long years = 0L;
		long centuries = 0L;
		long millenia = 0L;
		// If the total number of milliseconds exceed 1 second worth, then calculate how many seconds and keep the remainder as the milliseconds.
		if ((msecs >= 1000) && ((accuracyLimitUpper == null) || (ElapsedTimeStage.MILLISECONDS.ordinal() < accuracyLimitUpper.ordinal())))
		{
			secs = msecs / 1000;
			msecs = msecs % 1000;
			// If the total number of seconds exceed 1 minute worth, then calculate how many minutes and keep the remainder as the seconds.
			if ((secs >= 60) && ((accuracyLimitUpper == null) || (ElapsedTimeStage.SECONDS.ordinal() < accuracyLimitUpper.ordinal())))
			{
				mins = secs / 60;
				secs = secs % 60;
				// If the total number of minutes exceed 1 hour worth, then calculate how many hours and keep the remainder as the minutes.
				if ((mins >= 60) && ((accuracyLimitUpper == null) || (ElapsedTimeStage.MINUTES.ordinal() < accuracyLimitUpper.ordinal())))
				{
					hours = mins / 60;
					mins = mins % 60;
					// If the total number of hours exceed 1 day worth, then calculate how many days and keep the remainder as the hours.
					if ((hours >= 24) && ((accuracyLimitUpper == null) || (ElapsedTimeStage.HOURS.ordinal() < accuracyLimitUpper.ordinal())))
					{
						days = hours / 24;
						hours = hours % 24;
						// If the total number of days exceed 1 year worth, then calculate how many years and keep the remainder as the days.
						if ((days >= 364) && ((accuracyLimitUpper == null) || (ElapsedTimeStage.DAYS.ordinal() < accuracyLimitUpper.ordinal())))
						{
							years = days / 364;
							days = days % 364;
							// If the total number of days exceed 1 year worth, then calculate how many years and keep the remainder as the days.
							if ((years >= 100) && ((accuracyLimitUpper == null) || (ElapsedTimeStage.YEARS.ordinal() < accuracyLimitUpper.ordinal())))
							{
								centuries = years / 100;
								years = years % 100;
								// If the total number of days exceed 1 year worth, then calculate how many years and keep the remainder as the days.
								if ((centuries >= 10) && ((accuracyLimitUpper == null) || (ElapsedTimeStage.CENTURIES.ordinal() < accuracyLimitUpper.ordinal())))
								{
									millenia = centuries / 10;
									centuries = centuries % 10;
								}
							}
						}
					}
				}
			}
		}
		// If the user didn't specify a lower accuracy limit, then determine what is the lowest level that has a value.
		if (accuracyLimitLower == null)
		{
			if (msecs > 0)
			{
				accuracyLimitLower = ElapsedTimeStage.MILLISECONDS;
			}
			else if (secs > 0)
			{
				accuracyLimitLower = ElapsedTimeStage.SECONDS;
			}
			else if (mins > 0)
			{
				accuracyLimitLower = ElapsedTimeStage.MINUTES;
			}
			else if (hours > 0)
			{
				accuracyLimitLower = ElapsedTimeStage.HOURS;
			}
			else if (days > 0)
			{
				accuracyLimitLower = ElapsedTimeStage.DAYS;
			}
			else if (years > 0)
			{
				accuracyLimitLower = ElapsedTimeStage.YEARS;
			}
			else if (centuries > 0)
			{
				accuracyLimitLower = ElapsedTimeStage.CENTURIES;
			}
			else if (millenia > 0)
			{
				accuracyLimitLower = ElapsedTimeStage.MILLENIA;
			}
			else
			{
				accuracyLimitLower = ElapsedTimeStage.MILLISECONDS;
			}
			// Ensure that the lower limit, if it is being calculated dynamically, cannot be greater than the upper limit.
			if ((accuracyLimitUpper != null) && (accuracyLimitLower.ordinal() > accuracyLimitUpper.ordinal()))
			{
				accuracyLimitLower = accuracyLimitUpper;
			}
		}
		boolean somethingWritten = false;
		// If the total number of years calculated above exceeds 0, then write that.
		if ((ElapsedTimeStage.MILLENIA.ordinal() == accuracyLimitLower.ordinal()) || ((ElapsedTimeStage.MILLENIA.ordinal() >= accuracyLimitLower.ordinal()) && ((millenia > 0L) || (strictUpperLimit && (ElapsedTimeStage.MILLENIA.ordinal() <= accuracyLimitUpper.ordinal())))))
		{
			if (millenia == 1L)
			{
				humanOutput.append(millenia).append(" ").append(ElapsedTimeStage.MILLENIA.getHumanShortName()).append("  ");
			}
			else
			{
				humanOutput.append(millenia).append(" ").append(ElapsedTimeStage.MILLENIA.getHumanShortNamePlural()).append("  ");
			}
			somethingWritten = true;
		}
		// If the total number of years calculated above exceeds 0, then write that.
		if ((ElapsedTimeStage.CENTURIES.ordinal() == accuracyLimitLower.ordinal()) || ((ElapsedTimeStage.CENTURIES.ordinal() >= accuracyLimitLower.ordinal()) && ((centuries > 0L) || (strictUpperLimit && (ElapsedTimeStage.CENTURIES.ordinal() <= accuracyLimitUpper.ordinal())))))
		{
			if (centuries == 1L)
			{
				humanOutput.append(centuries).append(" ").append(ElapsedTimeStage.CENTURIES.getHumanShortName()).append("  ");
			}
			else
			{
				humanOutput.append(centuries).append(" ").append(ElapsedTimeStage.CENTURIES.getHumanShortNamePlural()).append("  ");
			}
			somethingWritten = true;
		}
		// If the total number of years calculated above exceeds 0, then write that.
		if ((ElapsedTimeStage.YEARS.ordinal() == accuracyLimitLower.ordinal()) || ((ElapsedTimeStage.YEARS.ordinal() >= accuracyLimitLower.ordinal()) && ((years > 0L) || (strictUpperLimit && (ElapsedTimeStage.YEARS.ordinal() <= accuracyLimitUpper.ordinal())))))
		{
			if (years == 1L)
			{
				humanOutput.append(years).append(" ").append(ElapsedTimeStage.YEARS.getHumanShortName()).append("  ");
			}
			else
			{
				humanOutput.append(years).append(" ").append(ElapsedTimeStage.YEARS.getHumanShortNamePlural()).append("  ");
			}
			somethingWritten = true;
		}
		// If the total number of days calculated above exceeds 0, then write that.
		if ((ElapsedTimeStage.DAYS.ordinal() == accuracyLimitLower.ordinal()) || ((ElapsedTimeStage.DAYS.ordinal() >= accuracyLimitLower.ordinal()) && ((days > 0L) || (strictUpperLimit && (ElapsedTimeStage.DAYS.ordinal() <= accuracyLimitUpper.ordinal())))))
		{
			if (days == 1L)
			{
				humanOutput.append(days).append(" ").append(ElapsedTimeStage.DAYS.getHumanShortName()).append("  ");
			}
			else
			{
				humanOutput.append(days).append(" ").append(ElapsedTimeStage.DAYS.getHumanShortNamePlural()).append("  ");
			}
			somethingWritten = true;
		}
		// If the total number of hours calculated above exceeds 0, then write that.
		if ((ElapsedTimeStage.HOURS.ordinal() == accuracyLimitLower.ordinal()) || ((ElapsedTimeStage.HOURS.ordinal() >= accuracyLimitLower.ordinal()) && (somethingWritten || (hours > 0L) || (strictUpperLimit && (ElapsedTimeStage.HOURS.ordinal() <= accuracyLimitUpper.ordinal())))))
		{
			if (hours == 1L)
			{
				humanOutput.append(hours).append(" ").append(ElapsedTimeStage.HOURS.getHumanShortName()).append("  ");
			}
			else
			{
				humanOutput.append(hours).append(" ").append(ElapsedTimeStage.HOURS.getHumanShortNamePlural()).append("  ");
			}
			somethingWritten = true;
		}
		// If the total number of minutes calculated above exceeds 0, then write that.
		if ((ElapsedTimeStage.MINUTES.ordinal() == accuracyLimitLower.ordinal()) || ((ElapsedTimeStage.MINUTES.ordinal() >= accuracyLimitLower.ordinal()) && (somethingWritten || (mins > 0L) || (strictUpperLimit && (ElapsedTimeStage.MINUTES.ordinal() <= accuracyLimitUpper.ordinal())))))
		{
			if (mins == 1)
			{
				humanOutput.append(mins).append(" ").append(ElapsedTimeStage.MINUTES.getHumanShortName()).append("  ");
			}
			else
			{
				humanOutput.append(mins).append(" ").append(ElapsedTimeStage.MINUTES.getHumanShortNamePlural()).append("  ");
			}
			somethingWritten = true;
		}
		// If the total number of seconds calculated above exceeds 0, then write that.
		if ((ElapsedTimeStage.SECONDS.ordinal() == accuracyLimitLower.ordinal()) || ((ElapsedTimeStage.SECONDS.ordinal() >= accuracyLimitLower.ordinal()) && (somethingWritten || (secs > 0L) || (strictUpperLimit && (ElapsedTimeStage.SECONDS.ordinal() <= accuracyLimitUpper.ordinal())))))
		{
			if (secs == 1)
			{
				humanOutput.append(secs).append(" ").append(ElapsedTimeStage.SECONDS.getHumanShortName()).append("  ");
			}
			else
			{
				humanOutput.append(secs).append(" ").append(ElapsedTimeStage.SECONDS.getHumanShortNamePlural()).append("  ");
			}
			somethingWritten = true;
		}
		// If the total number of milliseconds calculated above exceeds 0, then write that.
		if ((ElapsedTimeStage.MILLISECONDS.ordinal() == accuracyLimitLower.ordinal()) || ((ElapsedTimeStage.MILLISECONDS.ordinal() >= accuracyLimitLower.ordinal()) && (somethingWritten || (msecs > 0L) || (strictUpperLimit && (ElapsedTimeStage.MILLISECONDS.ordinal() <= accuracyLimitUpper.ordinal())))))
		{
			if (msecs == 1)
			{
				humanOutput.append(msecs).append(" ").append(ElapsedTimeStage.MILLISECONDS.getHumanShortName()).append("  ");
			}
			else
			{
				humanOutput.append(msecs).append(" ").append(ElapsedTimeStage.MILLISECONDS.getHumanShortNamePlural()).append("  ");
			}
			somethingWritten = true;
		}
		return humanOutput.toString();
	}
	
	/**
	 * This will determine how many <b>MILLISECONDS</b> the specified duration {@link String} represents.
	 * <br />
	 * <br />
	 * <b><u>Available Time Units</u>:</b>
	 * <br />
	 * If the unit component of the specified duration {@link String} starts with any of the below identifiers, then it matches the associated rule.
	 * <br />
	 * If the unit component of the specified duration {@link String} doesn't start with any of the below identifiers, then it is considered to be in <b>MILLISECONDS</b>.
	 * <table border="1px" cellpadding="2px">
	 * <tr>
	 * <th>Identifier</th>
	 * <th>Meaning</th>
	 * </tr>
	 * <tr>
	 * <td align="center" valign="top"><b>day</b></td>
	 * <td rowspan="2" valign="top">Specifies that the associated number is in <b>DAYS</b>.</td>
	 * </tr>
	 * <tr>
	 * <td align="center" valign="top"><b>dy</b></td>
	 * </tr>
	 * <tr>
	 * <td align="center" valign="top"><b>hour</b></td>
	 * <td rowspan="2" valign="top">Specifies that the associated number is in <b>HOURS</b>.</td>
	 * </tr>
	 * <tr>
	 * <td align="center" valign="top"><b>hr</b></td>
	 * </tr>
	 * <tr>
	 * <td align="center" valign="top"><b>min</b></td>
	 * <td rowspan="2" valign="top">Specifies that the associated number is in <b>MINUTES</b>.</td>
	 * </tr>
	 * <tr>
	 * <td align="center" valign="top"><b>mn</b></td>
	 * </tr>
	 * <tr>
	 * <td align="center" valign="top"><b>sec</b></td>
	 * <td valign="top">Specifies that the associated number is in <b>SECONDS</b>.</td>
	 * </tr>
	 * <tr>
	 * <td align="center" valign="top"><code>anything else</code></td>
	 * <td valign="top">Specifies that the associated number is in <b>MILLISECONDS</b>.</td>
	 * </tr>
	 * </table>
	 * <br />
	 * <br />
	 * <b><u>Examples</u>:</b>
	 * <ul>
	 * <li>30 MIN  (<i>1,800,000 ms</i>)</li>
	 * <li>7mins  (<i>420,000 ms</i>)</li>
	 * <li>4mn  (<i>240,000 ms</i>)</li>
	 * <li>84 Sec  (<i>84,000 ms</i>)</li>
	 * <li>2 hours  (<i>7,200,000 ms</i>)</li>
	 * <li>300 ms  (<i>300 ms</i>)</li>
	 * <li>900  (<i>900 ms</i>)</li>
	 * <li>0  (<i>0 ms</i>)</li>
	 * </ul>
	 * 
	 * @param durationStr
	 * The duration {@link String} that is to be parsed.
	 * 
	 * @return
	 * The total number of milliseconds that the specified duration {@link String} represents.
	 * */
	public static long convertDurationStrToNum(final String durationStr)
	{
		if (!StringUtils.isNullOrEmpty(durationStr))
		{
			StringBuilder tmpNumSb = new StringBuilder();
			StringBuilder tmpUnitSb = new StringBuilder();
			int tmpCharIndex = 0;
			boolean doneWithNum = false;
			char tmpChar;
			while (tmpCharIndex < durationStr.length())
			{
				tmpChar = durationStr.charAt(tmpCharIndex);
				if ((!doneWithNum) && (tmpChar >= 48) && (tmpChar <= 57))
				{
					tmpNumSb.append(tmpChar);
				}
				else
				{
					doneWithNum = true;
					tmpUnitSb.append(tmpChar);
				}
				tmpCharIndex++;
			}
			try
			{
				final long tmpNum = Long.parseLong(tmpNumSb.toString().trim());
				String unit = tmpUnitSb.toString().trim().toLowerCase();
				if (unit.startsWith("day") || unit.startsWith("dy"))
				{
					return tmpNum * 24 * 60 * 60 * 1000;
				}
				else if (unit.startsWith("hour") || unit.startsWith("hr"))
				{
					return tmpNum * 60 * 60 * 1000;
				}
				else if (unit.startsWith("min") || unit.startsWith("mn"))
				{
					return tmpNum * 60 * 1000;
				}
				else if (unit.startsWith("sec"))
				{
					return tmpNum * 1000;
				}
				else
				{
					return tmpNum;
				}
			}
			catch (Exception e)
			{
				StringUtils.logger.error("convertTimeStrToNum", "Failed to parse duration string: " + durationStr, e);
			}
		}
		return 0L;
	}
	
	/* PRIVATE CONSTANTS */
	/**
	 * This will be used to log any errors to a log file.
	 * */
	private static final Logger logger = new Logger(StringUtils.class.getName());
	
	/* PUBLIC ENUMS */
	/**
	 * This enum contains all possible time stage options.
	 * */
	public static enum ElapsedTimeStage
	{
		/**
		 * Time displayed in milliseconds.
		 * */
		MILLISECONDS("msec", "msecs", "millisecond", "milliseconds"),
		/**
		 * Time displayed in seconds. <br />
		 * <i>1 second = 1000 milliseconds</i>
		 * */
		SECONDS("sec", "secs", "second", "seconds"),
		/**
		 * Time displayed in minutes. <br />
		 * <i>1 minute = 60 seconds</i>
		 * */
		MINUTES("min", "mins", "minute", "minutes"),
		/**
		 * Time displayed in hours. <br />
		 * <i>1 hour = 60 minutes</i>
		 * */
		HOURS("hour", "hours", "hour", "hours"),
		/**
		 * Time displayed in days. <br />
		 * <i>1 day = 24 hours</i>
		 * */
		DAYS("day", "days", "day", "days"),
		/**
		 * Time displayed in years. <br />
		 * <i>1 year = 364 days</i> <br />
		 * <i>we are not accommodating for leap-years here as that will become
		 * much more complicated and technically would require a start
		 * time/date</i> <br />
		 * <br />
		 * <b><u>Neat Fact</u>:</b> <a
		 * href="http://en.wikipedia.org/wiki/Age_of_the_Earth">link</a> <br />
		 * The earth is believed to be 4.54 +- 0.05 billion years old
		 * */
		YEARS("year", "years", "year", "years"),
		/**
		 * Time displayed in centuries. <br />
		 * <i>1 century = 100 years</i> <br />
		 * <br />
		 * <b><u>Neat Fact</u>:</b> <a
		 * href="http://en.wikipedia.org/wiki/Age_of_the_Earth">link</a> <br />
		 * The earth is believed to be 4.54 +- 0.05 billion years old
		 * */
		CENTURIES("century", "centuries", "century", "centuries"),
		/**
		 * Time displayed in millenia. <br />
		 * <i>1 millenia = 10 centuries</i> <br />
		 * <i>1 millenia = 1000 years</i> <br />
		 * <br />
		 * <b><u>Neat Fact</u>:</b> <a
		 * href="http://en.wikipedia.org/wiki/Age_of_the_Earth">link</a> <br />
		 * The earth is believed to be 4.54 +- 0.05 billion years old
		 * */
		MILLENIA("millennium", "millenia", "millennium", "millenia");
		
		/* CONSTRUCTORS */
		/**
		 * This will create a new instance of a {@link ElapsedTimeStage} enum
		 * value.
		 * 
		 * @param humanShortName
		 *            The short-form singular name of this
		 *            {@link ElapsedTimeStage} instance.
		 * @param humanShortNamePlural
		 *            The short-form plural name of this
		 *            {@link ElapsedTimeStage} instance.
		 * @param humanLongName
		 *            The long-form singular name of this
		 *            {@link ElapsedTimeStage} instance.
		 * @param humanLongNamePlural
		 *            The long-form plural name of this {@link ElapsedTimeStage}
		 *            instance.
		 * */
		private ElapsedTimeStage(final String humanShortName, final String humanShortNamePlural, final String humanLongName, final String humanLongNamePlural)
		{
			this.humanShortName = humanShortName;
			this.humanShortNamePlural = humanShortNamePlural;
			this.humanLongName = humanLongName;
			this.humanLongNamePlural = humanLongNamePlural;
		}
		
		/* GETTERS & SETTERS */
		/**
		 * This will get the short-form singular name of this
		 * {@link ElapsedTimeStage} instance.
		 * 
		 * @return The short-form singular name of this {@link ElapsedTimeStage}
		 *         instance.
		 * */
		public String getHumanShortName()
		{
			return this.humanShortName;
		}
		
		/**
		 * This will get the short-form plural name of this
		 * {@link ElapsedTimeStage} instance.
		 * 
		 * @return The short-form plural name of this {@link ElapsedTimeStage}
		 *         instance.
		 * */
		public String getHumanShortNamePlural()
		{
			return this.humanShortNamePlural;
		}
		
		/**
		 * This will get the long-form singular name of this
		 * {@link ElapsedTimeStage} instance.
		 * 
		 * @return The long-form singular name of this {@link ElapsedTimeStage}
		 *         instance.
		 * */
		public String getHumanLongName()
		{
			return this.humanLongName;
		}
		
		/**
		 * This will get the long-form plural name of this
		 * {@link ElapsedTimeStage} instance.
		 * 
		 * @return The long-form plural name of this {@link ElapsedTimeStage}
		 *         instance.
		 * */
		public String getHumanLongNamePlural()
		{
			return this.humanLongNamePlural;
		}
		
		/* PRIVATE VARIABELS */
		/**
		 * This will store the short-form singular name of this
		 * {@link ElapsedTimeStage} instance.
		 * */
		private String humanShortName = null;
		/**
		 * This will store the short-form plural name of this
		 * {@link ElapsedTimeStage} instance.
		 * */
		private String humanShortNamePlural = null;
		/**
		 * This will store the long-form singular name of this
		 * {@link ElapsedTimeStage} instance.
		 * */
		private String humanLongName = null;
		/**
		 * This will store the long-form plural name of this
		 * {@link ElapsedTimeStage} instance.
		 * */
		private String humanLongNamePlural = null;
		
	}
	
}
