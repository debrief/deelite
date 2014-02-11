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


final public class Conversions
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  final static private double RADIAN_CONV = 180 / Math.PI;
  final static private double NM_M_CONV = 1852d;
  final static private double DEGS_KM_CONV = 60d * NM_M_CONV / 1000d;
  final static private double DEGS_M_CONV = 60d * NM_M_CONV;
  final static private double KTS_MPS_CONV = 1852d / 3600;
  final static private double MILES_M_CONV = 1609.344;
  final static private double FT_M_CONV = 0.3048;
  /** Note method of expressing this conversion using existing, exact constants
   * as supplied by Daniel Thibault, Dec 09
   */
  final static private double YDS_NM_CONV = NM_M_CONV/(3d*FT_M_CONV); 

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  final public static double Rads2Degs(final double val)
  {
    return val * RADIAN_CONV;
  }

  final public static double Degs2Rads(final double val)
  {
    return val / RADIAN_CONV;
  }

  final public static double Degs2Yds(final double val)
  {
    return Nm2Yds(Degs2Nm(val));
  }

  final public static double Yds2Degs(final double val)
  {
    return Nm2Degs(Yds2Nm(val));
  }

  final public static double Kts2Yps(final double val)
  {
    return val * YDS_NM_CONV / 3600;
  }

  final public static double Kts2Mps(final double val)
  {
    return val * KTS_MPS_CONV;
  }

  final public static double Mps2Kts(final double val)
  {
    return val / KTS_MPS_CONV;
  }

  final public static double Yps2Kts(final double val)
  {
    return val / YDS_NM_CONV * 3600;
  }

  final public static double Nm2Yds(final double val)
  {
    return val * YDS_NM_CONV;
  }

  final public static double Yds2Nm(final double val)
  {
    return val / YDS_NM_CONV;
  }

  final public static double Nm2Degs(final double val)
  {
    return val / 60;
  }

  final public static double Degs2Nm(final double val)
  {
    return val * 60;
  }

  final public static double Degs2m(final double val)
  {
    return val * DEGS_M_CONV;
  }

  final public static double m2Degs(final double val)
  {
    return val / DEGS_M_CONV;
  }

  final public static double Degs2Km(final double val)
  {
    return val * DEGS_KM_CONV;
  }

  final public static double ft2m(final double val)
  {
    return val * FT_M_CONV;
  }


  final public static double m2ft(final double val)
  {
    return val / FT_M_CONV;
  }

  final public static double miles2metres(final double val)
  {
    return val * MILES_M_CONV;
  }


  final public static double clipRadians(final double val)
  {
	double theVal = val;
    while (theVal > 2 * Math.PI)
      theVal = theVal - 2 * Math.PI;
    while (theVal < 0)
      theVal = theVal + 2 * Math.PI;

    return theVal;
  }
  
	/** convert the range to the supplied units
	 * 
	 * @param range range (in degrees)
	 * @param theUnits target units
	 * @return converted value
	 */
	public static final double convertRange(final double range, final String theUnits)
	{
		double theRng = 0;
		// do the units conversion
		if (theUnits.equals(UnitsPropertyEditor.YDS_UNITS)
				|| theUnits
						.equals(UnitsPropertyEditor.OLD_YDS_UNITS))
		{
			theRng = Degs2Yds(range);
		}
		else if (theUnits
				.equals(UnitsPropertyEditor.KYD_UNITS))
		{
			theRng = Degs2Yds(range) / 1000.0;
		}
		else if (theUnits
				.equals(UnitsPropertyEditor.METRES_UNITS))
		{
			theRng = Degs2m(range);
		}
		else if (theUnits.equals(UnitsPropertyEditor.KM_UNITS))
		{
			theRng = Degs2Km(range);
		}
		else if (theUnits.equals(UnitsPropertyEditor.NM_UNITS))
		{
			theRng = Degs2Nm(range);
		}
		else
		{
			System.err.println("Range/Bearing units in properties file may be corrupt");
		}
		return theRng;
	}  

}

