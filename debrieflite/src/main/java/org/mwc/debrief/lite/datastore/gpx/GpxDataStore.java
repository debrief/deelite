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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.mwc.debrief.lite.datastores.AbstractDataStore;
import org.mwc.debrief.lite.datastores.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snpe
 * 
 */
public class GpxDataStore extends AbstractDataStore {
		
	static final Logger logger = LoggerFactory.getLogger(GpxDataStore.class);
	
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
	private void parseFile(InputStream is) {
		try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(is))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line.trim();
				//readLine(line);
			}
		} catch (Exception e) {
			logger.info("Reading error:", e);
			valid = false;
			exceptions.add(e);
		}
	}

}
