/*
 * This file is part of JGrasstools (http://www.jgrasstools.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * JGrasstools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.deelite.prototypes;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * <ol>
 * <li>Date, either 2 of 4 figure date, followed by month then date</li>
 * <li>Time</li>
 * <li>Vessel Name - either as single, unquoted word, or as a multi-word phrase
 * enclosed in quotation marks.</li>
 * <li>Symbology</li>
 * <li>Latitude Degrees (Debrief is able to handle decimal degrees - provide
 * zeros for mins and secs)</li>
 * <li>Latitude Minutes (Debrief is able to handle decimal mins - provide zeros
 * for secs)</li>
 * <li>Latitude Seconds</li>
 * <li>Latitude Hemisphere</li>
 * <li>Longitude Degrees (Debrief is able to handle decimal degrees - provide
 * zeros for mins and secs)</li>
 * <li>Longitude Minutes (Debrief is able to handle decimal mins - provide zeros
 * for secs)</li>
 * <li>Longitude Seconds</li>
 * <li>Longitude Hemisphere</li>
 * <li>Heading (0..359.9 degrees)</li>
 * <li>Speed (knots)</li>
 * <li>Depth (metres)</li>
 * </ol>
 */
public final class DataSource {
    public static final String TIMESTAMP = "timestamp";
    public static final String TIMESTAMP_H = "ts_readable";
    private final SimpleDateFormat dateFormatReadable = new SimpleDateFormat("hh:mm:ss");
    private final SimpleDateFormat dateFormat_yyMMdd = new SimpleDateFormat("yyMMdd hhmmss.SSS");
    private final SimpleDateFormat dateFormat_yyyyMMdd = new SimpleDateFormat("yyyyMMdd hhmmss.SSS");
    private SimpleFeatureBuilder pointFeatureBuilder;
    private SimpleFeatureBuilder linesFeatureBuilder;
    private GeometryFactory geometryFactory = new GeometryFactory();

    private long minTime = Long.MAX_VALUE;
    private long maxTime = -Long.MAX_VALUE;
    private File vesselFile;
    private List<SimpleFeature> allPointFeatures;
    private List<SimpleFeature> allLineFeatures;

    public DataSource( File vesselFile ) {
        this.vesselFile = vesselFile;
    }

    public List<SimpleFeature> getPointData() throws Exception {
        if (allPointFeatures == null) {
            getPointFeatureBuilder();
            List<String> linesList = readFileToLinesList(vesselFile);
            allPointFeatures = new ArrayList<SimpleFeature>();
            for( String line : linesList ) {
                String[] tokens = line.split("\\s+");
                SimpleFeature feature = parseLine(tokens);
                allPointFeatures.add(feature);
            }
        }
        return allPointFeatures;
    }

    public List<SimpleFeature> getLineData() throws Exception {
        if (allLineFeatures == null) {
            getLinesFeatureBuilder();
            List<SimpleFeature> pointData = getPointData();
            allLineFeatures = new ArrayList<SimpleFeature>();
            for( int i = 0; i < pointData.size() - 1; i++ ) {
                SimpleFeature first = pointData.get(i);
                SimpleFeature second = pointData.get(i + 1);
                Coordinate firstCoordinate = ((Geometry) first.getDefaultGeometry()).getCoordinate();
                Coordinate secondCoordinate = ((Geometry) second.getDefaultGeometry()).getCoordinate();
                LineString lineString = geometryFactory.createLineString(new Coordinate[]{firstCoordinate, secondCoordinate});
                List<Object> attributes = second.getAttributes();
                attributes.set(0, lineString);
                linesFeatureBuilder.addAll(attributes);
                SimpleFeature feature = linesFeatureBuilder.buildFeature(null);
                allLineFeatures.add(feature);
            }
        }
        return allLineFeatures;
    }

    public SimpleFeature parseLine( String tokens[] ) throws Exception {
        String rawVesselName = parseVesselName(tokens);
        String rawSymbology = tokens[3];
        String vesselNameAndSymbology = rawVesselName + "-" + rawSymbology;

        double latDegs = Double.valueOf(tokens[4]);
        double latMins = Double.valueOf(tokens[5]);
        double latSecs = Double.valueOf(tokens[6]);
        String latHemi = tokens[7];
        double lonDegs = Double.valueOf(tokens[8]);
        double lonMins = Double.valueOf(tokens[9]);
        double lonSecs = Double.valueOf(tokens[10]);
        String longHemi = tokens[11];

        latDegs = latDegs + latMins / 60d + latSecs / (60d * 60d);
        lonDegs = lonDegs + lonMins / 60d + lonSecs / (60d * 60d);

        if (latHemi.toLowerCase().equals("s"))
            latDegs = -latDegs;
        if (longHemi.toLowerCase().equals("w"))
            lonDegs = -lonDegs;

        Double speed = Double.valueOf(tokens[13]);
        Double depth = Double.valueOf(tokens[13]);
        Date dateTime = parseDateTime(tokens);

        Point point = geometryFactory.createPoint(new Coordinate(lonDegs, latDegs));
        long time = dateTime.getTime();
        minTime = min(minTime, time);
        maxTime = max(maxTime, time);

        Object[] values = new Object[]{point, vesselNameAndSymbology, speed, depth, time, dateFormatReadable.format(dateTime)};
        pointFeatureBuilder.addAll(values);
        SimpleFeature feature = pointFeatureBuilder.buildFeature(null);
        return feature;
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    private SimpleFeatureBuilder getPointFeatureBuilder() {
        if (pointFeatureBuilder == null) {
            SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
            b.setName("vessel");
            b.setCRS(DefaultGeographicCRS.WGS84);
            b.add("the_geom", Point.class);
            b.add("vessel", String.class);
            b.add("speed", Double.class);
            b.add("depth", Double.class);
            b.add(TIMESTAMP, Long.class);
            b.add(TIMESTAMP_H, String.class);
            SimpleFeatureType type = b.buildFeatureType();
            pointFeatureBuilder = new SimpleFeatureBuilder(type);
        }
        return pointFeatureBuilder;
    }

    private SimpleFeatureBuilder getLinesFeatureBuilder() {
        if (linesFeatureBuilder == null) {
            SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
            b.setName("vessellines");
            b.setCRS(DefaultGeographicCRS.WGS84);
            b.add("the_geom", LineString.class);
            b.add("vessel", String.class);
            b.add("speed", Double.class);
            b.add("depth", Double.class);
            b.add(TIMESTAMP, Long.class);
            b.add(TIMESTAMP_H, String.class);
            SimpleFeatureType type = b.buildFeatureType();
            linesFeatureBuilder = new SimpleFeatureBuilder(type);
        }
        return linesFeatureBuilder;
    }

    public String parseVesselName( String[] tokens ) {
        // Vessel Name - either as single, unquoted word, or as a multi-word
        // phrase enclosed in quotation marks.
        return tokens[2].replaceAll("\"", "");
    }

    public Date parseDateTime( String[] values ) throws ParseException {
        if (values[0].length() == 6) {
            return dateFormat_yyMMdd.parse(values[0] + " " + values[1]);
        } else if (values[0].length() == 8) {
            return dateFormat_yyyyMMdd.parse(values[0] + " " + values[1]);
        }
        return null;
    }

    /**
     * Read text from a file to a list of lines.
     * 
     * @param file the file to read.
     * @return the list of lines.
     * @throws IOException 
     */
    public static List<String> readFileToLinesList( File file ) throws IOException {
        BufferedReader br = null;
        List<String> lines = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while( (line = br.readLine()) != null ) {
                lines.add(line);
            }
            return lines;
        } finally {
            if (br != null)
                br.close();
        }
    }
}