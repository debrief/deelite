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

import java.util.LinkedList;
import java.util.List;

import org.mwc.debrief.lite.model.Bearing;
import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Track;

/**
 * @author snpe
 *
 */
public class TrackImpl implements Track {

	private static final long serialVersionUID = 1L;
	private String name;
	private List<PositionFix> positionFixes = new LinkedList<PositionFix>();
	private List<Bearing> bearings = new LinkedList<Bearing>();
	
	public TrackImpl() {
	}
	/**
	 * @param name
	 */
	public TrackImpl(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.Named#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.Named#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.Track#getBearings()
	 */
	@Override
	public List<Bearing> getBearings() {
		return bearings;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.Track#getPositionFixes()
	 */
	@Override
	public List<PositionFix> getPositionFixes() {
		return positionFixes;
	}

}
