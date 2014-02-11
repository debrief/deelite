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
package org.mwc.debrief.lite.datastore.replay;

import java.text.ParseException;
import java.util.StringTokenizer;

import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Spatial;
import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.model.impl.PositionFixImpl;
import org.mwc.debrief.lite.model.impl.SpatialImpl;
import org.mwc.debrief.lite.utils.Conversions;
import org.mwc.debrief.lite.utils.DebriefFormatDateTime;
import org.mwc.debrief.lite.utils.ReadingUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * import a fix from a line of text (in Replay format)
 */
public final class ImportFix implements LineImporter {

	static final Logger logger = LoggerFactory.getLogger(ImportFix.class);
	
	private Exception exception;
	
	@Override
	public final Object readLine(final String line) {

		exception = null;
		final StringTokenizer st = new StringTokenizer(line);

		// declare local variables
		Spatial theLoc;
		double latDeg, longDeg, latMin, longMin;
		char latHem, longHem;
		double latSec, longSec;
		Temporal theDate = null;
		double theCourse;
		double theSpeed;
		double theDepth;

		String theTrackName;
		String theSymbology;

		// parse the line
		// 951212 050000.000 CARPET @C 12 11 10.63 N 11 41 52.37 W 269.7 2.0 0

		// first the date

		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

		// trouble - the track name may have been quoted, in which case we will
		// pull
		// in the remaining fields aswell
		theTrackName = checkForQuotedTrackName(st);

		// trim the track name, just in case
		theTrackName.trim();

		theSymbology = st.nextToken(normalDelimiters);

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

			// parse (and convert) the vessel status parameters
			theCourse = Conversions.Degs2Rads(ReadingUtility
					.readThisDouble(st.nextToken()));
			theSpeed = Conversions.Kts2Yps(Double.valueOf(
					st.nextToken()).doubleValue());

			// get the depth value
			final String depthStr = st.nextToken();

			// we know that the Depth str may be NaN, but Java can interpret
			// this
			// directly
			if (depthStr.equals("NaN"))
				theDepth = Double.NaN;
			else
				theDepth = ReadingUtility.readThisDouble(depthStr);

			// NEW FEATURE: we take any remaining text, and use it as a label
			String txtLabel = null;
			if (st.hasMoreTokens())
				txtLabel = st.nextToken("\r");
			if (txtLabel != null)
				txtLabel = txtLabel.trim();

			// create the tactical data
			theLoc = new SpatialImpl(latDeg, latMin, latSec, latHem, longDeg,
					longMin, longSec, longHem, theDepth);

			// create the fix ready to store it
			final PositionFix positionFix = new PositionFixImpl(theTrackName,
					theDate, theLoc, theCourse, theSpeed, theSymbology);
			if ((txtLabel != null) && (txtLabel.length() > 0)) {
				positionFix.setLabel(txtLabel);
			}
			return positionFix;
		} catch (final ParseException pe) {
			logger.error("Line {}\nException:\n{}", line, pe);
			exception = pe;
			return null;
		}
	}

	@Override
	public boolean canImport(String line) {
		return line != null && !line.trim().isEmpty()
				&& !line.startsWith(PREFIX);
	}

	/**
	 * when importing the track name, it may turn out to be quoted, in which case
	 * we consume the remaining tokens until we get another quote
	 * 
	 * @param st
	 *          the tokenised stream to read the name from
	 * @return a string containing the (possibly multi-word) track name
	 */
	static public String checkForQuotedTrackName(final StringTokenizer st)
	{
		String theTrackName = st.nextToken();

		// so, does the track name contain a quote character?
		final int quoteIndex = theTrackName.indexOf("\"");
		if (quoteIndex >= 0)
		{
			// aah, but, we may have just read in all of the item. just check if
			// the
			// token contains
			// both speech marks...
			final int secondQuoteIndex = theTrackName.indexOf("\"", quoteIndex + 1);

			if (secondQuoteIndex >= 0)
			{
				// yes, we have caught both quotes
				// just trim off the quote marks
				theTrackName = theTrackName.substring(1, theTrackName.length() - 1);
			}
			else
			{
				// no, we just caught the first quote.
				// fish around for the second one.

				String lastPartOfName = st.nextToken(quoteDelimiter);

				// yup. the ne
				theTrackName += lastPartOfName;

				// and trim away the quote
				theTrackName = theTrackName.substring(theTrackName.indexOf("\"") + 1);

				// consume the trailing quote delimiter (note - we allow spaces
				// & tabs)
				lastPartOfName = st.nextToken(" \t");
			}
		}
		return theTrackName;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.datastore.replay.LineImporter#getException()
	 */
	@Override
	public Exception getException() {
		return exception;
	}

}
