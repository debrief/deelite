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
package org.mwc.debrief.lite.graphics;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.Iterator;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.omGraphics.OMGeometry;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.GreatCircle;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.Debug;
import com.bbn.openmap.util.DeepCopyUtil;

/**
 * OMGraphic object that represents a line, labeled with distances.
 */
public class DebriefDistance extends OMLine {

	private static final long serialVersionUID = 1L;
	private static final String DEGREE  = "\u00b0";
	protected OMGraphicList labels = new OMGraphicList();
	protected OMGraphicList points = new OMGraphicList();

	protected Length distUnits = Length.METER;
	public DecimalFormat df = new DecimalFormat("0.#");
	/**
	 * Paint used for labels
	 */
	protected Paint labelPaint;
	/**
	 * Font used for labels
	 */
	protected Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	private int units;
	private double[] rawllpts = new double[4];

	public DebriefDistance() {
		super();
		setRenderType(RENDERTYPE_LATLON);
	}

	public DebriefDistance(double lat_1, double lon_1, double lat_2,
			double lon_2, int units, int lType, Length distanceUnits) {
		this(lat_1, lon_1, lat_2, lon_2, units, lType, -1, distanceUnits);
	}

	public DebriefDistance(double lat_1, double lon_1, double lat_2,
			double lon_2, int units, int lType, int nsegs, Length distanceUnits) {
		super(lat_1, lon_1, lat_2, lon_2, lType, nsegs);
		this.units = units;
		setDistUnits(distanceUnits);
	}

	/**
	 * Set the Length object used to represent distances.
	 */
	public void setDistUnits(Length distanceUnits) {
		distUnits = distanceUnits;
	}

	/**
	 * Get the Length object used to represent distances.
	 */
	public Length getDistUnits() {
		return distUnits;
	}

	public void createLabels() {
		labels.clear();
		points.clear();

		double[] llpts = getLL();
		System.arraycopy(llpts, 0, rawllpts, 0, 4);
		ProjMath.arrayDegToRad(rawllpts);
		if (rawllpts == null) {
			return;
		}
		if (rawllpts.length < 4) {
			return;
		}

		Geo lastGeo = new Geo(rawllpts[0], rawllpts[1],
				units == DECIMAL_DEGREES);
		double latpnt = rawllpts[0];
		double lonpnt = rawllpts[1];
		if (units == RADIANS) {
			latpnt = ProjMath.radToDeg(latpnt);
			lonpnt = ProjMath.radToDeg(lonpnt);
		}
		points.add(new OMPoint(latpnt, lonpnt, 2));
		Geo curGeo = null;
		for (int p = 2; p < rawllpts.length; p += 2) {
			if (curGeo == null) {
				curGeo = new Geo(rawllpts[p], rawllpts[p + 1],
						units == DECIMAL_DEGREES);
			} else {
				if (units == DECIMAL_DEGREES) {
					curGeo.initialize(rawllpts[p], rawllpts[p + 1]);
				} else {
					curGeo.initializeRadians(rawllpts[p], rawllpts[p + 1]);
				}
			}

			float dist = getDist(lastGeo, curGeo);
		
			double angle = GreatCircle.sphericalAzimuth(rawllpts[0], rawllpts[1],
					rawllpts[2], rawllpts[3]);
			double angleInDg = ProjMath.radToDeg(angle);
			while (angleInDg < 0) {
				angleInDg += 360;
			}
			
			labels.add(createLabel(lastGeo, curGeo, dist, distUnits, angleInDg));
			latpnt = rawllpts[p];
			lonpnt = rawllpts[p + 1];
			if (units == RADIANS) {
				latpnt = ProjMath.radToDeg(latpnt);
				lonpnt = ProjMath.radToDeg(lonpnt);
			}

			points.add(new OMPoint(latpnt, lonpnt, 2));
			lastGeo.initialize(curGeo);
		}
	}

	/**
	 * Get an OMText label for a segments between the given lat/lon points 
	 * @param angleInDg 
	 * 
	 */
	public OMText createLabel(Geo g1, Geo g2, float dist,
			Length distanceUnits, double angleInDg) {
		Geo mid;
		switch (getLineType()) {
		case LINETYPE_STRAIGHT:
			float lat = (float) (g1.getLatitude() + g2.getLatitude()) / 2f;
			float lon = (float) (g1.getLongitude() + g2.getLongitude()) / 2f;
			mid = new Geo(lat, lon);
			break;
		case LINETYPE_RHUMB:
			System.err.println("Rhumb distance calculation not implemented.");
		case LINETYPE_GREATCIRCLE:
		case LINETYPE_UNKNOWN:
		default:
			mid = g1.midPoint(g2);
		}

		String text = getText(dist, distanceUnits, angleInDg);
		OMText omtext = new OMText((float) mid.getLatitude(),
				(float) mid.getLongitude(), text, OMText.JUSTIFY_LEFT);
		return omtext;
	}

	/**
	 * @param dist
	 * @param distanceUnits
	 * @param angleInDg
	 * @return
	 */
	private String getText(float dist, Length distanceUnits, double angleInDg) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(df.format(distanceUnits.fromRadians(dist)));
		builder.append(distanceUnits.getAbbr());
		builder.append(" ");
		builder.append((int)angleInDg);
		builder.append(DEGREE);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Return the distance between that lat/lons defined in radians. The
	 * returned value is in radians.
	 */
	public float getDist(Geo g1, Geo g2) {
		switch (getLineType()) {
		case LINETYPE_STRAIGHT:
			float lonDist = ProjMath.lonDistance((float) g2.getLongitude(),
					(float) g1.getLongitude());
			float latDist = (float) g2.getLatitude() - (float) g1.getLatitude();
			return (float) Math.sqrt(lonDist * lonDist + latDist * latDist);
		case LINETYPE_RHUMB:
			Debug.error("Rhumb distance calculation not implemented.");
		case LINETYPE_GREATCIRCLE:
		case LINETYPE_UNKNOWN:
		default:
			return (float) g1.distance(g2);
		}
	}

	/**
	 * Prepare the line for rendering.
	 * 
	 * @param proj
	 *            Projection
	 * @return true if generate was successful
	 */
	public boolean generate(Projection proj) {
		boolean ret = super.generate(proj);
		createLabels();
		labels.generate(proj);
		points.generate(proj);
		return ret;
	}

	/**
	 * Paint the line. This works if generate() has been successful.
	 * 
	 * @param g
	 *            java.awt.Graphics to paint the line onto.
	 */
	public void render(Graphics g) {
		super.render(g);
		renderPoints(g);
		renderLabels(g);
	}

	public void clear() {
		points.clear();
		labels.clear();
	}

	/**
	 * render points
	 */
	protected void renderPoints(Graphics g) {
		Paint pointPaint = getLabelPaint();

		for (Iterator<OMGraphic> it = points.iterator(); it.hasNext();) {
			OMGraphic point = (OMPoint) it.next();
			point.setLinePaint(pointPaint);
			point.setFillPaint(pointPaint);
			point.render(g);
		}
	}

	/**
	 * render labels
	 */
	protected void renderLabels(Graphics g) {
		Font f = getFont();
		Paint labelPaint = getLabelPaint();
		Paint mattingPaint = getMattingPaint();
		boolean isMatted = isMatted();
		for (Iterator<OMGraphic> it = labels.iterator(); it.hasNext();) {
			OMText text = (OMText) it.next();
			text.setFont(f);
			text.setLinePaint(labelPaint);
			if (isMatted) {
				text.setFillPaint(mattingPaint);
			}
			text.render(g);
		}
	}

	/**
	 * Set paint used for labels
	 * 
	 * @param lPaint
	 *            paint used for labels
	 */
	public void setLabelPaint(Paint lPaint) {
		labelPaint = lPaint;
	}

	/**
	 * @return normal paint used for labels
	 */
	public Paint getLabelPaint() {
		if (labelPaint == null) {
			return getLinePaint();
		}
		return labelPaint;
	}

	/**
	 * @param font
	 *            font used for labels
	 */
	public void setFont(Font font) {
		if (font == null) {
			labelFont = OMText.DEFAULT_FONT;
		} else {
			labelFont = font;
		}
	}

	/**
	 * @return font used for labels
	 */
	public Font getFont() {
		if (labelFont == null) {
			labelFont = OMText.DEFAULT_FONT;
		}
		return labelFont;
	}

	private void writeObject(java.io.ObjectOutputStream stream)
			throws java.io.IOException {
		stream.defaultWriteObject();
		stream.writeObject(distUnits.getAbbr());
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws java.io.IOException, ClassNotFoundException {
		stream.defaultReadObject();
		distUnits = Length.get((String) stream.readObject());
	}

	public void restore(OMGeometry source) {
		super.restore(source);
		if (source instanceof DebriefDistance) {
			DebriefDistance dist = (DebriefDistance) source;
			this.labels = DeepCopyUtil.deepCopy(dist.labels);
			this.points = DeepCopyUtil.deepCopy(dist.points);
			this.distUnits = dist.distUnits;
			this.df = new DecimalFormat(dist.df.toLocalizedPattern());
			this.labelPaint = dist.labelPaint;
			if (dist.labelFont != null) {
				this.labelFont = dist.labelFont
						.deriveFont(AffineTransform.TYPE_IDENTITY);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.bbn.openmap.omGraphics.OMGraphicAdapter#setStroke(java.awt.Stroke)
	 */
	@Override
	public void setStroke(Stroke s) {
		super.setStroke(new BasicStroke(2));
	}	
	
}