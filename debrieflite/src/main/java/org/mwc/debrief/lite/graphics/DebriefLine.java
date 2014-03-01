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
package org.mwc.debrief.lite.graphics;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMLine;

/**
 * @author snpe
 * 
 */
public class DebriefLine extends OMLine {

	private static final long serialVersionUID = 1L;
	private DebriefPoint startPoint;
	private DebriefPoint endPoint;

	/**
	 * @param startPoint
	 * @param endPoint
	 */
	public DebriefLine(DebriefPoint startPoint, DebriefPoint endPoint) {
		this(startPoint, endPoint, OMGraphic.LINETYPE_GREATCIRCLE);
	}
	
	/**
	 * @param startPoint
	 * @param endPoint
	 * @param lineType
	 */
	public DebriefLine(DebriefPoint startPoint, DebriefPoint endPoint, int lineType) {
		super(startPoint.getLat(), startPoint.getLon(), endPoint.getLat(),
				endPoint.getLon(), OMGraphic.LINETYPE_GREATCIRCLE);
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	/**
	 * @return the startPoint
	 */
	public DebriefPoint getStartPoint() {
		return startPoint;
	}

	/**
	 * @return the endPoint
	 */
	public DebriefPoint getEndPoint() {
		return endPoint;
	}
}
