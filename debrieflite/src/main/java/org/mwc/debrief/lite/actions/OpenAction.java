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

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.mwc.debrief.lite.DebriefMain;
import org.mwc.debrief.lite.datastore.replay.ReplayDataStore;
import org.mwc.debrief.lite.layers.TrackLayer;
import org.mwc.debrief.lite.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.gui.OverlayMapPanel;

/**
 * 
 * @author snpe
 *
 */
public class OpenAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	static final Logger logger = LoggerFactory.getLogger(OpenAction.class);
	private OverlayMapPanel map;
	

	public OpenAction(OverlayMapPanel map) {
		super("Open...");
		putValue(SHORT_DESCRIPTION, "Open plot file...");
		KeyStroke ctrlXKeyStroke = KeyStroke.getKeyStroke("control O");
	    putValue(ACCELERATOR_KEY, ctrlXKeyStroke);
	    this.map = map;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (map == null) {
			return;
		}
		FileDialog dialog = new FileDialog(DebriefMain.mainFrame, "Open plot file", FileDialog.LOAD);
		dialog.setMultipleMode(false);
		dialog.setFilenameFilter(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				for (String suffix:ReplayDataStore.SUFFIXES) {
					if (name.endsWith(suffix)) {
						return true;
					}
				}
				return false;
			}
		});
		dialog.setVisible(true);
		File[] files = dialog.getFiles();
		if (files == null || files.length <= 0) {
			return;
		}
		String fileName = files[0].getAbsolutePath();
		TrackLayer trackLayer = Utils.createTrackLayer(fileName, map);
		if (trackLayer != null) {
			Utils.removeTrackLayer(map);
			map.addMapComponent(trackLayer);
		}
	}
	
}
