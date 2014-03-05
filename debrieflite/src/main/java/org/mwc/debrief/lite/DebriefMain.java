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
import java.io.File;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.mwc.debrief.lite.dnd.PlotDropTarget;
import org.mwc.debrief.lite.layers.CoastlineLayer;
import org.mwc.debrief.lite.layers.RangeBearingLayer;
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
	public static DebriefMain mainFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				try {
					MapBean.suppressCopyright = true;
					setBaseLookAndFeel();
					mainFrame = new DebriefMain();
					mainFrame.pack();
					mainFrame.setLocationRelativeTo(null);
					
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
		// mapBean.setBackgroundColor(new Color(0x99b3cc));
		//mapBean.setBackground(new Color(0xF5C66C));
		mapBean.setBackground(new Color(0xD4F1F5));
		
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

		informationDelegator.setBorder(BorderFactory.createBevelBorder(
				BevelBorder.LOWERED, Color.GRAY, Color.DARK_GRAY));
		add(informationDelegator, BorderLayout.SOUTH);
		informationDelegator.setPreferredSize(new Dimension(getWidth(), 32));
		informationDelegator.setLayout(new BoxLayout(informationDelegator,
				BoxLayout.X_AXIS));

		informationDelegator.setMap(map.getMapBean());
		informationDelegator.findAndInit(map.getMapBean());

		informationDelegator.setShowCoordsInfoLine(true);
		informationDelegator.setShowInfoLine(true);
		informationDelegator.setShowLights(true);
		informationDelegator.setLightTriggers(true);

		RangeBearingLayer rangeBearingLayer = new RangeBearingLayer();
		Properties props = new Properties();
		props.put("rangeBearing.prettyName", "Range Bearing");
		props.put("rangeBearing.showAttributes", "false");
		props.put("rangeBearing.editor",
				"com.bbn.openmap.layer.editor.DrawingEditorTool");
		props.put("rangeBearing.loaders", "rangeBearing");
		props.put("rangeBearing.rangeBearing.class",
				"org.mwc.debrief.lite.drawing.DebriefDistanceLoader");
		props.put("rangeBearing.rangeBearing.attributesClass",
				"com.bbn.openmap.omGraphics.DrawingAttributes");
		props.put("rangeBearing.rangeBearing.lineColor", "FFAA0000");
		props.put("rangeBearing.rangeBearing.mattingColor", "66333333");
		props.put("rangeBearing.rangeBearing.matted", "true");
		rangeBearingLayer.setProperties("rangeBearing", props);
		rangeBearingLayer.findAndInit(informationDelegator);
		map.addMapComponent(rangeBearingLayer);

		// coastline layer
		File coastlineHome = new File("coastline");
		File shpFile = new File(coastlineHome, "ne_10m_land.shp");
		if (shpFile.exists()) {
			CoastlineLayer coastlineLayer = new CoastlineLayer(shpFile.getAbsolutePath());

			// RGB(212, 241, 245) = D4F1F5 - ocean filled
			// RGB(102, 122,123) = 667A7B - outline for the ocean
			// RGB(245, 198, 108) = F5C66C - bckground
			props = new Properties();
			props.put("coastline.shapeFile", shpFile.getAbsolutePath());
			//props.put("coastline.lineColor", "ff000000");
			props.put("coastline.lineColor", "667A7B");
			//props.put("coastline.fillColor", "DAD5CB");
			//props.put("coastline.fillColor", "D4F1F5");
			props.put("coastline.fillColor", "F5C66C");
			props.put("coastline.rp.class", "com.bbn.openmap.layer.policy.PanningImageRenderPolicy");
			props.put("coastline.background", "true");
			
			//props.put("coastline.rules", "1 2 3 4 5 6 7 8");
			props.put("coastline.rules", "1");
			
			props.put("coastline.1.key", "COLOR_MAP");
			props.put("coastline.1.op", "equals");
			props.put("coastline.1.val", "1");
			props.put("coastline.1.fillColor", "F0F0F0");
			props.put("coastline.1.render", "true");
			
//			props.put("coastline.2.key", "COLOR_MAP");
//			props.put("coastline.2.op", "equals");
//			props.put("coastline.2.val", "2");
//			props.put("coastline.2.fillColor", "e9e9e9");
//			props.put("coastline.2.render", "true");
//
//			props.put("coastline.3.key", "COLOR_MAP");
//			props.put("coastline.3.op", "equals");
//			props.put("coastline.3.val", "3");
//			props.put("coastline.3.fillColor", "e0e0e0");
//			props.put("coastline.3.render", "true");
//
//			props.put("coastline.4.key", "COLOR_MAP");
//			props.put("coastline.4.op", "equals");
//			props.put("coastline.4.val", "4");
//			props.put("coastline.4.fillColor", "d9d9d9");
//			props.put("coastline.4.render", "true");
//
//			props.put("coastline.5.key", "COLOR_MAP");
//			props.put("coastline.5.op", "equals");
//			props.put("coastline.5.val", "5");
//			props.put("coastline.5.fillColor", "d0d0d0");
//			props.put("coastline.5.render", "true");
//
//			props.put("coastline.6.key", "COLOR_MAP");
//			props.put("coastline.6.op", "equals");
//			props.put("coastline.6.val", "6");
//			props.put("coastline.6.fillColor", "c9c9c9");
//			props.put("coastline.6.render", "true");
//
//			props.put("coastline.7.key", "COLOR_MAP");
//			props.put("coastline.7.op", "equals");
//			props.put("coastline.7.val", "7");
//			props.put("coastline.7.fillColor", "c0c0c0");
//			props.put("coastline.7.render", "true");
//
//			props.put("coastline.8.key", "COLOR_MAP");
//			props.put("coastline.8.op", "equals");
//			props.put("coastline.8.val", "8");
//			props.put("coastline.8.fillColor", "b9b9b9");
//			props.put("coastline.8.render", "true");

			coastlineLayer.setProperties("coastline", props);

			map.addMapComponent(coastlineLayer);
		}
		zoomSupport.add(mapBean);
		plotPanel.add(map);

		map.setDropTarget(new PlotDropTarget(map));

	}

}
