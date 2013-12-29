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

import java.util.List;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.Graphic;
import org.geotools.styling.LabelPlacement;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.MarkImpl;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * A class to create some prototype styles. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class StyleGenerator {
    public static StyleFactory sf = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());
    public static FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
    public static StyleBuilder sb = new StyleBuilder(sf, ff);

    /**
     * Create style for a points layer. 
     * 
     * @param schema
     * @return a point style with labels.
     */
    public static Style getPointsStyle( SimpleFeatureType schema ) {
        Style style = SLD.createSimpleStyle(schema);

        Rule origRule = style.featureTypeStyles().get(0).rules().get(0);

        Symbolizer symbolizer = origRule.symbolizers().get(0);
        PointSymbolizer pointSymbolizer = (PointSymbolizer) symbolizer;
        Graphic graphic = pointSymbolizer.getGraphic();

        // size
        graphic.setSize(ff.literal("12"));

        Mark pointMark = new MarkImpl();
        pointMark.setWellKnownName(ff.literal("circle"));
        graphic.graphicalSymbols().clear();
        graphic.graphicalSymbols().add(pointMark);

        Stroke stroke = pointMark.getStroke();
        if (stroke == null) {
            stroke = sf.createStroke(ff.literal("#940000"), ff.literal("1"), ff.literal("1"));
        } else {
            stroke.setColor(ff.literal("#940000"));
            stroke.setOpacity(ff.literal("1"));
        }
        pointMark.setStroke(stroke);

        Fill fill = pointMark.getFill();
        if (fill == null) {
            fill = sf.createFill(ff.literal("#FF0000"), ff.literal("0.5"));
        } else {
            fill.setColor(ff.literal("#FF0000"));
            fill.setOpacity(ff.literal("0.5"));
        }
        pointMark.setFill(fill);

        LabelPlacement labelPlacement = sf.createPointPlacement(sf.createAnchorPoint(ff.literal(0.0), ff.literal(0.0)),
                sf.createDisplacement(ff.literal(0.0), ff.literal(0.0)), ff.literal(0.0));

        Font font = sb.createFont("Arial", false, false, 12); //$NON-NLS-1$
        TextSymbolizer textSymbolizer = sf.createTextSymbolizer(//
                sf.createFill(ff.literal("#000000")), //
                new Font[]{font}, //
                null, //
                ff.property(DataSource.TIMESTAMP_H), //
                labelPlacement, //
                null);
        origRule.symbolizers().add(textSymbolizer);

        // Rule rule = sf.createRule();
        // rule.symbolizers().add(pointSymbolizer);
        // rule.symbolizers().add(textSymbolizer);
        //
        // FeatureTypeStyle fts = sf.createFeatureTypeStyle(new Rule[]{rule});
        // Style newStyle = sf.createStyle();
        // newStyle.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * Create style for a lines layer. 
     * 
     * @param schema
     * @return a line style.
     */
    public static Style getLinesStyle( SimpleFeatureType schema ) {
        Style style = SLD.createSimpleStyle(schema);

        Rule origRule = style.featureTypeStyles().get(0).rules().get(0);

        Symbolizer symbolizer = origRule.symbolizers().get(0);
        LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;

        Stroke stroke = lineSymbolizer.getStroke();
        if (stroke == null) {
            stroke = sf.createStroke(ff.literal("#0000FF"), ff.literal("2"), ff.literal("1"));
        } else {
            stroke.setColor(ff.literal("#0000FF"));
            stroke.setWidth(ff.literal("2"));
            stroke.setOpacity(ff.literal("1"));
        }
        lineSymbolizer.setStroke(stroke);
        return style;
    }

    /**
     * Applies a filter to the rules of a style.
     * 
     * @param style
     * @param filter
     */
    public static void applyFilter( Style style, Filter filter ) {
        List<Rule> rules = style.featureTypeStyles().get(0).rules();
        for( Rule rule : rules ) {
            rule.setFilter(filter);
        }
    }

}
