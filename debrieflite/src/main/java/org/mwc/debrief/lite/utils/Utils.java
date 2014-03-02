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
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;

import org.mwc.debrief.lite.AbstractMain;
import org.mwc.debrief.lite.DebriefMain;
import org.mwc.debrief.lite.actions.AbstractDebriefAction;
import org.mwc.debrief.lite.datastores.DataStore;
import org.mwc.debrief.lite.datastores.DataStoreFactory;
import org.mwc.debrief.lite.layers.TrackLayer;
import org.mwc.debrief.lite.model.NarrativeEntry;
import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.model.Track;
import org.mwc.debrief.lite.time.TimeEvent;
import org.mwc.debrief.lite.views.NarrativeTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.Environment;
import com.bbn.openmap.gui.EmbeddedNavPanel;
import com.bbn.openmap.gui.OverlayMapPanel;

/**
 * @author snpe
 *
 */
public class Utils {

	static final Logger logger = LoggerFactory.getLogger(AbstractDebriefAction.class);
	private static DataStore dataStore;
	private static final DateFormat df = new java.text.SimpleDateFormat("yy/MM/dd HH:mm");

	static {
		// check the formats are in the correct time zone
	    df.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	/**
	 * @param map
	 * @param trackLayer
	 * 
	 */
	public static double configureTracks(OverlayMapPanel map,
			Map<String, Track> tracks) {
		if ( map == null) {
			return Environment.getDouble(Environment.Scale);
		}
		if (tracks == null || tracks.size() <= 0) {
        	return map.getMapBean().getScale();
        }
		double minLat = 180f;
		double maxLat = -180f;
		double minLon = 180f;
		double maxLon = -180f;
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
			//int height = (int) screenSize.getHeight();
			float scale = (float) (length*width*1000f)/2.1f;
			
//			Projection p = new Mercator(new LatLonPoint.Double(lat, lon),
//					scale, width, height);
//			MapBean mapBean = map.getMapBean();
//			mapBean.setProjection(p);
			
			map.getMapBean().setScale((float) scale);
			DebriefMain.getCenterSupport().fireCenter(lat, lon);
			
			Object object = map.getMapHandler().get(EmbeddedNavPanel.class);
			if (object instanceof EmbeddedNavPanel) {
				EmbeddedNavPanel navPanel = (EmbeddedNavPanel) object;
				navPanel.setRecenterPoint(map.getMapBean().getCenter());
			}
			//OMPoint center = new OMPoint(lat, lon, 3);
			//trackLayer.setCenter(center);
			
			Environment.set(Environment.Scale, Float.toString(scale));
			return scale;
		}
		return Environment.getDouble(Environment.Scale);
	}

	/**
	 * @param map
	 */
	public static void removeTrackLayer(OverlayMapPanel map) {
		@SuppressWarnings("unchecked")
		Collection<TrackLayer> trackLayers = (Collection<TrackLayer>) map.getMapComponentsByType(TrackLayer.class);
		for (TrackLayer trackLayer:trackLayers) {
			if (trackLayer != null) {
				DebriefMain.getTimeController().removeTimeListener(trackLayer);
				map.removeMapComponent(trackLayer);
			}
		}
	}

	public static List<TrackLayer> createTrackLayer(String fileName, OverlayMapPanel map) {
		 
        Properties props = new Properties();
        if (fileName.endsWith(".gpx")) {
        	props.put(DataStore.TYPE, DataStore.GPX_TYPE);
        } else {
        	// FIXME
        	props.put(DataStore.TYPE, DataStore.REPLAY_TYPE);
        }
        props.put(DataStore.FILENAME, fileName);
		dataStore = DataStoreFactory.getDataStore(props);
		
		if (dataStore == null) {
			JOptionPane.showMessageDialog(DebriefMain.mainFrame, "Unknown file type for the file: " + fileName);
			return null;
		}
		// initialize
		Map<String, Track> tracks = dataStore.getTracks();
        if (dataStore.getException().size() > 0) {
        	String message = "Can't parse the file: " + fileName + ".";
        	if (dataStore.getException().size() > 0) {
        		message = message + "\nException: " + dataStore.getException().get(0).getLocalizedMessage();
        	}
        	JOptionPane.showMessageDialog(DebriefMain.mainFrame, message);
        	return null;
        }
        List<TrackLayer> trackLayers = new ArrayList<TrackLayer>();
        for (Track track:tracks.values()) {
        	TrackLayer trackLayer = new TrackLayer();
        	Map<String, Track> maps = new HashMap<String, Track>();
        	maps.put(track.getName(), track);
        	trackLayer.setTracks(maps);
        	trackLayers.add(trackLayer);
        }
        double scale = Utils.configureTracks(map, tracks);
        DebriefMain.setActionEnabled(true);
        DebriefMain.fitToWindow.setScale(scale);
        DebriefMain.fitToWindow.setLatitude(map.getMapBean().getCenter().getY());
        DebriefMain.fitToWindow.setLongitude(map.getMapBean().getCenter().getX());
        return trackLayers;
	}

	public static DataStore getCurrentDataStore() {
		return dataStore;
	}
	
	public static ImageIcon getIcon(String imageName) {
	    String location = "/icons/" + imageName;
	    URL imageURL = Utils.class.getResource(location);
	
	    if (imageURL == null) {
	        logger.warn("Can't find icon file: {}" + location);
	        return null;
	    } else {
	        return new ImageIcon(imageURL);
	    }
	}
	
	public static DateFormat getDefaultDateFormat() {
		return df;
	}
	
	public static void currentTimeChanged(Temporal currentTime, Object object) {
		if (currentTime != null) {
			DebriefMain.getTimeController().notifyListeners(new TimeEvent(currentTime.getTime(), object));
		} else {
			DebriefMain.getTimeController().notifyListeners(new TimeEvent(0l, object));
		}
	}
	
	/**
	 * @param fileName
	 */
	public static void addTrackLayers(String fileName) {
		OverlayMapPanel map = DebriefMain.getMap();
		DebriefMain.setTimeViewEnabled(false);
		List<TrackLayer> trackLayers = Utils.createTrackLayer(fileName, map);
		if (trackLayers != null) {
			Utils.removeTrackLayer(map);
			for (TrackLayer trackLayer:trackLayers) {
				map.addMapComponent(trackLayer);
			}
			if (trackLayers.size() > 0) {
				DebriefMain.setTimeViewEnabled(true);
			}
		}
		List<NarrativeEntry> narrativeEntries = null;
		if (Utils.getCurrentDataStore() != null) {
			narrativeEntries = Utils.getCurrentDataStore().getNarrativeEntries();
		}
		NarrativeTableModel tableModel = new NarrativeTableModel(narrativeEntries);
		AbstractMain.narrativeTable.setModel(tableModel);
		for (int i = 0; i < NarrativeTableModel.COLUMN_COUNT; i++) {
        	TableColumn column = AbstractMain.narrativeTable.getColumnModel().getColumn(i);
        	switch (i) {
			case 0:
				column.setMinWidth(100);
		        column.setPreferredWidth(120);	
				break;
			case 1:
			case 2:
		        column.setMinWidth(80);
		        column.setPreferredWidth(100);	
				break;
			case 3:
		        column.setMinWidth(100);
		        column.setPreferredWidth(200);
				break;
			default:
				break;
			}
			
		}
	}
}
