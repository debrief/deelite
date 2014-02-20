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
import java.util.List;

import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;

import org.mwc.debrief.lite.AbstractMain;
import org.mwc.debrief.lite.DebriefMain;
import org.mwc.debrief.lite.datastore.replay.ReplayDataStore;
import org.mwc.debrief.lite.layers.TrackLayer;
import org.mwc.debrief.lite.model.NarrativeEntry;
import org.mwc.debrief.lite.utils.Utils;
import org.mwc.debrief.lite.views.NarrativeTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.gui.OverlayMapPanel;

/**
 * 
 * @author snpe
 *
 */
public class OpenAction extends AbstractDebriefAction {

	private static final long serialVersionUID = 1L;
	static final Logger logger = LoggerFactory.getLogger(OpenAction.class);
	private OverlayMapPanel map;
	

	public OpenAction() {
		super("Open...", "Open plot file...", "new_con.gif");
		KeyStroke ctrlXKeyStroke = KeyStroke.getKeyStroke("control O");
	    putValue(ACCELERATOR_KEY, ctrlXKeyStroke);
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

	/**
	 * @param map
	 */
	public void setMap(OverlayMapPanel map) {
		this.map = map;
	}
	
}
