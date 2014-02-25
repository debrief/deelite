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
package org.mwc.debrief.lite.actions;

import javax.swing.AbstractAction;

import org.mwc.debrief.lite.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author snpe
 *
 */
public abstract class AbstractDebriefAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	static final Logger logger = LoggerFactory.getLogger(AbstractDebriefAction.class);
	

	public AbstractDebriefAction(String name, String shortDescription, String iconName) {
		super(name);
		putValue(SHORT_DESCRIPTION, shortDescription);
		putValue(SMALL_ICON, Utils.getIcon(iconName));
	}
	
	public AbstractDebriefAction(String name, String shortDescription, String iconName,
			int mnemonic) {
		this(name, shortDescription, iconName);
		putValue(MNEMONIC_KEY, mnemonic);
	}

}
