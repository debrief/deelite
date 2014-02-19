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

import java.util.List;
import java.util.Map;

import org.mwc.debrief.lite.model.AnnotationLayer;
import org.mwc.debrief.lite.model.Narrative;
import org.mwc.debrief.lite.model.PeriodText;
import org.mwc.debrief.lite.model.Track;

/**
 * @author snpe
 *
 */
public interface DataStore {
	
	public static String REPLAY_TYPE = "rep";
	public static String TYPE = "type";
	public static String FILENAME = "fileName";
	
	Map<String,Narrative> getNarratives();
	Map<String,Track> getTracks();
	List<PeriodText> getPeriodTexts();
	Map<String,AnnotationLayer> getAnnotationLayers();
	List<Exception> getException();

}
