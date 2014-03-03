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
package org.mwc.debrief.lite.dnd;

import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import org.mwc.debrief.lite.DebriefMain;
import org.mwc.debrief.lite.layers.TrackLayer;
import org.mwc.debrief.lite.utils.Utils;

import com.bbn.openmap.gui.OverlayMapPanel;

/**
 * @author snpe
 *
 */
public class PlotDropTarget extends DropTarget {
	
	private static final long serialVersionUID = 1L;
	private OverlayMapPanel map;

	/**
	 * @param map
	 * @throws HeadlessException
	 */
	public PlotDropTarget(OverlayMapPanel map) throws HeadlessException {
		super();
		this.map = map;
	}

	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTarget#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	@Override
	public synchronized void drop(DropTargetDropEvent event) {
		try {
            event.acceptDrop(DnDConstants.ACTION_COPY);
            @SuppressWarnings("unchecked")
			List<File> droppedFiles = (List<File>) event.getTransferable().
            		getTransferData(DataFlavor.javaFileListFlavor);
            // we select only first file
            if (droppedFiles != null && droppedFiles.size() > 0) {
            	// FIXME check extensions
            	Collection<?> oldLayers = map.getMapComponentsByType(TrackLayer.class);
        		boolean replace = oldLayers == null || oldLayers.size() <= 0;
        		if (!replace) {
        			int ok = JOptionPane.showConfirmDialog(DebriefMain.mainFrame, 
        					"Do you want to replace the current plot(s)?", "Drop plot", 
        					JOptionPane.YES_NO_OPTION);
        			replace = (ok == JOptionPane.OK_OPTION);
        		}
        		if (replace) {
    				Utils.removeTrackLayer(map);
    			}
        		for (File file:droppedFiles) {
    				Utils.addTrackLayers(file.getAbsolutePath());
    			}
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	

}
