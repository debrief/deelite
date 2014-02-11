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
package org.mwc.debrief.lite.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mwc.debrief.lite.layers.TrackLayer;
import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Track;

import com.bbn.openmap.Environment;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.gui.EmbeddedNavPanel;
import com.bbn.openmap.gui.OverlayMapPanel;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.proj.Mercator;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

/**
 * @author snpe
 *
 */
public class Utils {

	/**
	 * @param map
	 * @param trackLayer
	 * 
	 */
	public static double configureTracks(OverlayMapPanel map,
			TrackLayer trackLayer) {
		if (trackLayer ==null || map == null) {
			return map.getMapBean().getScale();
		}
		Map<String, Track> tracks = trackLayer.getTracks();
		if (tracks == null || tracks.size() <= 0) {
        	return map.getMapBean().getScale();
        }
		double minLat = 90f;
		double maxLat = -90f;
		double minLon = 90f;
		double maxLon = -90f;
		boolean changeProjection = false;
		List<PositionFix> positionFixes = new ArrayList<PositionFix>();
		for (Track track : tracks.values()) {
			positionFixes.addAll(track.getPositionFixes());
		}
		if (positionFixes.size() <= 0) {
			return map.getMapBean().getScale();
		}
		changeProjection = true;
		for (PositionFix p : positionFixes) {
			if (p.getSpatial().getLatitude() < minLat) {
				minLat = p.getSpatial().getLatitude();
			}
			if (p.getSpatial().getLatitude() > maxLat) {
				maxLat = p.getSpatial().getLatitude();
			}
			if (p.getSpatial().getLongitude() < minLon) {
				minLon = p.getSpatial().getLongitude();
			}
			if (p.getSpatial().getLongitude() > maxLon) {
				maxLon = p.getSpatial().getLongitude();
			}
		}

		if (changeProjection) {
			double lon = maxLon - (maxLon - minLon)/2;
			double lat = maxLat - (maxLat - minLat)/2;
			double length = maxLon - minLon;
			if ( (maxLat-minLat) > length) {
				length = maxLat - minLat;
			}
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = (int) screenSize.getWidth();
			int height = (int) screenSize.getHeight();
			float scale = (float) (length*width*1000f)/2.1f;
			
			Projection p = new Mercator(new LatLonPoint.Double(lat, lon),
					scale, width, height);
			MapBean mapBean = map.getMapBean();
			mapBean.setProjection(p);
			
			Object object = map.getMapHandler().get(EmbeddedNavPanel.class);
			if (object instanceof EmbeddedNavPanel) {
				EmbeddedNavPanel navPanel = (EmbeddedNavPanel) object;
				navPanel.setRecenterPoint(mapBean.getCenter());
			}
			OMPoint center = new OMPoint(lat, lon, 3);
			trackLayer.setCenter(center);
			Environment.set(Environment.Scale, Float.toString(scale));
			return scale;
		}
		return Environment.getDouble(Environment.Scale);
	}

}
