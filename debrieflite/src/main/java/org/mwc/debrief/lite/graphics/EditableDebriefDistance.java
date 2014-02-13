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

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.EditableOMLine;
import com.bbn.openmap.omGraphics.GraphicAttributes;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.event.EOMGEvent;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.util.Debug;

/**
 * The EditableDebriefDistance encompasses an DebriefDistance, providing methods for
 * modifying or creating it.
 * 
 */
public class EditableDebriefDistance extends EditableOMLine {

    /**
     * Create the EditableDebriefDistance, setting the state machine to create the
     * line off of the gestures.
     */
    public EditableDebriefDistance() {
        super();
    }

    public EditableDebriefDistance(GraphicAttributes ga) {
        super(ga);
    }
    
    /**
     * Create the EditableDebriefDistance with an DebriefDistance already defined, ready
     * for editing.
     * 
     * @param debriefDistance
     *            DebriefDistance that should be edited.
     */
    public EditableDebriefDistance(DebriefDistance debriefDistance) {
        super(debriefDistance);
    }

    /**
     * Create and set the graphic within the state machine. The
     * GraphicAttributes describe the type of line to create.
     */
    public void createGraphic(GraphicAttributes ga) {
        init();
        stateMachine.setUndefined();
        int renderType = OMGraphic.RENDERTYPE_LATLON;
        int lineType = OMGraphic.LINETYPE_GREATCIRCLE;

        if (ga != null) {
            renderType = ga.getRenderType();
            lineType = ga.getLineType();
        }

        if (Debug.debugging("eomg")) {
            Debug.output("EditableDebriefDistance.createGraphic(): rendertype = "
                    + renderType);
        }

        if (lineType == OMGraphic.LINETYPE_UNKNOWN) {
            lineType = OMGraphic.LINETYPE_GREATCIRCLE;
            if (ga != null) {
                ga.setLineType(OMGraphic.LINETYPE_GREATCIRCLE);
            }
        }

        this.line = (DebriefDistance) createGraphic(renderType, lineType);
        
        if (ga != null) {
            ga.setRenderType(line.getRenderType());
            ga.setTo(line, true);
        }
    }

    /**
     * Extendable method to create specific subclasses of DebriefDistances.
     */
    public OMGraphic createGraphic(int renderType, int lineType) {
        OMGraphic g = null;
        switch (renderType) {
        case (OMGraphic.RENDERTYPE_OFFSET):
            System.err.println("Offset type not supported for DebriefDistance");
        }
        g = new DebriefDistance(0, 0, 0, 0, OMGraphic.RADIANS, lineType,
                Length.METER);
        return g;
    }

    protected void modifyOMGraphicForEditRender() {
    }

    protected void resetOMGraphicAfterEditRender() {
    }

	/* (non-Javadoc)
	 * @see com.bbn.openmap.omGraphics.EditableOMGraphic#redraw(java.awt.event.MouseEvent, boolean, boolean)
	 */
	@Override
	public void redraw(MouseEvent e, boolean firmPaint, boolean drawXOR) {
		if (!firmPaint) {
			super.redraw(e, firmPaint, drawXOR);
		} else {
			OMGraphic g = getGraphic();
			if (g instanceof DebriefDistance) {
				((DebriefDistance)g).setLL(new double[] {0,0,0,0});
				((DebriefDistance)g).clear();
				setMovingPoint(null);
				((DebriefDistance)g).generate(getProjection());
				fireEvent(new EOMGEvent());
			}
			super.redraw(e, firmPaint, drawXOR);
			
		}
	}
}
