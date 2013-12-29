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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

/**
 * This class explains how to do apply temporary filters for rendering.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class FilterRenderProto {

    public FilterRenderProto() throws Exception {

        String imageFolder = "/media/FATBOTTOMED/Dropbox/hydrologis/lavori/2014_ian_mayo/";
        String vesselPath = "/media/FATBOTTOMED/Dropbox/hydrologis/lavori/2014_ian_mayo/boat1.rep";
        int imageWidth = 2400;
        int imageHeight = 1200;

        File imageFolderFile = new File(imageFolder);
        File vesselFile = new File(vesselPath);

        /*
         * read the input data to a featurecollection to be rendered
         */
        DataSource mapper = new DataSource(vesselFile);
        SimpleFeatureCollection vesselDataPointsFC = FeatureCollections.newCollection();
        List<SimpleFeature> pointData = mapper.getPointData();
        vesselDataPointsFC.addAll(pointData);
        SimpleFeatureCollection vesselDataLinesFC = FeatureCollections.newCollection();
        List<SimpleFeature> lineData = mapper.getLineData();
        vesselDataLinesFC.addAll(lineData);
        ReferencedEnvelope referencedEnvelope = new ReferencedEnvelope(vesselDataPointsFC.getBounds());
        referencedEnvelope.expandBy(0.1);
        long minTime = mapper.getMinTime();
        long maxTime = mapper.getMaxTime();
        long middleTime = minTime + (maxTime - minTime) / 2;

        /*
         * create the MapContent
         */
        MapContent content = new MapContent();
        content.setTitle("dump");

        /*
         * add the read featurecollection as new featurelayer to the mapcontent
         * and apply a default style
         */
        Style defaultLinesStyle = StyleGenerator.getLinesStyle(vesselDataLinesFC.getSchema());
        // Style defaultLinesStyle = SLD.createSimpleStyle(vesselDataLinesFC.getSchema());
        FeatureLayer vesselDataLinesLayer = new FeatureLayer(vesselDataLinesFC, defaultLinesStyle);
        content.addLayer(vesselDataLinesLayer);

        Style defaultPointsStyle = StyleGenerator.getPointsStyle(vesselDataPointsFC.getSchema());
        // Style defaultPointsStyle = SLD.createSimpleStyle(vesselDataPointsFC.getSchema());
        FeatureLayer vesselDataPointsLayer = new FeatureLayer(vesselDataPointsFC, defaultPointsStyle);
        content.addLayer(vesselDataPointsLayer);

        StreamingRenderer renderer = new StreamingRenderer();
        renderer.setMapContent(content);

        double envW = referencedEnvelope.getWidth();
        double envH = referencedEnvelope.getHeight();

        if (envW < envH) {
            double newEnvW = envH * (double) imageWidth / (double) imageHeight;
            double delta = newEnvW - envW;
            referencedEnvelope.expandBy(delta / 2, 0);
        } else {
            double newEnvH = envW * (double) imageHeight / (double) imageWidth;
            double delta = newEnvH - envH;
            referencedEnvelope.expandBy(0, delta / 2.0);
        }

        /*
         * render all the features to image file
         */
        String fileEnding = "_01.png";
        renderToFile(imageWidth, imageHeight, imageFolderFile, vesselFile, referencedEnvelope, renderer, fileEnding);

        /*
         * create a simple filter that limits to the features being in
         * the second half of the track.
         */
        StringBuilder sb = new StringBuilder();
        sb.append(DataSource.TIMESTAMP);
        sb.append(" < ");
        sb.append(maxTime);
        sb.append(" AND ");
        sb.append(DataSource.TIMESTAMP);
        sb.append(" > ");
        sb.append(middleTime);
        /*
         * create the filter from the string and apply it as a query to the 
         * featurelayer
         */
        Filter filter = ECQL.toFilter(sb.toString());
        // WITH QUERY
        Query queryPoints = new Query(vesselDataPointsFC.getSchema().getName().getLocalPart());
        queryPoints.setFilter(filter);
        vesselDataPointsLayer.setQuery(queryPoints);
        Query queryLines = new Query(vesselDataLinesFC.getSchema().getName().getLocalPart());
        queryLines.setFilter(filter);
        vesselDataLinesLayer.setQuery(queryLines);

        // WITH FILTER
        // StyleGenerator.applyFilter(vesselDataPointsLayer.getStyle(), filter);

        /*
         * render the same layer as before with the query applied
         */
        fileEnding = "_02.png";
        renderToFile(imageWidth, imageHeight, imageFolderFile, vesselFile, referencedEnvelope, renderer, fileEnding);

        content.dispose();
    }

    private void renderToFile( int imageWidth, int imageHeight, File imageFolderFile, File vesselFile,
            ReferencedEnvelope referencedEnvelope, StreamingRenderer renderer, String fileEnding ) throws IOException {
        Rectangle imageBounds = new Rectangle(0, 0, imageWidth, imageHeight);
        BufferedImage dumpImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dumpImage.createGraphics();
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        renderer.paint(g2d, imageBounds, referencedEnvelope);
        ImageIO.write(dumpImage, "png", new File(imageFolderFile, vesselFile.getName() + fileEnding)); //$NON-NLS-1$
    }

    public static void main( String[] args ) throws Exception {
        new FilterRenderProto();
    }

}
