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

import org.mwc.debrief.lite.model.Spatial;

/**
 * @author snpe
 * 
 */
public class SpatialImpl implements Spatial {

	private static final long serialVersionUID = 1L;
	private final double longitude;
	private final double latitude;
	private final double depth;

	/**
	 * long winded constructor, taking raw arguments note: if there are decimal
	 * parts of degs or mins then the contents of the mins & secs, or secs
	 * (resp) are ignored. So, if there's 22.5 in the mins and 10 in the secs,
	 * this will go to 22 mins, 30 secs - using the mins fractional component
	 * and ignoring the secs value.
	 */
	public SpatialImpl(final double latDegs, final double latMin,
			final double latSec, final char latHem, final double longDegs,
			final double longMin, final double longSec, final char longHem,
			final double theDepth) {
		double theLatDegs = latDegs;
		double theLatMin = latMin;
		double theLongDegs = longDegs;
		double theLongMin = longMin;
		double theLongSec = longSec;
		double theLatSec = latSec;
		// this constructor allows for decimal values of degs and mins,
		// cascading them as appropriate

		// just check if we've got decimal values for degs or minutes. If so,
		// cascade them to other units
		double dec = decimalComponentOf(theLatDegs);
		if (dec > 0) {
			theLatDegs -= dec;
			theLatMin = dec * 60d;
			theLatSec = 0;
		}

		dec = decimalComponentOf(theLatMin);
		if (dec > 0) {
			theLatMin -= dec;
			theLatSec = dec * 60d;
		}

		// Now for longitude:
		//
		// just check if we've got decimal values for degs or minutes. If so,
		// cascade them to other units
		dec = decimalComponentOf(theLongDegs);
		if (dec > 0) {
			theLongDegs -= dec;
			theLongMin = dec * 60d;
			theLongSec = 0;
		}

		dec = decimalComponentOf(theLongMin);
		if (dec > 0) {
			theLongMin -= dec;
			theLongSec = dec * 60d;
		}

		// ok - calculat the degrees
		double latVal = theLatDegs + theLatMin / 60 + theLatSec / (60 * 60);
		double longVal = theLongDegs + theLongMin / 60 + theLongSec / (60 * 60);
		
		// and sort out the hemishere
		this.latitude =  (""+latHem).toUpperCase().equals("S") ? -latVal : latVal;
		this.longitude = (""+longHem).toUpperCase().equals("W") ? - longVal : longVal;
		this.depth = theDepth;
	}

	public SpatialImpl(double latitude, double longitude, double depth) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.depth = depth;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.debrief.lite.model.Spatial#getLatitude()
	 */
	@Override
	public double getLatitude() {
		return latitude;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.debrief.lite.model.Spatial#getLongitude()
	 */
	@Override
	public double getLongitude() {
		return longitude;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.debrief.lite.model.Spatial#getDepth()
	 */
	@Override
	public double getDepth() {
		return depth;
	}

	/**
	 * return the decimal component of the supplied number
	 * 
	 */
	private double decimalComponentOf(final double latDeg) {
		return latDeg - Math.floor(latDeg);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpatialImpl [longitude=" + longitude + ", latitude=" + latitude
				+ ", depth=" + depth + "]";
	}
}
