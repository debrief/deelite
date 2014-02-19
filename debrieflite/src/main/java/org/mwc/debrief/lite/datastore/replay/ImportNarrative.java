// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportNarrative.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: ImportNarrative.java,v $
// Revision 1.7  2006/08/08 12:55:29  Ian.Mayo
// Restructure loading narrative entries (so we can see it from CMAP)
//
// Revision 1.6  2006/07/17 11:06:15  Ian.Mayo
// Tidy export formatting, only export if not Narrative2 entrty
//
// Revision 1.5  2005/12/13 09:04:36  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2004/11/25 10:24:16  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/11 11:52:44  Ian.Mayo
// Reflect new directory structure
//
// Revision 1.2  2004/08/19 14:12:47  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.1.1.2  2003/07/21 14:47:49  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-07-01 14:11:50+01  ian_mayo
// extend test
//
// Revision 1.4  2003-06-03 16:25:32+01  ian_mayo
// Minor tidying, lots of testing
//
// Revision 1.3  2003-03-19 15:37:49+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 11:34:44+01  ian_mayo
// Check we're of the correct type
//
// Revision 1.1  2002-05-28 09:12:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-02-26 16:35:47+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.0  2001-07-17 08:41:33+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-12 12:14:18+01  novatech
// Trim any leading whitespace from the narrative text
//
// Revision 1.1  2001-07-09 13:57:58+01  novatech
// Initial revision
//

package org.mwc.debrief.lite.datastore.replay;

import java.util.StringTokenizer;

import org.mwc.debrief.lite.model.NarrativeEntry;
import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.model.impl.NarrativeEntryImpl;
import org.mwc.debrief.lite.utils.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
public final class ImportNarrative implements LineImporter {
	/**
	 * the type for this string
	 */
	private static final String PREFIX = ";NARRATIVE:";
	private Exception exception;

	/**
	 * read in this string and return a Label
	 */
	public final Object readLine(final String line) {

		// get a stream from the string
		final StringTokenizer st = new StringTokenizer(line);

		// declare local variables
		Temporal DTG = null;
		String theTrack = null;
		String theEntry = null;

		// skip the comment identifier
		st.nextToken();

		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		DTG = DebriefFormatDateTime.parseThis(dateToken, timeToken);

		// now the track name
		theTrack = ImportFix.checkForQuotedTrackName(st);

		// and now read in the message
		theEntry = st.nextToken("\r").trim();

		// can we trim any leading whitespace?
		theEntry = theEntry.trim();

		final NarrativeEntry entry = new NarrativeEntryImpl(theTrack, DTG, theEntry);

		return entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.debrief.lite.datastore.replay.LineImporter#canImport(String line)
	 */

	@Override
	public boolean canImport(String line) {
		return line != null && !line.trim().isEmpty()
				&& line.startsWith(PREFIX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.debrief.lite.datastore.replay.LineImporter#getException()
	 */
	@Override
	public Exception getException() {
		return exception;
	}

}
