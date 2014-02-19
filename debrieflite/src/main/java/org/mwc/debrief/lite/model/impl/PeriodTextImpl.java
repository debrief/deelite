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
package org.mwc.debrief.lite.model.impl;

import org.mwc.debrief.lite.model.PeriodText;
import org.mwc.debrief.lite.model.Spatial;
import org.mwc.debrief.lite.model.Temporal;

/**
 * @author snpe
 * 
 */
public class PeriodTextImpl implements PeriodText {

	private Temporal _otherDate;
	private Temporal _date;
	private String _symbology;
	private Spatial _location;
	private String _text;

	/**
	 * @param theText
	 * @param theLoc
	 * @param theSymbology
	 * @param theDate
	 * @param theOtherDate
	 */
	public PeriodTextImpl(String theText, Spatial theLoc, String theSymbology,
			Temporal theDate, Temporal theOtherDate) {
		_text = theText;
		_location = theLoc;
		_symbology = theSymbology;
		_date = theDate;
		_otherDate = theOtherDate;
	}

	/**
	 * @param theText
	 * @param theLoc
	 * @param theSymbology
	 */
	public PeriodTextImpl(String theText, Spatial theLoc, String theSymbology) {
		this(theText, theLoc, theSymbology, null, null);
	}

	/**
	 * @return the _otherDate
	 */
	@Override
	public Temporal getOtherDate() {
		return _otherDate;
	}

	/**
	 * @return the _date
	 */
	@Override
	public Temporal getDate() {
		return _date;
	}

	/**
	 * @return the _symbology
	 */
	@Override
	public String getSymbology() {
		return _symbology;
	}

	/**
	 * @return the _location
	 */
	@Override
	public Spatial getLocation() {
		return _location;
	}

	/**
	 * @return the _text
	 */
	@Override
	public String getText() {
		return _text;
	}

}
