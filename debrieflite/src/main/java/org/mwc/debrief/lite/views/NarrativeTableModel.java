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
package org.mwc.debrief.lite.views;

import java.text.DateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.swing.table.AbstractTableModel;

import org.mwc.debrief.lite.model.NarrativeEntry;

/**
 * @author snpe
 *
 */
public class NarrativeTableModel extends AbstractTableModel {

	public static final int COLUMN_COUNT = 4; // Time, Source, Type , Entry
	private static final long serialVersionUID = 1L;
	private static final DateFormat df = new java.text.SimpleDateFormat("yy/MM/dd HH:mm");
   
	private List<NarrativeEntry> narrativeEntries;
	
	/**
	 * @param narrativeEntries
	 */
	public NarrativeTableModel(List<NarrativeEntry> narrativeEntries) {
		super();
		this.narrativeEntries = narrativeEntries;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return narrativeEntries == null ? 0 : narrativeEntries.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return COLUMN_COUNT;  
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (narrativeEntries == null) {
			return null;
		}
		NarrativeEntry entry = narrativeEntries.get(rowIndex);
		switch (columnIndex) {
		case 0:
			// check the formats are in the correct time zone
		    df.setTimeZone(TimeZone.getTimeZone("GMT"));
			return df.format(entry.getDate().getDate());
		case 1:
			return entry.getName();
		case 2:
			return entry.getType();
		case 3:
			return entry.getEntry();

		default:
			break;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Time";
		case 1:
			return "Source";
		case 2:
			return "Type";
		case 3:
			return "Entry";

		default:
			break;
		}
		return super.getColumnName(column);
	}

}
