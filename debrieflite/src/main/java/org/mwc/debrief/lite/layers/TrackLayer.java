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
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mwc.debrief.lite.DebriefMain;
import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.model.Track;
import org.mwc.debrief.lite.model.impl.TemporalImpl;
import org.mwc.debrief.lite.time.TimeEvent;
import org.mwc.debrief.lite.time.TimeListener;
import org.mwc.debrief.lite.utils.ReadingUtility;
import org.mwc.debrief.lite.utils.Utils;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.layer.policy.BufferedImageRenderPolicy;
import com.bbn.openmap.layer.policy.StandardPCPolicy;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.omGraphics.OMTextLabeler;
import com.bbn.openmap.proj.GreatCircle;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.coords.LatLonPoint;

/**
 * This layer is a main plot layer. It paints track(s).
 * 
 */
public class TrackLayer extends OMGraphicHandlerLayer implements TimeListener {

	private static final long serialVersionUID = 1L;
	private OMPoint center;
	private Temporal currentTime;
	private Temporal startTime;
	private Temporal endTime;
	private Map<String, Track> tracks;
	
	public TrackLayer() {
		setName("Tracks");
		setProjectionChangePolicy(new StandardPCPolicy(
				this, true));
		BufferedImageRenderPolicy policy = new BufferedImageRenderPolicy();
		Map<Key, Object> map = new HashMap<RenderingHints.Key, Object>();
		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
		RenderingHints rh = new RenderingHints(map);
		policy.setRenderingHints(rh);
		setRenderPolicy(policy);
		DebriefMain.getTimeController().addTimeListener(this);
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

		PositionFix currentPositionFix = null;
		currentTime = null;
		
		if (tracks != null) {
			for (Track track:tracks.values()) {
				if (track != null && track.getPositionFixes() != null && track.getPositionFixes().size() > 0) {
					currentPositionFix = track.getPositionFixes().get(0);
					currentTime = currentPositionFix.getTemporal();
					if (currentTime != null) {
						newTime(new TimeEvent(currentTime.getTime(), this));
					}
					OMPoint currentPoint = getPoint(currentPositionFix);
					omList.add(currentPoint);
				}
			}
		} 
		Utils.currentTimeChanged(currentTime, this);
		return omList;
	}

	/**
	 * @param track
	 * @return
	 */
	private OMGraphicList getTrackList(Track track) {
		OMGraphicList trackList = new OMGraphicList();
		OMPoint latest = null;
		startTime = endTime = null;
		for (PositionFix positionFix:track.getPositionFixes()) {
			OMPoint point = new OMPoint(positionFix.getSpatial().getLatitude(), positionFix.getSpatial().getLongitude(), 3);
			Temporal temporal = positionFix.getTemporal();
			if (temporal != null) {
				if (startTime == null || temporal.compareTo(startTime) < 0) {
					startTime = temporal;
				}
				if (endTime == null || temporal.compareTo(endTime) > 0) {
					endTime = temporal;
				}
			}
			if (latest != null) {
				OMLine line = new OMLine(latest.getLat(), latest.getLon(), point.getLat(), point.getLon(), OMGraphic.LINETYPE_GREATCIRCLE);
				String symbology = positionFix.getSymbology();
				line.setLinePaint(ReadingUtility.getSymbologyColor(symbology));
				line.setStroke(new BasicStroke(2));
				trackList.add(line);
			} 
			latest = point;
		}
		return trackList;
	}
	
	private OMPoint getPoint(PositionFix positionFix) {
		OMPoint point = new OMPoint(positionFix.getSpatial().getLatitude(), positionFix.getSpatial().getLongitude(), 6);
		point.setLinePaint(Color.white);
		point.setOval(false);
		point.putAttribute(OMGraphicConstants.LABEL, new OMTextLabeler(positionFix.getName()));
		return point;
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

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.time.TimeListener#getSource()
	 */
	@Override
	public Object getSource() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.time.TimeListener#newTime(org.mwc.debrief.lite.time.TimeEvent)
	 */
	@Override
	public void newTime(TimeEvent event) {
		if (event == null) {
			return;
		}
		if (event.getSource() != this) {
			if (startTime == null || endTime == null) {
				return;
			}
			long newTime = event.getTime();
			if (startTime.getTime() > newTime) {
				newTime = startTime.getTime();
			} else if (endTime.getTime() < newTime) {
				newTime = endTime.getTime();
			}
			if (newTime == currentTime.getTime()) {
				return;
			}
			OMGraphicList omg = calculateTimePoints(newTime); 
			if (omg != null) {
				setList(omg);
				doPrepare();
			}
		}
	}

	/**
	 * @param newTime
	 * @return
	 */
	private OMGraphicList calculateTimePoints(long newTime) {
		tracks = getTracks();
		if (tracks == null || tracks.size() <= 0) {
			return null;
		}
		OMGraphicList omg = new OMGraphicList();
		omg.addAll(getList());
		Iterator<OMGraphic> iterator = omg.iterator();
		while (iterator.hasNext()) {
			OMGraphic graphic = iterator.next();
			if (graphic instanceof OMPoint) {
				iterator.remove();
			}
		}
		currentTime = new TemporalImpl(newTime);
		for (Track track:tracks.values()) {
			calculateTimePoints(track, omg);
		}
		
		return omg;
	}

	/**
	 * @param track
	 * @param omg 
	 */
	private void calculateTimePoints(Track track, OMGraphicList omg) {
		if (track != null && track.getPositionFixes().size() > 0) {
			PositionFix latestFix = null;
			for (PositionFix positionFix:track.getPositionFixes()) {
				Temporal fixTemporal = positionFix.getTemporal();
				if (fixTemporal == null) {
					continue;
				}
				if (currentTime.equals(fixTemporal)) {
					OMPoint point = getPoint(positionFix);
					omg.add(point);
					latestFix = null;
					break;
				}
				if (fixTemporal.compareTo(currentTime) < 0) {
					latestFix = positionFix;
				} else {
					break;
				}
			}
			if (latestFix != null) {
				int index = track.getPositionFixes().indexOf(latestFix);
				if (index >= 0 && track.getPositionFixes().size() >= index+1) {
					PositionFix nextFix = track.getPositionFixes().get(index + 1);
					Temporal start = latestFix.getTemporal();
					Temporal end = nextFix.getTemporal();
					if (start != null && end != null) {
						long time = currentTime.getTime() - start.getTime();
						long timeRange = end.getTime() - start.getTime();
						if (timeRange > 0) {
							double[] points = new double[4];
							points[0] = nextFix.getSpatial().getLatitude();
							points[1] = nextFix.getSpatial().getLongitude();
							points[2] = latestFix.getSpatial().getLatitude();
							points[3] = latestFix.getSpatial().getLongitude();
							ProjMath.arrayDegToRad(points);
							double distanceRange = GreatCircle.sphericalDistance(points[0], points[1],
											points[2], points[3]);
							double distance = time * distanceRange / timeRange;
							//double angle = GreatCircle.sphericalAzimuth(points[0], points[1],
							//				points[2], points[3]);
							LatLonPoint llp = GreatCircle.pointAtDistanceBetweenPoints(points[0], points[1],
											points[2], points[3], distance, -1);
							if (llp != null) {
								OMPoint point = getPoint(llp, latestFix.getName());
								omg.add(point);
							}
						}
						
					}
					
				}
			}
		}
	}

	/**
	 * @param llp
	 * @return
	 */
	private OMPoint getPoint(LatLonPoint llp, String name) {
		OMPoint point = new OMPoint(llp.getLatitude(), llp.getLongitude(), 6);
		point.setLinePaint(Color.white);
		point.setOval(false);
		if (name != null) {
			point.putAttribute(OMGraphicConstants.LABEL, new OMTextLabeler(name));
		}
		return point;
	}

	/**
	 * @return the currentTime
	 */
	public Temporal getCurrentTime() {
		return currentTime;
	}

	/**
	 * @return the startTime
	 */
	public Temporal getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public Temporal getEndTime() {
		return endTime;
	}

}