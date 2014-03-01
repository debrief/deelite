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
package org.mwc.debrief.lite.datastores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.mwc.debrief.lite.model.AnnotationLayer;
import org.mwc.debrief.lite.model.Narrative;
import org.mwc.debrief.lite.model.NarrativeEntry;
import org.mwc.debrief.lite.model.PeriodText;
import org.mwc.debrief.lite.model.Track;
import org.mwc.debrief.lite.model.impl.NarrativeImpl;
import org.mwc.debrief.lite.model.impl.TrackImpl;

/**
 * @author snpe
 *
 */
public abstract class AbstractDataStore implements DataStore {

	protected Properties properties;
	protected boolean initialized = false;
	protected boolean valid = true;
	protected Map<String,Narrative> narratives = new HashMap<String,Narrative>();
	protected Map<String,Track> tracks = new HashMap<String,Track>();
	protected List<PeriodText> periodTexts = new LinkedList<PeriodText>();
	protected List<Exception> exceptions = new ArrayList<Exception>();
	protected List<NarrativeEntry> narrativeEntries = new LinkedList<NarrativeEntry>();
	protected Map<String,AnnotationLayer> annotationLayers = new HashMap<String,AnnotationLayer>();
	/**
	 * the format we use to parse text
	 */
	protected static final java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(
				"yyMMdd HHmmss.SSS");
	@Override
	public Map<String, Narrative> getNarratives() {
		init();
		return narratives;
	}
	/**
	 * @param name
	 * @return
	 */
	public Track getTrack(String name) {
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
	public Narrative getNarrative(String name) {
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
	@Override
	public Map<String, Track> getTracks() {
		init();
		return tracks;
	}
	@Override
	public Map<String,AnnotationLayer> getAnnotationLayers() {
		init();
		return annotationLayers ;
	}
	@Override
	public List<PeriodText> getPeriodTexts() {
		init();
		return periodTexts;
	}
	@Override
	public List<NarrativeEntry> getNarrativeEntries() {
		init();
		return narrativeEntries;
	}

	public abstract void init();
}
