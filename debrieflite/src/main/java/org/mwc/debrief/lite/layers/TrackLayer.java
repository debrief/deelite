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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mwc.debrief.lite.DebriefMain;
import org.mwc.debrief.lite.graphics.DebriefLine;
import org.mwc.debrief.lite.graphics.DebriefPoint;
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

	private static final int SNAIL_TIME = 10*60*1000;
	private static final int ALPHA_STEP = 30;
	private static final long serialVersionUID = 1L;
	private OMPoint center;
	private Temporal currentTime;
	private Temporal startTime;
	private Temporal endTime;
	private Map<String, Track> tracks;
	private Map<Track, TrackInfo> trackInfos = new HashMap<Track, TrackInfo>();
	private boolean snailMode;
	
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
			if (track != null && track.getPositionFixes() != null && track.getPositionFixes().size() > 0) {
				List<DebriefLine> trackList = getTrackList(track);
				pointList.addAll(trackList);
				PositionFix fix = track.getPositionFixes().get(0);
				currentTime = fix.getTemporal();
				if (currentTime != null) {
					newTime(new TimeEvent(currentTime.getTime(), this));
				}
				OMPoint currentPoint = getPoint(fix);
				pointList.add(currentPoint);
				TrackInfo info = new TrackInfo(fix, fix, currentPoint, trackList);
				trackInfos.put(track, info);
			}
		}		
		
//		if (center != null) {
//			pointList.add(center);
//		}

		omList.add(pointList);

		Utils.currentTimeChanged(currentTime, this);
		return omList;
	}

	/**
	 * @param track
	 * @return
	 */
	private List<DebriefLine> getTrackList(Track track) {
		List<DebriefLine> trackList = new LinkedList<DebriefLine>();
		DebriefPoint latest = null;
		startTime = endTime = null;
		for (PositionFix positionFix:track.getPositionFixes()) {
			DebriefPoint point = new DebriefPoint(positionFix, 3);
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
				DebriefLine line = new DebriefLine(latest, point, OMGraphic.LINETYPE_GREATCIRCLE);
				String symbology = positionFix.getSymbology();
				line.setLinePaint(ReadingUtility.getSymbologyColor(symbology));
				line.setStroke(new BasicStroke(2));
				trackList.add(line);
			} 
			latest = point;
		}
		return trackList;
	}
	
	private DebriefPoint getPoint(PositionFix positionFix) {
		DebriefPoint point = new DebriefPoint(positionFix, 6);
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
			currentTime = new TemporalImpl(newTime);
			refreshList();
		}
	}

	/**
	 * @param newTime
	 * @return
	 */
	private OMGraphicList calculateTimePoints() {
		tracks = getTracks();
		if (tracks == null || tracks.size() <= 0) {
			return null;
		}
		for (Track track:tracks.values()) {
			calculateTimePoints(track);
		}
		OMGraphicList omg = new OMGraphicList();
		if (!snailMode) {
			for (Track track : tracks.values()) {
				TrackInfo info = trackInfos.get(track);
				if (info != null) {
					if (info.getLineList() != null) {
						omg.addAll(info.getLineList());
					}
					if (info.getCurrentPoint() != null) {
						omg.add(info.getCurrentPoint());
					}
				}
			}
		} else {
			for (Track track : tracks.values()) {
				TrackInfo info = trackInfos.get(track);
				if (info != null) {
					List<OMLine> snailLines = createSnailList(track);
					omg.addAll(snailLines);
					if (info.getCurrentPoint() != null) {
						omg.add(info.getCurrentPoint());
					}
				}
			}
		}
		return omg;
	}

	/**
	 * @param info
	 * @return
	 */
	private List<OMLine> createSnailList(Track track) {
		List<OMLine> lines = new ArrayList<OMLine>();
		if (track == null) {
			return lines;
		}
		TrackInfo info = trackInfos.get(track);
		if (info == null) {
			return lines;
		}
		OMPoint currentPoint = info.getCurrentPoint();
		PositionFix fix = info.getStartFix() == null ? info.getEndFix() : info.getStartFix();
		if (fix != null && fix.getTemporal() != null) {
			List<PositionFix> positionFixes = track.getPositionFixes();
			if (positionFixes == null) {
				return lines;
			}
			int index = positionFixes.indexOf(fix);
			long time = currentTime.getTime() - SNAIL_TIME;
			long fixTime = fix.getTemporal().getTime();
			double lat = currentPoint.getLat();
			double lon = currentPoint.getLon();
			int alpha = 255;
			int alphaStep = ALPHA_STEP;
			while (fixTime > time) {
				OMLine line = new OMLine(lat,
						lon, fix.getSpatial().getLatitude(),
						fix.getSpatial().getLongitude(),
						OMGraphic.LINETYPE_GREATCIRCLE);
				String symbology = fix.getSymbology();
				Color color = ReadingUtility.getSymbologyColor(symbology);
				if (alpha != 255) {
					color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
				}
				line.setLinePaint(color);
				line.setStroke(new BasicStroke(2));
				lines.add(line);
				index--;
				if (index < 0) {
					return lines;
				}
				lat = fix.getSpatial().getLatitude();
				lon = fix.getSpatial().getLongitude();
				fix = positionFixes.get(index);
				if (fix == null || fix.getTemporal() == null) {
					return lines;
				}
				fixTime = fix.getTemporal().getTime();
				if (alpha > 30) {
					alpha-=alphaStep;
				}
			}
		}
		return lines;
	}

	/**
	 * @param track
	 * @param omg 
	 */
	private void calculateTimePoints(Track track) {
		if (track != null && track.getPositionFixes().size() > 0) {
			PositionFix startFix = null;
			PositionFix endFix = null;
			OMPoint currentPoint =null;
			for (PositionFix positionFix:track.getPositionFixes()) {
				Temporal fixTemporal = positionFix.getTemporal();
				if (fixTemporal == null) {
					continue;
				}
				if (currentTime.equals(fixTemporal)) {
					currentPoint = getPoint(positionFix);
					startFix = positionFix;
					break;
				}
				if (fixTemporal.compareTo(currentTime) < 0) {
					startFix = positionFix;
				} else {
					break;
				}
			}
			if (currentPoint == null) {
				int index = track.getPositionFixes().indexOf(startFix);
				if (index >= 0 && track.getPositionFixes().size() >= index+1) {
					endFix = track.getPositionFixes().get(index + 1);
					Temporal start = startFix.getTemporal();
					Temporal end = endFix.getTemporal();
					if (start != null && end != null) {
						long time = currentTime.getTime() - start.getTime();
						long timeRange = end.getTime() - start.getTime();
						if (timeRange > 0) {
							double[] points = new double[4];
							points[0] = endFix.getSpatial().getLatitude();
							points[1] = endFix.getSpatial().getLongitude();
							points[2] = startFix.getSpatial().getLatitude();
							points[3] = startFix.getSpatial().getLongitude();
							ProjMath.arrayDegToRad(points);
							double distanceRange = GreatCircle.sphericalDistance(points[0], points[1],
											points[2], points[3]);
							double distance = time * distanceRange / timeRange;
							//double angle = GreatCircle.sphericalAzimuth(points[0], points[1],
							//				points[2], points[3]);
							LatLonPoint llp = GreatCircle.pointAtDistanceBetweenPoints(points[0], points[1],
											points[2], points[3], distance, -1);
							if (llp != null) {
								currentPoint = getPoint(llp, startFix.getName());
								//omg.add(currentPoint);
							}
						}
					}
				}
			}
			
			TrackInfo trackInfo = trackInfos.get(track);
			if (trackInfo == null) {
				logger.warning("trackInfo is null for track" + track.getName());
			} else {
				trackInfo.setCurrentPoint(currentPoint);
				trackInfo.setStartFix(startFix);
				trackInfo.setEndFix(endFix);
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

	/**
	 * @param snailMode
	 */
	public void setSnailMode(boolean snailMode) {
		this.snailMode = snailMode;
		refreshList();
	}

	private void refreshList() {
		OMGraphicList omg = calculateTimePoints(); 
		if (omg != null) {
			setList(omg);
			doPrepare();
		}
	}

}