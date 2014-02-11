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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Track;
import org.mwc.debrief.lite.utils.ReadingUtility;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.layer.policy.BufferedImageRenderPolicy;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.omGraphics.OMTextLabeler;

/**
 * This layer is a main plot layer. It paints track(s).
 * 
 */
public class TrackLayer extends OMGraphicHandlerLayer {

	private static final long serialVersionUID = 1L;
	private OMPoint center;
	private Map<String, Track> tracks;

	public TrackLayer() {
		setName("Tracks");
		setProjectionChangePolicy(new com.bbn.openmap.layer.policy.StandardPCPolicy(
				this, true));
		setRenderPolicy(new BufferedImageRenderPolicy());
	}

	public synchronized OMGraphicList prepare() {
		OMGraphicList list = getList();
		if (list == null) {
			list = init();
		}

		if (list != null) {
			list.generate(getProjection());
		}

		return list;
	}

	public OMGraphicList init() {

		OMGraphicList list = getList();
		if (list != null) {
			return list;
		}
		OMGraphicList omList = new OMGraphicList();

		if (tracks == null || tracks.size() <= 0) {
			return list;
		}

		OMGraphicList pointList = new OMGraphicList();
		
		for (Track track:tracks.values()) {
			OMGraphicList trackList = getTrackList(track);
			pointList.addAll(trackList);
			
		}		
		
//		if (center != null) {
//			pointList.add(center);
//		}

		omList.add(pointList);

		return omList;
	}

	/**
	 * @param track
	 * @return
	 */
	private OMGraphicList getTrackList(Track track) {
		OMGraphicList trackList = new OMGraphicList();
		OMPoint latest = null;
		for (PositionFix positionFix:track.getPositionFixes()) {
			OMPoint point = new OMPoint(positionFix.getSpatial().getLatitude(), positionFix.getSpatial().getLongitude(), 3);
			//trackList.add(point);
			if (latest != null) {
				OMLine line = new OMLine(latest.getLat(), latest.getLon(), point.getLat(), point.getLon(), OMGraphic.LINETYPE_GREATCIRCLE);
				String symbology = positionFix.getSymbology();
				line.setLinePaint(ReadingUtility.getSymbologyColor(symbology));
				line.setStroke(new BasicStroke(2));
				trackList.add(line);
			} else {
				point.setLinePaint(Color.white);
				point.setOval(false);
				point.putAttribute(OMGraphicConstants.LABEL, new OMTextLabeler(positionFix.getName()));
				trackList.add(point);
			}
			latest = point;
		}
		return trackList;
	}

	/**
	 * @return the center
	 */
	public OMPoint getCenter() {
		return center;
	}

	/**
	 * @param center the center to set
	 */
	public void setCenter(OMPoint center) {
		this.center = center;
	}

	/**
	 * @param tracks
	 */
	public void setTracks(Map<String, Track> tracks) {
		this.tracks = tracks;
		if (tracks != null && tracks.size() > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append("Tracks (");
			Iterator<String> iterator = tracks.keySet().iterator();
			while (iterator.hasNext()) {
				builder.append(iterator.next());
				if (iterator.hasNext()) {
					builder.append(",");
				}
			}
			builder.append(")");
			setName(builder.toString());
		}
	}

	/**
	 * @return
	 */
	public Map<String, Track> getTracks() {
		return tracks;
	}

}