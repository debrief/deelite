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
import org.mwc.debrief.lite.layers.TrackLayer;
import org.mwc.debrief.lite.model.Track;
import org.mwc.debrief.lite.utils.Utils;

import com.bbn.openmap.InformationDelegator;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.PropertyHandler;
import com.bbn.openmap.event.DistanceMouseMode;
import com.bbn.openmap.event.OMMouseMode;
import com.bbn.openmap.gui.LayersPanel;
import com.bbn.openmap.gui.OverlayMapPanel;
import com.bbn.openmap.layer.editor.EditorLayer;
import com.bbn.openmap.tools.drawing.OMDrawingTool;

public class DebriefMain extends AbstractMain {

	private static final long serialVersionUID = 1L;
	
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
					e.printStackTrace();
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
        // OMDrawingTool dt = 
        		new OMDrawingTool();
        //mapBean.setBackgroundColor(new Color(0x99b3cc));
        LayerHandler layerHandler = new LayerHandler();
        map.addMapComponent(layerHandler);
        
        layersPanel.add(new LayersPanel(layerHandler));
        
        InformationDelegator informationDelegator = new InformationDelegator();
        OMMouseMode mouseMode = new OMMouseMode();
        mouseMode.setInfoDelegator(informationDelegator);
        
        DistanceMouseMode distanceMouseMode = new DistanceMouseMode();
        distanceMouseMode.setInfoDelegator(informationDelegator);
        map.addMapComponent(distanceMouseMode);
        
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
        
		TrackLayer trackLayer = createTrackLayer("/data/boat_file.rep", map);
        
        map.addMapComponent(trackLayer);
          		
        EditorLayer distanceLayer = new EditorLayer();
        Properties props = new Properties();
        props.put("distlayer.prettyName", "Distance Layer");
        props.put("distlayer.showAttributes","false");
        props.put("distlayer.editor","com.bbn.openmap.layer.editor.DrawingEditorTool");
        props.put("distlayer.loaders","distance");
        props.put("distlayer.distance.class","com.bbn.openmap.tools.drawing.OMDistanceLoader");
        props.put("distlayer.distance.attributesClass","com.bbn.openmap.omGraphics.DrawingAttributes");
        props.put("distlayer.distance.lineColor","FFAA0000");
        props.put("distlayer.distance.mattingColor","66333333");
        props.put("distlayer.distance.matted","true");
        distanceLayer.setProperties("distlayer", props);
        map.addMapComponent(distanceLayer);

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
