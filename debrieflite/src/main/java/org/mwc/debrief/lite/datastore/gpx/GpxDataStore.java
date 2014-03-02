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
package org.mwc.debrief.lite.datastore.gpx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.mwc.debrief.lite.datastores.AbstractDataStore;
import org.mwc.debrief.lite.datastores.DataStore;
import org.mwc.debrief.lite.model.PositionFix;
import org.mwc.debrief.lite.model.Spatial;
import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.model.Track;
import org.mwc.debrief.lite.model.impl.PositionFixImpl;
import org.mwc.debrief.lite.model.impl.SpatialImpl;
import org.mwc.debrief.lite.model.impl.TemporalImpl;
import org.mwc.debrief.lite.model.impl.TrackImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author snpe
 * 
 */
public class GpxDataStore extends AbstractDataStore {
		
	static final Logger logger = LoggerFactory.getLogger(GpxDataStore.class);
	static final DatatypeFactory df;
	
	static
	{
		try
		{
			df = DatatypeFactory.newInstance();
		}
		catch (final DatatypeConfigurationException dce)
		{
			throw new IllegalStateException("Exception while obtaining DatatypeFactory instance. Can't marshall/unmarshall GPX documents.", dce);
		}
	}
	/**
	 * @param properties
	 */
	public GpxDataStore(Properties properties) {
		this.properties = properties;
		if (properties == null) {
			valid = false;
		}
	}

	/**
	 * read replay file
	 */
	@Override
	public synchronized void init() {
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
					parseFile(is);
				} catch (Exception e) {
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
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	private void parseFile(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		
		SAXParser parser = factory.newSAXParser();
		
		parser.parse(is, new GPXHandler());
	}
	
	private class GPXHandler extends DefaultHandler {
		
		private StringBuilder builder = new StringBuilder();	
		private Track currentTrack;
		private String lat, lon, ele, time;
		private boolean trkpt;
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			builder.setLength(0);
			if (currentTrack == null) {
				if ("trk".equals(localName)) {
					currentTrack = new TrackImpl();
				}
			} else {
				if (!trkpt) {
					if ("trkpt".equals(localName)) {
						trkpt = true;
						lat = attributes.getValue("", "lat");
						lon = attributes.getValue("", "lon");
					} else {
							
					}
				}
			}
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if ("trk".equals(localName)) {
				tracks.put(currentTrack.getName(), currentTrack);
				List<PositionFix> fixes = new ArrayList<PositionFix>();
				fixes.addAll(currentTrack.getPositionFixes());
				Collections.sort(fixes, new Comparator<PositionFix>() {

					/* (non-Javadoc)
					 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
					 */
					@Override
					public int compare(PositionFix o1, PositionFix o2) {
						if (o1 == o2) {
							return 0;
						}
						if (o1 == null || o1.getTemporal() == null) {
							return 1;
						}
						if (o2 == null || o2.getTemporal() == null) {
							return -1;
						}
						return o1.getTemporal().compareTo(o2.getTemporal());
					}
					
				});
				currentTrack.getPositionFixes().clear();
				for (PositionFix fix:fixes) {
					currentTrack.getPositionFixes().add(fix);
				}
				currentTrack = null;
			} else {
				if ("trkpt".equals(localName)) {
					trkpt = false;
					double latitude = 0;
					double longitude = 0;
					double depth = 0;
					boolean valid = true;
					try {
						latitude = new Double(lat);
					} catch (NumberFormatException e) {
						logger.warn("Invalid latitude:{}", lat);
						valid = false;
					}
					try {
						longitude = new Double(lon);
					} catch (NumberFormatException e) {
						logger.warn("Invalid longitude:{}", lon);
						valid = false;
					}
					try {
						depth = new Double(ele);
					} catch (NumberFormatException e) {
						logger.warn("Invalid depth:{}", ele);
						valid = false;
					}
					Temporal temporal;
					try {
						XMLGregorianCalendar date = df.newXMLGregorianCalendar(time);
						date.setFractionalSecond(null);
						date = date.normalize();
						temporal = new TemporalImpl(date.toGregorianCalendar().getTime());
					} catch (Exception e) {
						temporal = new TemporalImpl();
					}
					if (valid) {
						Spatial spatial = new SpatialImpl(latitude, longitude, depth);
						PositionFix fix = new PositionFixImpl(currentTrack.getName(), temporal, spatial, 0, 0, "D");
						currentTrack.getPositionFixes().add(fix);
					}
				}
				if ("name".equals(localName) && currentTrack != null) {
					String name = builder.toString();
					currentTrack.setName(name);
				}
				if (trkpt) {
					if ("ele".equals(localName)) {
						ele = builder.toString();
					}
					if ("time".equals(localName)) {
						time = builder.toString();
					}
				}
			}
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			builder.append(ch, start, length);
		}
		
	}

}
