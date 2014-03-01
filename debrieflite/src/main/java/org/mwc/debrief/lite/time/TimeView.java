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
package org.mwc.debrief.lite.time;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.mwc.debrief.lite.AbstractMain;
import org.mwc.debrief.lite.DebriefMain;
import org.mwc.debrief.lite.layers.TrackLayer;
import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.model.impl.TemporalImpl;
import org.mwc.debrief.lite.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snpe
 *
 */
public class TimeView extends JPanel implements TimeListener {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(TimeView.class);
	
	private static final int SLIDER_MAX_VALUE = 100;
	private static final String EMPTY_LABEL = "n/a";
	private JButton startButton;
	private JButton backLargeButton;
	private JButton backSmallButton;
	private JButton playButton;
	private JButton forwardSmallButton;
	private JButton forwardLargeButton;
	private JButton endButton;
	private JLabel textLabel;
	private JSlider slider;
	private Temporal currentTime;
	private Temporal startTime;
	private Temporal endTime;
	
	private long LARGE_STEP = 10*60*1000; // 10 minutes
	private long SMALL_STEP = 1*60*1000; // 1 minutes
	private long STEP_INTERVAL = 1*1000; // 1 second
	private long timeScale;
	private boolean timeViewEnabled;
	private boolean changing = false;
	private boolean playing = false;
	private Thread playingThread;
	private JButton snailButton;
	boolean snailMode = false;

	public TimeView() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JPanel snailPanel = new JPanel();
		snailPanel.setMaximumSize(new Dimension(snailPanel.getMaximumSize().width, 80));
		snailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		add(snailPanel);
		add(Box.createRigidArea(new Dimension(0,5)));
		
		snailButton = new JButton();
		setSnailMode(false);
		snailPanel.add(snailButton);
		snailButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setSnailMode(!snailMode);
				TrackLayer trackLayer = (TrackLayer) AbstractMain.getMap().getMapComponentByType(TrackLayer.class);
				if (trackLayer != null) {
					trackLayer.setSnailMode(snailMode);
				}
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setMaximumSize(new Dimension(buttonPanel.getMaximumSize().width, 80));
		add(buttonPanel);
		add(Box.createRigidArea(new Dimension(0,5)));
		
		startButton = createButton("media_beginning.png", "Move to start of dataset", buttonPanel);
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (startTime != null) {
					currentTime = startTime;
					setCurrentTime();
					DebriefMain.getTimeController().notifyListeners(new TimeEvent(currentTime.getTime(), TimeView.this));
				}
			}
		});
		
		backLargeButton = createButton("media_rewind.png", "Move backward large step", buttonPanel);
		
		backLargeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addTime(-LARGE_STEP);
			}
		});
		
		
		backSmallButton = createButton("media_back.png", "Move backward small step", buttonPanel);
		
		backSmallButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addTime(-SMALL_STEP);
			}
		});
		playButton = createButton("media_play.png", "Start automatically moving forward", buttonPanel);
		
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				playing = !playing;
				setPlayButton();
			}
		});
		
		forwardSmallButton = createButton("media_forward.png", "Move forward small step", buttonPanel);
		
		forwardSmallButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addTime(SMALL_STEP);
			}
		});
		forwardLargeButton = createButton("media_fast_forward.png", "Move forward large step", buttonPanel);
		
		forwardLargeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addTime(LARGE_STEP);
			}
		});
		endButton = createButton("media_end.png", "Move to end of dataset", buttonPanel);
		
		endButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (endTime != null) {
					currentTime = endTime;
					setCurrentTime();
					DebriefMain.getTimeController().notifyListeners(new TimeEvent(currentTime.getTime(), TimeView.this));
				}
			}
		});
		
		textLabel = new JLabel();
		textLabel.setText(EMPTY_LABEL);
		textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(textLabel);
		add(Box.createRigidArea(new Dimension(0,5)));
		
		slider = new JSlider(0, SLIDER_MAX_VALUE, 0);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(false);
		slider.setValue(0);
		//slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if (currentTime == null || startTime == null || endTime == null || changing) {
					return;
				}
				JSlider source = (JSlider)e.getSource();
				
			    int val = source.getValue();
			    long timeRange = endTime.getTime() - startTime.getTime();
			    //long time = currentTime.getTime() - startTime.getTime();
			    long scale = timeRange*val/SLIDER_MAX_VALUE;
				long newTime = startTime.getTime() + scale;
				if (newTime != currentTime.getTime()) {
					currentTime = new TemporalImpl(newTime);
					setCurrentTime();
					DebriefMain.getTimeController().notifyListeners(new TimeEvent(currentTime.getTime(), TimeView.this));
				}
			}
		});
		
		add(slider);
		add(Box.createVerticalGlue());
		setTimeViewEnabled(false);
		DebriefMain.getTimeController().addTimeListener(this);
		playingThread = new Thread("Playing thread") {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(STEP_INTERVAL);
					} catch (InterruptedException e) {
						logger.info("playingThread stopped {}", e);
					}
					if (timeViewEnabled && playing && currentTime != null
							&& startTime != null && endTime != null) {
						long newTime = currentTime.getTime() + SMALL_STEP;
						if (newTime > endTime.getTime()) {
							currentTime = endTime;
							playing = false;
							setPlayButton();
						} else {
							currentTime = new TemporalImpl(newTime);
							setCurrentTime();
							DebriefMain.getTimeController().notifyListeners(
									new TimeEvent(newTime, TimeView.this));
						}
					}
				}
			}
		};
		playingThread.start();
		setWidgetsEnabled(false);
	}

	/**
	 * @param b
	 */
	private void setSnailMode(boolean mode) {
		if (mode) {
			snailButton.setIcon(Utils.getIcon("normal.gif"));
			snailButton.setToolTipText("Set Normal Mode");
		} else {
			snailButton.setIcon(Utils.getIcon("snail.gif"));
			snailButton.setToolTipText("Set Snail Mode");
		}
		snailMode = mode;
	}

	public void setTimeViewEnabled(boolean enabled) {
		if (this.timeViewEnabled == enabled) {
			return;
		}
		this.timeViewEnabled = enabled;
		setWidgetsEnabled(enabled);
		if (enabled == false) {
			textLabel.setText(EMPTY_LABEL);
		} else {
			updateTrackLayer();
		}
		playing = false;
		changing = false;
		setPlayButton();
	}

	/**
	 * @param enabled
	 */
	private void setWidgetsEnabled(boolean enabled) {
		snailButton.setEnabled(enabled);
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
	 * 
	 */
	private void setPlayButton() {
		if (!playing) {
			playButton.setIcon(Utils.getIcon("media_play.png"));
			playButton.setToolTipText("Start automatically moving forward");
		} else {
			playButton.setIcon(Utils.getIcon("media_pause.png"));
			playButton.setToolTipText("Stop automatically moving forward");
		}
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

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.time.TimeListener#notify(org.mwc.debrief.lite.time.TimeEvent)
	 */
	@Override
	public void newTime(TimeEvent event) {
		if (event == null || event.getSource() == this) {
			return;
		}
		if (currentTime != null && currentTime.getTime() == event.getTime() ) {
			return;
		}
		currentTime = new TemporalImpl(event.getTime());
		setCurrentTime();
	}

	private void updateTrackLayer() {
		TrackLayer trackLayer = (TrackLayer) AbstractMain.getMap().getMapComponentByType(TrackLayer.class);
		timeScale = 0;
		String text = EMPTY_LABEL;
		if (trackLayer != null) {
			currentTime = trackLayer.getCurrentTime();
			startTime = trackLayer.getStartTime();
			endTime = trackLayer.getEndTime();
			if (startTime != null && endTime != null) {
				timeScale = endTime.getTime() - startTime.getTime();
				if (timeScale < 0) {
					timeScale = 0;
				}
			}
			if (timeScale > 0 && currentTime != null && currentTime.getTime() > 0) {
				text = Utils.getDefaultDateFormat().format(currentTime.getDate());
			}
		}
		textLabel.setText(text);
		slider.setValue(0);
		setWidgetsEnabled(timeScale > 0 && !EMPTY_LABEL.equals(text));
	}
	
	private void setCurrentTime() {
		long sliderValue = 0;
		String text = EMPTY_LABEL;
		if (timeScale > 0 && currentTime != null && currentTime.getTime() > 0) {
			text = Utils.getDefaultDateFormat().format(currentTime.getDate());
			long time = currentTime.getTime() - startTime.getTime();
			sliderValue = SLIDER_MAX_VALUE*time/timeScale;
		}
		changing = true;
		slider.setValue((int) sliderValue);
		changing=false;
		textLabel.setText(text);
		setWidgetsEnabled(timeScale > 0 && !EMPTY_LABEL.equals(text));
	}

	/* (non-Javadoc)
	 * @see org.mwc.debrief.lite.time.TimeListener#getSource()
	 */
	@Override
	public Object getSource() {
		return this;
	}

	/**
	 * 
	 */
	private void addTime(long step) {
		if (currentTime != null) {
			long newTime = currentTime.getTime() + step;
			if (startTime != null && startTime.getTime() > newTime) {
				newTime = startTime.getTime();
			}
			if (endTime != null && endTime.getTime() < newTime) {
				newTime = endTime.getTime();
			}
			if (newTime != currentTime.getTime()) {
				currentTime = new TemporalImpl(newTime);
				setCurrentTime();
				DebriefMain.getTimeController().notifyListeners(new TimeEvent(currentTime.getTime(), TimeView.this));
			}
		}
	}

}
