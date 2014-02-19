// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportPeriodText.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ImportPeriodText.java,v $
// Revision 1.3  2005/12/13 09:04:37  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:17  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:50  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:39+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:46+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:38+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-02-26 16:38:15+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.1  2002-02-26 16:36:11+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.0  2001-07-17 08:41:31+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-24 11:36:57+00  novatech
// setString has changed to setLabel in label
//
// Revision 1.2  2001-01-17 13:23:46+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.1  2001-01-03 13:40:45+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:13  ianmayo
// initial import of files
//
// Revision 1.2  2000-10-09 13:37:37+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.1  2000-09-26 10:57:45+01  ian_mayo
// Initial revision
//

package org.mwc.debrief.lite.datastore.replay;

import java.text.ParseException;
import java.util.StringTokenizer;

import org.mwc.debrief.lite.model.PeriodText;
import org.mwc.debrief.lite.model.Spatial;
import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.model.impl.PeriodTextImpl;
import org.mwc.debrief.lite.model.impl.SpatialImpl;
import org.mwc.debrief.lite.utils.DebriefFormatDateTime;
import org.mwc.debrief.lite.utils.ReadingUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class to parse a label from a line of text
 */
final class ImportPeriodText implements LineImporter {
	
	static final Logger logger = LoggerFactory.getLogger(ImportPeriodText.class);
	
	/**
	 * the type for this string
	 */
	private static final String PREFIX = ";PERIODTEXT:";

	private Exception exception;

	/**
	 * read in this string and return a Label
	 */
	@Override
	public final Object readLine(final String line) {

		// get a stream from the string
		final StringTokenizer st = new StringTokenizer(line);

		// declare local variables
		Spatial theLoc;
		double latDeg, longDeg, latMin, longMin;
		char latHem, longHem;
		double latSec, longSec;
		String theText = null;
		String theSymbology;
		String dateStr;
		Temporal theDate = null;
		Temporal theOtherDate = null;
		double theDepth = 0d;

		// skip the comment identifier
		st.nextToken();

		// start with the symbology
		theSymbology = st.nextToken();

		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

		// combine the date, a space, and the time
		dateStr = st.nextToken() + " " + st.nextToken();

		// and extract the date
		theOtherDate = DebriefFormatDateTime.parseThis(dateStr);

		// now the location
		try {
			latDeg = ReadingUtility.readThisDouble(st.nextToken());
			latMin = ReadingUtility.readThisDouble(st.nextToken());
			latSec = ReadingUtility.readThisDouble(st.nextToken());

			/**
			 * now, we may have trouble here, since there may not be a space
			 * between the hemisphere character and a 3-digit latitude value -
			 * so BE CAREFUL
			 */
			final String vDiff = st.nextToken();
			if (vDiff.length() > 3) {
				// hmm, they are combined
				latHem = vDiff.charAt(0);
				final String secondPart = vDiff.substring(1, vDiff.length());
				longDeg = ReadingUtility.readThisDouble(secondPart);
			} else {
				// they are separate, so only the hem is in this one
				latHem = vDiff.charAt(0);
				longDeg = ReadingUtility.readThisDouble(st.nextToken());
			}
			longMin = ReadingUtility.readThisDouble(st.nextToken());
			longSec = ReadingUtility.readThisDouble(st.nextToken());
			longHem = st.nextToken().charAt(0);

			final String depthStr = st.nextToken();
			try {
				theDepth = ReadingUtility.readThisDouble(depthStr);
			} catch (final ParseException e) {
				// hey, it didn't contain a double, just use it as a text string
				theText = depthStr;
			}

			if (st.hasMoreTokens()) {
				// if we haven't read in part of the message already, read in
				// the remainder
				if (theText != null) {
					// and now read in the remainder of the line, and append to
					// the message
					theText += " " + st.nextToken("\r").trim();
				} else {
					// and now read in the message, we have already read the
					// depth
					theText = st.nextToken("\r").trim();
				}
			}

			// create the tactical data
			theLoc = new SpatialImpl(latDeg, latMin, latSec, latHem, longDeg,
					longMin, longSec, longHem, theDepth);

			// create the fix ready to store it
			final PeriodText lw = new PeriodTextImpl(theText, theLoc,
					theSymbology, theDate,
					theOtherDate);

			return lw;
		} catch (final ParseException pe) {
			logger.info("Whilst import PeriodText {}", pe);
			exception = pe;
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.datastore.replay.LineImporter#canImport(java.lang.String)
	 */
	@Override
	public boolean canImport(String line) {
		return line != null && !line.trim().isEmpty()
				&& line.startsWith(PREFIX);
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.datastore.replay.LineImporter#getException()
	 */
	@Override
	public Exception getException() {
		return exception;
	}

}