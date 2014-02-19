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

import org.mwc.debrief.lite.model.NarrativeEntry;
import org.mwc.debrief.lite.model.Temporal;

/**
 * @author snpe
 *
 */
public class NarrativeEntryImpl implements NarrativeEntry {

	private static final long serialVersionUID = 1L;
	private String _track;
	private Temporal _date;
	private String _entry;
	private String _type;
	
	/**
	 * @param theTrack
	 * @param dTG
	 * @param theEntry
	 */
	public NarrativeEntryImpl(String theTrack, Temporal dTG, String theEntry) {
		this(theTrack, null, dTG, theEntry);
	}

	/**
	 * @param theTrack
	 * @param theType
	 * @param dTG
	 * @param theEntry
	 */
	public NarrativeEntryImpl(String theTrack, String theType, Temporal dTG,
			String theEntry) {
		_track = theTrack;
		_date = dTG;
		_entry = theEntry;
		_type = theType;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.Named#getName()
	 */
	@Override
	public String getName() {
		return _track;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.Named#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		_track = name;
	}

	
	@Override
	public Temporal getDate() {
		return _date;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.NarrativeEntry#getType()
	 */
	@Override
	public String getType() {
		return _type;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.NarrativeEntry#setType(java.lang.String)
	 */
	@Override
	public void setType(String type) {
		_type = type;
	}

	/**
	 * @return the _entry
	 */
	@Override
	public String getEntry() {
		return _entry;
	}

	/**
	 * @param _entry the _entry to set
	 */
	@Override
	public void setEntry(String _entry) {
		this._entry = _entry;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.NarrativeEntry#setDate(org.mwc.debrief.lite.model.Temporal)
	 */
	@Override
	public void setDate(Temporal date) {
		_date = date;
	}

}
