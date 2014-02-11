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
package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

import org.mwc.debrief.lite.actions.AboutAction;
import org.mwc.debrief.lite.actions.FitToWindowAction;
import org.mwc.debrief.lite.actions.PanAction;
import org.mwc.debrief.lite.actions.QuitAction;
import org.mwc.debrief.lite.actions.RangeBearingAction;
import org.mwc.debrief.lite.actions.RedrawAction;
import org.mwc.debrief.lite.actions.ZoomInAction;
import org.mwc.debrief.lite.actions.ZoomOutAction;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.ZoomSupport;
import com.bbn.openmap.gui.OpenMapFrame;
import com.bbn.openmap.gui.OverlayMapPanel;

/**
 * 
 * @author snpe
 *
 */
public abstract class AbstractMain extends OpenMapFrame {

	private static final long serialVersionUID = 1L;
	private static final ServiceLoader<LookAndFeel> LOOK_AND_FEEL_LOADER = ServiceLoader.load(LookAndFeel.class); 
	
	protected ZoomSupport zoomSupport;
	
	protected JPanel mainPanel;
	protected JPanel plotPanel;
	protected JPanel leftPanel;
	protected JPanel topPanel;
	protected JPanel bottomPanel;
	protected JSplitPane mainSplitPane;
	protected JSplitPane leftSplitPane;
	protected JPanel timePanel;
	protected JPanel narrativePanel;
	protected JPanel layersPanel;
	
	protected List<Action> actions;

	protected MapBean mapBean;
	protected OverlayMapPanel map;
	protected String lookAndFeel;
	protected ButtonGroup lookAndFeelRadioGroup;
	protected FitToWindowAction fitToWindow;
	private PanAction panAction;
	private ZoomInAction zoomInAction;
	private ZoomOutAction zoomOutAction;
	private RangeBearingAction rangeBearingAction;
	@SuppressWarnings("unused")
	private RedrawAction redrawAction;

	protected static void setBaseLookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public AbstractMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Debrief Lite");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height *= 0.9;
		screenSize.width *= 0.9;
		setPreferredSize(screenSize);
		setVisible(true);
		configurePanels();
		zoomSupport = new ZoomSupport(this);
		actions = createActions();
		createMap();
		configureActions();
		setJMenuBar(createMenuBar(actions));
		createToolBar();
	}

	/**
	 * 
	 */
	private void configureActions() {
		panAction.setMap(map);
		zoomInAction.setZoomDelegate(zoomSupport);
		zoomOutAction.setZoomDelegate(zoomSupport);
		fitToWindow.setMap(map);
		rangeBearingAction.setMap(map);
	}

	protected List<Action> createActions() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(panAction = new PanAction());
		actions.add(zoomInAction = new ZoomInAction());
		actions.add(zoomOutAction = new ZoomOutAction());
		actions.add(fitToWindow = new FitToWindowAction());
		actions.add(rangeBearingAction = new RangeBearingAction());
		actions.add(redrawAction = new RedrawAction());
		return actions;
	}

	protected abstract void createMap();

	protected void configurePanels() {
		mainPanel = createPanel();
		mainPanel.setOpaque(true);
		setContentPane(mainPanel);
		
		plotPanel = createPanel();
		plotPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		leftPanel = createPanel();
		
		mainSplitPane = createSplitPane(JSplitPane.HORIZONTAL_SPLIT, 250,
				leftPanel, plotPanel);

        topPanel = createPanel();
		bottomPanel = createPanel();
		
		leftSplitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT, 250,
				topPanel, bottomPanel);

        leftPanel.add(leftSplitPane, BorderLayout.CENTER);
		
		mainPanel.add(mainSplitPane, BorderLayout.CENTER);
		
		JTabbedPane topTabbedPane = new JTabbedPane();
		layersPanel = createPanel();
		topTabbedPane.add("Layers", layersPanel);
		
		timePanel = createPanel();
		topTabbedPane.add("Time", timePanel);
		topPanel.add(topTabbedPane);
		
		JTabbedPane bottomTabbedPane = new JTabbedPane();
		narrativePanel = createPanel();
		bottomTabbedPane.add("Narrative", narrativePanel);
		bottomPanel.add(bottomTabbedPane);
	}
	
	protected void createToolBar() {
		JToolBar toolBar = new JToolBar("Debrief toolbar");
		add(toolBar, BorderLayout.PAGE_START);
		
		for (Action action:actions) {
			addButton(toolBar, action);
		}
	}

	private void addButton(JToolBar toolBar, Action action) {
		JButton button = new JButton(action);
		if (button.getIcon() != null) {
			button.setText("");
		}
		toolBar.add(button);
	}

	protected JMenuBar createMenuBar(List<Action> actions) {
	    
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        
        // Quit action
        JMenuItem quitItem = new JMenuItem();
        quitItem.setAction(new QuitAction());
        fileMenu.add(quitItem);
       
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        viewMenu.add(createLookAndFeelMenu());
        menuBar.add(viewMenu);
        
        for (Action action:actions) {
        	addItem(viewMenu, action);
        }
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        
        // About action
        JMenuItem aboutItem = new JMenuItem();
        aboutItem.setAction(new AboutAction());
        helpMenu.add(aboutItem);

        return menuBar;
    }

	// copied from SwingSet3
	private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Set Look And Feel");
        menu.setName("lookAndFeel");
        
        // Look for toolkit look and feels first
        UIManager.LookAndFeelInfo lookAndFeelInfos[] = UIManager.getInstalledLookAndFeels();
        lookAndFeel = UIManager.getLookAndFeel().getClass().getName();
        lookAndFeelRadioGroup = new ButtonGroup();
        for(UIManager.LookAndFeelInfo lafInfo: lookAndFeelInfos) {
            menu.add(createLookAndFeelItem(lafInfo.getName(), lafInfo.getClassName()));
        }  
        // Now load any look and feels defined externally as service via java.util.ServiceLoader
        LOOK_AND_FEEL_LOADER.iterator();
        for (LookAndFeel laf : LOOK_AND_FEEL_LOADER) {           
            menu.add(createLookAndFeelItem(laf.getName(), laf.getClass().getName()));
        }
         
        return menu;
    }
	
	public void setLookAndFeel(String lookAndFeel)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {

		String oldLookAndFeel = this.lookAndFeel;

		if (oldLookAndFeel != lookAndFeel) {
			UIManager.setLookAndFeel(lookAndFeel);
			this.lookAndFeel = lookAndFeel;
			updateLookAndFeel();
			firePropertyChange("lookAndFeel", oldLookAndFeel, lookAndFeel);
		}
	}
	
	private void updateLookAndFeel() {
        Window windows[] = Frame.getWindows();

        for(Window window : windows) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }
	
	private JRadioButtonMenuItem createLookAndFeelItem(String lafName, final String lafClassName) {
        JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem();

        lafItem.setSelected(lafClassName.equals(lookAndFeel));
        lafItem.setHideActionText(true);
        Action action = new AbstractAction() {
			
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					setLookAndFeel(lafClassName);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
        lafItem.setAction(action);
        lafItem.setText(lafName);
        lafItem.setActionCommand(lafClassName);
        lookAndFeelRadioGroup.add(lafItem);
        
        return lafItem;
    }
	// end copy from SwingSet3
	
	private void addItem(JMenu viewMenu, Action action) {
		JMenuItem item = new JMenuItem(action);
        item.setIcon(null);
        viewMenu.add(item);
	}

	private JSplitPane createSplitPane(int orientation, int dividerLocation, JPanel leftPanel, JPanel rightPanel) {
		JSplitPane pane = new JSplitPane(orientation, leftPanel, rightPanel);
		
		pane.setContinuousLayout(true);
        pane.setOneTouchExpandable(true);
        pane.setDividerLocation(dividerLocation);
        rightPanel.setMinimumSize(new Dimension(20, 20));
        leftPanel.setMinimumSize(new Dimension(20, 20));
        return pane;
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		return panel;
	}

}
