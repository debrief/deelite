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
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import com.bbn.openmap.InformationDelegator;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.PropertyHandler;
import com.bbn.openmap.event.DistanceMouseMode;
import com.bbn.openmap.event.OMMouseMode;
import com.bbn.openmap.gui.LayersPanel;
import com.bbn.openmap.gui.OverlayMapPanel;
import com.bbn.openmap.layer.DemoLayer;
import com.bbn.openmap.layer.editor.EditorLayer;
import com.bbn.openmap.layer.learn.BasicLayer;
import com.bbn.openmap.layer.learn.InteractionLayer;
import com.bbn.openmap.layer.shape.ShapeLayer;
import com.bbn.openmap.tools.drawing.OMDrawingTool;

public class Main extends AbstractMain {

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
					Main frame = new Main();
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
	public Main() {
		super();
	}

	protected void createMap() {
		map = new OverlayMapPanel(new PropertyHandler(new Properties()), true);
        map.create();

        mapBean = map.getMapBean(); 
        OMDrawingTool dt = new OMDrawingTool();
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
        
        // OpenMap Political Boundaries layer 
        ShapeLayer shapeLayer = new ShapeLayer("data/shape/cntry02/cntry02.shp");
        shapeLayer.setAddAsBackground(true);
        shapeLayer.setName("Political Boundaries");
        shapeLayer.setVisible(false);
        map.addMapComponent(shapeLayer);
        
        // OpenMap Demo Layer
        DemoLayer demoLayer = new DemoLayer();
        demoLayer.setName("Demo");
        demoLayer.setDrawingTool(dt);
        map.addMapComponent(demoLayer);
        
        // Basic Layer
        BasicLayer basicLayer = new BasicLayer();
        map.addMapComponent(basicLayer);
        
        InteractionLayer interactionLayer = new InteractionLayer();
        map.addMapComponent(interactionLayer);
        
        
//        distlayer.class=com.bbn.openmap.layer.editor.EditorLayer
//        distlayer.prettyName=Distance Layer
//        distlayer.showAttributes=false
//        distlayer.editor=com.bbn.openmap.layer.editor.DrawingEditorTool
//        distlayer.loaders=distance
//        distlayer.distance.class=com.bbn.openmap.tools.drawing.OMDistanceLoader
//        distlayer.distance.attributesClass=com.bbn.openmap.omGraphics.DrawingAttributes
//        distlayer.distance.lineColor=FFAA0000
//        distlayer.distance.mattingColor=66333333
//        distlayer.distance.matted=true
//        		
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

}
