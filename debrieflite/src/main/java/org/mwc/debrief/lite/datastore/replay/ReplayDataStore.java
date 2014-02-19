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
package org.mwc.debrief.lite.datastore.replay;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.mwc.debrief.lite.datastores.DataStore;
import org.mwc.debrief.lite.model.AnnotationLayer;
import org.mwc.debrief.lite.model.Bearing;
import org.mwc.debrief.lite.model.Narrative;
import org.mwc.debrief.lite.model.NarrativeEntry;
import org.mwc.debrief.lite.model.PeriodText;
import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Track;
import org.mwc.debrief.lite.model.impl.NarrativeImpl;
import org.mwc.debrief.lite.model.impl.TrackImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snpe
 * 
 */
public class ReplayDataStore implements DataStore {

	private static final String COMMENT_PREFIX = ";;";
	public static final String[] SUFFIXES = new String[]{ ".rep", ".dsf", ".dtf" };
			
	static final Logger logger = LoggerFactory.getLogger(ReplayDataStore.class);
	
	private Properties properties;
	private boolean initialized = false;
	private boolean valid = true;
	private Map<String,Narrative> narratives = new HashMap<String,Narrative>();
	private Map<String,Track> tracks = new HashMap<String,Track>();
	private List<PeriodText> periodTexts = new LinkedList<PeriodText>();
	private List<Exception> exceptions = new ArrayList<Exception>();

	private Map<String,AnnotationLayer> annotationLayers = new HashMap<String,AnnotationLayer>();

	/**
	 * the format we use to parse text
	 */
	private static final java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(
			"yyMMdd HHmmss.SSS");
	private static List<LineImporter> _theImporters = new ArrayList<LineImporter>();

	static private Map<String, Color> colors = new HashMap<String, Color>(); 

	static {
		colors.put("@", Color.white);
		colors.put("A", Color.blue);
		colors.put("B", Color.green);
		colors.put("C", Color.red);
		colors.put("D", Color.yellow);
		colors.put("E", new Color(169, 1, 132));
		colors.put("F", Color.orange);
		colors.put("G", new Color(188, 93, 6));
		colors.put("H", Color.cyan);
		colors.put("I", new Color(100, 240, 100));
		colors.put("J", new Color(230, 200, 20));
		colors.put("K", Color.pink);
		
//		_theImporters.add(new ImportCircle());
//		_theImporters.add(new ImportRectangle());
//		_theImporters.add(new ImportLine());
//		_theImporters.add(new ImportVector());
//		_theImporters.add(new ImportEllipse());
//		_theImporters.add(new ImportTimeText());
//		_theImporters.add(new ImportLabel());
//		_theImporters.add(new ImportWheel());
//		_theImporters.add(new ImportBearing());
//		_theImporters.add(new ImportSensor());
//		_theImporters.add(new ImportSensor2());
//		_theImporters.add(new ImportSensor3());
//		_theImporters.add(new ImportTMA_Pos());
//		_theImporters.add(new ImportTMA_RngBrg());
//		_theImporters.add(new ImportPolygon());
//		_theImporters.add(new ImportPolyline());

		_theImporters.add(new ImportNarrative());
		_theImporters.add(new ImportNarrative2());
		_theImporters.add(new ImportPeriodText());

		_theImporters.add(new ImportFix());
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * @param properties
	 */
	public ReplayDataStore(Properties properties) {
		this.properties = properties;
		if (properties == null) {
			valid = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.debrief.lite.datastores.DataStore#getNarattives()
	 */
	@Override
	public Map<String, Narrative> getNarratives() {
		init();
		return narratives;
	}

	/**
	 * read replay file
	 */
	private synchronized void init() {
		if (!initialized) {
			if (valid) {
				String fileName = properties.getProperty(DataStore.FILENAME);
				if (fileName == null || fileName.isEmpty()) {
					valid = false;
					logger.info("Invalid file: {}", fileName);
					return;
				}
				// check type ???
				
				File file = new File(fileName);
				InputStream is = null;
				try {
					if (file.isFile()) {
						is = new FileInputStream(file);
					} else {
						is = getClass().getResourceAsStream(fileName);
					}
					if (is == null) {
						valid = false;
						logger.info("Invalid file: {}", fileName);
						exceptions.add(new FileNotFoundException("Can't open the " + fileName));
						return;
					}
					readFile(is);
				} catch (FileNotFoundException e) {
					valid = false;
					exceptions.add(e);
					logger.info("File: {}\n{}", fileName, e);
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
			initialized = true;
		}
	}

	/**
	 * @param file
	 */
	private void readFile(InputStream is) {
		try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(is))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line.trim();
				readLine(line);
			}
		} catch (Exception e) {
			logger.info("Reading error:", e);
			valid = false;
			exceptions.add(e);
		}
	}

	/**
	 * @param line
	 */
	private void readLine(String line) {
		if (line == null || line.isEmpty() || line.startsWith(COMMENT_PREFIX)) {
			return;
		}
		for (LineImporter importer:_theImporters) {
			if (importer.canImport(line)) {
				Object object = importer.readLine(line);
				if (importer.getException() != null) {
					exceptions.add(importer.getException());
				} else {
					if (object instanceof PositionFix) {
						PositionFix positionFix = (PositionFix) object;
						String name = positionFix.getName();
						Track track = getTrack(name);
						track.getPositionFixes().add(positionFix);
					} else if (object instanceof Bearing) {
						Bearing bearing = (Bearing) object;
						String name = bearing.getName();
						Track track = getTrack(name);
						track.getBearings().add(bearing);
					} else if (object instanceof NarrativeEntry) {
						NarrativeEntry entry = (NarrativeEntry) object;
						String name = entry.getName();
						Narrative narrative = getNarrative(name);
						narrative.getEntries().add(entry);
					} else if (object instanceof PeriodText) {
						periodTexts.add((PeriodText) object);
					}
				}
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private Track getTrack(String name) {
		Track track = tracks.get(name);
		if (track == null) {
			track = new TrackImpl(name);
			tracks.put(name, track);
		}
		return track;
	}
	
	/**
	 * @param name
	 * @return
	 */
	private Narrative getNarrative(String name) {
		Narrative narrative = narratives.get(name);
		if (narrative == null) {
			narrative = new NarrativeImpl(name);
			narratives.put(name, narrative);
		}
		return narrative;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @return the exception
	 */
	@Override
	public List<Exception> getException() {
		return exceptions;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.datastores.DataStore#getTracks()
	 */
	@Override
	public Map<String, Track> getTracks() {
		init();
		return tracks;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.datastores.DataStore#getAnnotationLayers()
	 */
	@Override
	public Map<String,AnnotationLayer> getAnnotationLayers() {
		return annotationLayers ;
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.datastores.DataStore#getPeriodTexts()
	 */
	@Override
	public List<PeriodText> getPeriodTexts() {
		return periodTexts;
	}

}
