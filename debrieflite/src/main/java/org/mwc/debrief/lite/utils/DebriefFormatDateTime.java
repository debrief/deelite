/*******************************************************************************
 * Copyright (c) 2014, PlanetMayo Ltd. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package org.mwc.debrief.lite.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.model.impl.TemporalImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebriefFormatDateTime
{
	static final Logger logger = LoggerFactory.getLogger(DebriefFormatDateTime.class);
	
	private static DateFormat _dfMillis = null;
	private static DateFormat _df = null;
	private static NumberFormat _micros = null;
	private static NumberFormat _millis = null;
	private static final DateFormat FOUR_DIGIT_YEAR_FORMAT = new SimpleDateFormat(
			"yyyyMMdd HHmmss");
	private static final DateFormat TWO_DIGIT_YEAR_FORMAT = new SimpleDateFormat(
			"yyMMdd HHmmss");

	/**
	 * the string which in the past could appear, when the intention of the
	 * software was to store a null string
	 */
	private static final String NULL_DATE_STRING = "691231 235959.999";

	/**
	 * there are also some instances where invalid dates have crept in, possibly
	 * related to Debrief storing 0 and trying to write this to disk. Problem
	 * probably occured during Hi-Res times transition.
	 */
	private static final String INVALID_DATE_STRING = "700101 000000";

	/**
	 * we use static instances of patterns. just initialise them once
	 * 
	 */
	private static void initialisePatterns()
	{
		if (_dfMillis == null)
		{
			FOUR_DIGIT_YEAR_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
			TWO_DIGIT_YEAR_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));

			_dfMillis = new SimpleDateFormat("yyMMdd HHmmss.SSS");
			_df = new SimpleDateFormat("yyMMdd HHmmss");
			_df.setTimeZone(TimeZone.getTimeZone("GMT"));
			_dfMillis.setTimeZone(TimeZone.getTimeZone("GMT"));

			// and the microsecond bits
			_micros = new DecimalFormat("000000");
			_millis = new DecimalFormat("000");
		}
	}

	static public String toString(final long theVal)
	{
		initialisePatterns();

		final java.util.Date theTime = new java.util.Date(theVal);
		String res;

		// first determine which pattern to use.
		DateFormat selectedFormat;
		if (theVal % 1000 > 0)
		{
			// ok, it contains milliseconds - include them in the output
			selectedFormat = _dfMillis;
		}
		else
		{
			selectedFormat = _df;
		}

		res = selectedFormat.format(theTime);

		return res;
	}

	/**
	 * formatting method which just exports the micro-seconds within a DTG
	 * 
	 * @param dtg
	 * @return
	 */
	public static String formatMicros(final Temporal dtg)
	{
		// check our declarations
		initialisePatterns();
		return _micros.format(dtg.getMicros() % 1000000);
	}

	/**
	 * output the hi-res date as a formatted string, supplying micro-second and
	 * milli-second decimal places as required.
	 * 
	 * @param time
	 *          - can't imagine. What-ever could this parameter be called for?
	 * @return formatted string
	 */
	public static String toStringHiRes(final Temporal time, final String formatStr)
	{
		String res;

		// hmm, see if we are actually working in micros
		final long micros = time.getMicros();
		if (micros % 1000 > 0)
		{
			res = toStringHiRes(time);
		}
		else
		{
			final DateFormat myDF = new SimpleDateFormat(formatStr);
			myDF.setTimeZone(TimeZone.getTimeZone("GMT"));
			res = myDF.format(time.getDate());
		}

		// cool, all finished
		return res;
	}

	/**
	 * output the hi-res date as a formatted string, supplying micro-second and
	 * milli-second decimal places as required.
	 * 
	 * @param time
	 *          - can't imagine. What-ever could this parameter be called for?
	 * @return formatted string
	 */
	public static String toStringHiRes(final Temporal time)
	{
		// check our declarations
		initialisePatterns();

		// so, have a look at the data
		long micros = time.getMicros();

		final long wholeSeconds = micros / 1000000;

		final StringBuffer res = new StringBuffer();
		res.append(toString(wholeSeconds * 1000));

		// do we have micros?
		if (micros % 1000 > 0)
		{
			// yes
			res.append(".");
			res.append(_micros.format(micros % 1000000));
		}
		else
		{
			// do we have millis?
			if (micros % 1000000 > 0)
			{
				// yes, convert the value to millis

				final long millis = micros = (micros % 1000000) / 1000;

				res.append(".");
				res.append(_millis.format(millis));
			}
			else
			{
				// just use the normal output
			}
		}

		return res.toString();

	}

	/**
	 * parse a date string using our format
	 */
	public static Temporal parseThis(final String dateToken, final String timeToken)
	{
		// do we have millis?
		final int decPoint = timeToken.indexOf(".");
		String milliStr, timeStr;
		if (decPoint > 0)
		{
			milliStr = timeToken.substring(decPoint, timeToken.length());
			timeStr = timeToken.substring(0, decPoint);
		}
		else
		{
			milliStr = "";
			timeStr = timeToken;
		}

		
		// sort out if we have to padd
		// check the date for missing leading zeros
		final String theDateToken = String.format("%06d", Integer.parseInt(dateToken));
		timeStr = String.format("%06d", Integer.parseInt(timeStr));

		final String composite = theDateToken + " " + timeStr + milliStr;

//		if (milliStr.length() > 0)
//			composite += milliStr;

		return parseThis(composite);
	}

	/**
	 * parse a date string using our format
	 */
	public static Temporal parseThis(final String rawText)
	{
		// make sure our two and four-digit date bits are initialised
		initialisePatterns();

		Date date = null;
		Temporal res = null;

		// right, start off by trimming spaces off the date
		final String theRawText = rawText.trim();

		// right. Special check to see if this is an incorrectly represented null
		// date (-1)
		if (theRawText.equals(NULL_DATE_STRING) || theRawText.equals(INVALID_DATE_STRING))
		{
			System.err.println("Invalid date read from xml file: " + theRawText);
			res = null;
		}
		else
		{

			String secondPart = theRawText;
			String subSecondPart = null;

			// start off by seeing if we have sub-millisecond date
			final int subSecondIndex = theRawText.indexOf('.');
			if (subSecondIndex > 0)
			{
				// so, there is a separator - extract the text before the separator
				secondPart = theRawText.substring(0, subSecondIndex);

				// just check that the '.' isn't the last character
				if (subSecondIndex < theRawText.length() - 1)
				{
					// yes, we do have digits after the separator
					subSecondPart = theRawText.substring(subSecondIndex + 1);
				}
			}

			// next determine if we have a 4-figure year value (in which case the
			// space will be in column 9
			final int spaceIndex = secondPart.indexOf(" ");

			try
			{
				if (spaceIndex > 6)
				{
					date = FOUR_DIGIT_YEAR_FORMAT.parse(secondPart);
				}
				else
				{
					date = TWO_DIGIT_YEAR_FORMAT.parse(secondPart);
				}
			}
			catch (final ParseException e1)
			{
				logger.info("Parsing error", e1);
			}

			int micros = 0;

			// do we have a sub-second part?
			if (subSecondPart != null)
			{
				// get the value
				micros = Integer.parseInt(subSecondPart);

				final int subSecLen = subSecondPart.length();

				// are we within the acceptable data resolution?
				if (subSecLen <= 6)
				{
					micros = micros * (int) (Math.pow(10, 6 - subSecLen));
				}
				else
				{
					System.err
							.println("Debrief is only capable of reading data to microsecond resolution (dtg:"
									+ theRawText + ")");
					micros = -1;
				}
			}

			if (micros != -1)
				if (date != null)
					res = new TemporalImpl(date.getTime(), micros);
		}

		return res;
	}

}
