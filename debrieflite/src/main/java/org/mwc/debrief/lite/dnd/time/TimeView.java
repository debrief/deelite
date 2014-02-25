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
package org.mwc.debrief.lite.dnd.time;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.mwc.debrief.lite.utils.Utils;

/**
 * @author snpe
 *
 */
public class TimeView extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton startButton;
	private JButton backLargeButton;
	private JButton backSmallButton;
	private JButton playButton;
	private JButton forwardSmallButton;
	private JButton forwardLargeButton;
	private JButton endButton;
	private JLabel textLabel;
	private JSlider slider;

	public TimeView() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setMaximumSize(new Dimension(buttonPanel.getMaximumSize().width, 100));
		add(buttonPanel);
		add(Box.createRigidArea(new Dimension(0,5)));
		
		startButton = createButton("media_beginning.png", "Move to start of dataset", buttonPanel);
		backLargeButton = createButton("media_rewind.png", "Move backward large step", buttonPanel);
		backSmallButton = createButton("media_back.png", "Move backward small step", buttonPanel);
		playButton = createButton("media_play.png", "Start automatically moving forward", buttonPanel);
		forwardSmallButton = createButton("media_forward.png", "Move forward small step", buttonPanel);
		forwardLargeButton = createButton("media_fast_forward.png", "Move forward large step", buttonPanel);
		endButton = createButton("media_end.png", "Move to end of dataset", buttonPanel);
		
		textLabel = new JLabel();
		textLabel.setText(Utils.getDefaultDateFormat().format(new Date()));
		textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(textLabel);
		add(Box.createRigidArea(new Dimension(0,5)));
		
		slider = new JSlider(0, 100, 0);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		//slider.setPaintLabels(true);
		
		add(slider);
		add(Box.createVerticalGlue());
		setWidgetsEnabled(false);
	}

	public void setWidgetsEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
		backLargeButton.setEnabled(enabled);
		backSmallButton.setEnabled(enabled);
		playButton.setEnabled(enabled);
		forwardLargeButton.setEnabled(enabled);
		forwardSmallButton.setEnabled(enabled);
		endButton.setEnabled(enabled);
		slider.setEnabled(enabled);
	}
	
	/**
	 * @param panel 
	 * @return
	 */
	private JButton createButton(String icon, String tooltip, JComponent panel) {
		JButton button = new JButton(Utils.getIcon(icon));
		button.setToolTipText(tooltip);
		panel.add(button);
		return button;
	}

}
