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
package org.mwc.debrief.lite.layers;

import java.util.List;

import org.mwc.debrief.lite.graphics.DebriefLine;
import org.mwc.debrief.lite.model.PositionFix;

import com.bbn.openmap.omGraphics.OMPoint;

/**
 * @author snpe
 * 
 */
public class TrackInfo {

	private PositionFix startFix;
	private PositionFix endFix;
	private OMPoint currentPoint;
	private List<DebriefLine> lineList;

	/**
	 * @param startFix
	 * @param endFix
	 * @param currentPoint
	 */
	public TrackInfo(PositionFix startFix, PositionFix endFix,
			OMPoint currentPoint, List<DebriefLine> list) {
		super();
		this.startFix = startFix;
		this.endFix = endFix;
		this.currentPoint = currentPoint;
		this.lineList = list;
	}

	/**
	 * @return the startFix
	 */
	public PositionFix getStartFix() {
		return startFix;
	}

	/**
	 * @return the endFix
	 */
	public PositionFix getEndFix() {
		return endFix;
	}

	/**
	 * @return the currentPoint
	 */
	public OMPoint getCurrentPoint() {
		return currentPoint;
	}

	/**
	 * @return the lineList
	 */
	public List<DebriefLine> getLineList() {
		return lineList;
	}

	/**
	 * @param startFix the startFix to set
	 */
	public void setStartFix(PositionFix startFix) {
		this.startFix = startFix;
	}

	/**
	 * @param endFix the endFix to set
	 */
	public void setEndFix(PositionFix endFix) {
		this.endFix = endFix;
	}

	/**
	 * @param currentPoint the currentPoint to set
	 */
	public void setCurrentPoint(OMPoint currentPoint) {
		this.currentPoint = currentPoint;
	}

}