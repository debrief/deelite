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
package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.mwc.debrief.lite.datastores.DataStore;
import org.mwc.debrief.lite.datastores.DataStoreFactory;
import org.mwc.debrief.lite.layers.RangeBearingLayer;
import org.mwc.debrief.lite.layers.TrackLayer;
import org.mwc.debrief.lite.model.Track;
import org.mwc.debrief.lite.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.InformationDelegator;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.PropertyHandler;
import com.bbn.openmap.event.OMMouseMode;
import com.bbn.openmap.gui.LayersPanel;
import com.bbn.openmap.gui.OverlayMapPanel;

public class DebriefMain extends AbstractMain {

	private static final long serialVersionUID = 1L;
	static final Logger logger = LoggerFactory.getLogger(DebriefMain.class);
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					MapBean.suppressCopyright = true;
					setBaseLookAndFeel();
					DebriefMain frame = new DebriefMain();
					frame.pack();
					frame.setLocationRelativeTo(null);
					
				} catch (Exception e) {
					logger.error("DebriefMain", e);
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public DebriefMain() {
		super();
	}

	protected void createMap() {
		map = new OverlayMapPanel(new PropertyHandler(new Properties()), true);
        map.create();

        mapBean = map.getMapBean(); 
        //mapBean.setBackgroundColor(new Color(0x99b3cc));
        //mapBean.setBackground(Color.black);
        LayerHandler layerHandler = new LayerHandler();
        map.addMapComponent(layerHandler);
        
        layersPanel.add(new LayersPanel(layerHandler));
        
        InformationDelegator informationDelegator = new InformationDelegator();
        OMMouseMode mouseMode = new OMMouseMode();
        mouseMode.setInfoDelegator(informationDelegator);
        
        MouseDelegator mouseDelegator = new MouseDelegator();
        map.addMapComponent(mouseDelegator);
        map.addMapComponent(mouseMode);
        mouseDelegator.setActiveMouseMode(mouseMode);
        
        informationDelegator.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.GRAY, Color.DARK_GRAY));
		add(informationDelegator, BorderLayout.SOUTH);
		informationDelegator.setPreferredSize(new Dimension(getWidth(), 32));
		informationDelegator.setLayout(new BoxLayout(informationDelegator, BoxLayout.X_AXIS));
		
		informationDelegator.setMap(map.getMapBean());
        informationDelegator.findAndInit(map.getMapBean());
		
        informationDelegator.setShowCoordsInfoLine(true);
        informationDelegator.setShowInfoLine(true);
        informationDelegator.setShowLights(true);
        informationDelegator.setLightTriggers(true);
        
        RangeBearingLayer rangeBearingLayer = new RangeBearingLayer();
        Properties props = new Properties();
        props.put("rangeBearing.prettyName", "Range Bearing");
        props.put("rangeBearing.showAttributes","false");
        props.put("rangeBearing.editor","com.bbn.openmap.layer.editor.DrawingEditorTool");
        props.put("rangeBearing.loaders","rangeBearing");
        props.put("rangeBearing.rangeBearing.class","org.mwc.debrief.lite.drawing.DebriefDistanceLoader");
        props.put("rangeBearing.rangeBearing.attributesClass","com.bbn.openmap.omGraphics.DrawingAttributes");
        props.put("rangeBearing.rangeBearing.lineColor","FFAA0000");
        props.put("rangeBearing.rangeBearing.mattingColor","66333333");
        props.put("rangeBearing.rangeBearing.matted","true");
        rangeBearingLayer.setProperties("rangeBearing", props);
        rangeBearingLayer.findAndInit(informationDelegator);
        map.addMapComponent(rangeBearingLayer);
        
		TrackLayer trackLayer = createTrackLayer("/data/boat_file.rep", map);
        map.addMapComponent(trackLayer);          		
        zoomSupport.add(mapBean);
        plotPanel.add(map);
	}

	/**
	 * @param map 
	 * @return track layer
	 */
	private TrackLayer createTrackLayer(String fileName, OverlayMapPanel map) {
		 
        TrackLayer trackLayer = new TrackLayer();
        
        Properties props = new Properties();
        props.put(DataStore.TYPE, DataStore.REPLAY_TYPE);
        props.put(DataStore.FILENAME, fileName);
		DataStore dataStore = DataStoreFactory.getDataStore(props);
        Map<String, Track> tracks = dataStore.getTracks();
        trackLayer.setTracks(tracks);
        double scale = Utils.configureTracks(map, trackLayer);
        fitToWindow.setScale(scale);
        fitToWindow.setLatitude(mapBean.getCenter().getY());
        fitToWindow.setLongitude(mapBean.getCenter().getX());
        return trackLayer;
	}

}
