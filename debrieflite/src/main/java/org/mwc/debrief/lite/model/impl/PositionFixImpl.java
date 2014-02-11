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

import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Spatial;
import org.mwc.debrief.lite.model.Temporal;

/**
 * @author snpe
 *
 */
public class PositionFixImpl implements PositionFix {

	private static final long serialVersionUID = 1L;
	private String name;
	private Spatial location;
	private Temporal temporal;
	private double speed; // in yards per second
	private double course; // in radians
	private String symbology;
	private String label;
	
	/**
	 * @param theTrackName
	 * @param theDate
	 * @param theLoc
	 * @param theCourse
	 * @param theSpeed
	 */
	public PositionFixImpl(String theTrackName, Temporal theDate,
			Spatial theLoc, double theCourse, double theSpeed,
			String theSymbology) {
		name = theTrackName;
		temporal = theDate;
		location = theLoc;
		course = theCourse;
		speed = theSpeed;
		symbology = theSymbology;
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
	 * @see org.mwc.debrief.lite.model.BaseElement#getTemporal()
	 */
	@Override
	public Temporal getTemporal() {
		return temporal;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.BaseElement#getSpatial()
	 */
	@Override
	public Spatial getSpatial() {
		return location;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.BaseElement#setSpatial(org.mwc.debrief.lite.model.Spatial)
	 */
	@Override
	public void setSpatial(Spatial spatial) {
		this.location = spatial;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.BaseElement#setTemporal(org.mwc.debrief.lite.model.Temporal)
	 */
	@Override
	public void setTemporal(Temporal temporal) {
		this.temporal = temporal;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.PositionFix#getSpeed()
	 */
	@Override
	public double getSpeed() {
		return speed;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.PositionFix#getCourse()
	 */
	@Override
	public double getCourse() {
		return course;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.PositionFix#setSpeed(double)
	 */
	@Override
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.PositionFix#setCourse(double)
	 */
	@Override
	public void setCourse(double course) {
		this.course = course;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.PositionFix#getSymbology()
	 */
	@Override
	public String getSymbology() {
		return symbology;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.PositionFix#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.PositionFix#setSymbology(java.lang.String)
	 */
	@Override
	public void setSymbology(String symbology) {
		this.symbology = symbology;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.model.PositionFix#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PositionFixImpl [name=" + name + ", location=" + location
				+ ", temporal=" + temporal + ", speed=" + speed + ", course="
				+ course + ", symbology=" + symbology + ", label=" + label
				+ "]";
	}

}
