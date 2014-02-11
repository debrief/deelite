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
package org.mwc.debrief.lite.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import com.bbn.openmap.event.CenterSupport;
import com.bbn.openmap.gui.EmbeddedNavPanel;
import com.bbn.openmap.gui.OverlayMapPanel;

/**
 * 
 * @author snpe
 *
 */
public class FitToWindowAction extends AbstractDebriefAction {

	private static final long serialVersionUID = 1L;
	private OverlayMapPanel map;
	private double scale, longitude, latitude;
	private CenterSupport centerSupport;
	
	public FitToWindowAction() {
		super("Fit To Window", "Fit To Window (Alt+8)", "fit_to_win.gif", KeyEvent.VK_8);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (map != null && scale != 0) {
			map.getMapBean().setScale((float) scale);
			centerSupport.fireCenter(latitude, longitude);
		}
	}

	/**
	 * @param tracks
	 */
	public void setScale(double scale) {
		this.scale = scale;
		
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(OverlayMapPanel map) {
		this.map = map;
		centerSupport = new CenterSupport(map.getMapBean());
		centerSupport.add(map.getMapBean());
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

}
