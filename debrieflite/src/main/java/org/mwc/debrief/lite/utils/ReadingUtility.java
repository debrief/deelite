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
package org.mwc.debrief.lite.utils;

import java.awt.Color;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author snpe
 *
 */
public class ReadingUtility {

	static private final java.text.DecimalFormat shortFormat = new java.text.DecimalFormat(
			"0.000", new DecimalFormatSymbols(Locale.UK));
	// comma is used as the decimal separator here
	static private final java.text.DecimalFormat shortCommaFormat = new java.text.DecimalFormat(
			"0.000", new DecimalFormatSymbols(Locale.FRANCE));

	private static Map<String, Color> colors = new HashMap<String, Color>(); 

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
	}
	static public double readThisDouble(final String value) 
			throws ParseException {
		double res;
		
		// do some trimming, just in case
		final String trimmed = value.trim();

		// SPECIAL CASE: An external system is producing Debrief datafiles. It
		// puts NaN in for course, and it's making us trip over.
		if (trimmed.toUpperCase().equals("NAN"))
			res = 0;
		else
		{
			try 
			{
				res = shortFormat.parse(trimmed).doubleValue();
			} 
			catch (final ParseException e) 
			{
				res = shortCommaFormat.parse(trimmed).doubleValue();
			}
		}

		return res;
	}
	
	public static Color getSymbologyColor(String symbology) {
		if (symbology != null && symbology.startsWith("@") && symbology.length() == 2) {
			symbology = symbology.substring(1);
		}
		Color c = colors.get(symbology);
		if (c == null) {
			return Color.white;
		}
		return c;
	}
}
