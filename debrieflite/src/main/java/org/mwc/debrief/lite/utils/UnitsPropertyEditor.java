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

import java.beans.PropertyEditorSupport;

/**
* ////////////////////////////////////
* class providing a drop-down list of units types
*/

public class UnitsPropertyEditor extends PropertyEditorSupport
{

  public static final String UNITS_PROPERTY = "RNG_UNITS";
  public static final String YDS_UNITS = "yd";
  public static final String KYD_UNITS = "kyd";
  public static final String METRES_UNITS = "m";
  public static final String KM_UNITS = "km";
  public static final String NM_UNITS = "nm";

  public static final String OLD_YDS_UNITS = "yds";
  public static final String OLD_KYD_UNITS = "kyds";


  /**
   * the user's current selection
   */
  protected String _myUnits;

  /**
   * get the list of String we provide editing for
   *
   * @return list of units types
   */
  public String[] getTags()
  {
    final String tags[] = {YDS_UNITS, KYD_UNITS, NM_UNITS, KM_UNITS, METRES_UNITS};
    return tags;
  }

  public Object getValue()
  {
    return _myUnits;
  }

  public void setValue(final Object p1)
  {
    if(p1 instanceof String)
    {
      final String val = (String) p1;
      setAsText(val);
    }
  }

  public void setAsText(final String val)
  {
    _myUnits = val;
  }

  public String getAsText()
  {
    return _myUnits;
  }
}
