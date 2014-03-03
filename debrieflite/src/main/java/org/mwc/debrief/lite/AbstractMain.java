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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

import org.mwc.debrief.lite.actions.AboutAction;
import org.mwc.debrief.lite.actions.CloseAction;
import org.mwc.debrief.lite.actions.FitToWindowAction;
import org.mwc.debrief.lite.actions.OpenAction;
import org.mwc.debrief.lite.actions.PanAction;
import org.mwc.debrief.lite.actions.QuitAction;
import org.mwc.debrief.lite.actions.RangeBearingAction;
import org.mwc.debrief.lite.actions.ZoomInAction;
import org.mwc.debrief.lite.actions.ZoomOutAction;
import org.mwc.debrief.lite.model.NarrativeEntry;
import org.mwc.debrief.lite.model.Temporal;
import org.mwc.debrief.lite.time.TimeController;
import org.mwc.debrief.lite.time.TimeEvent;
import org.mwc.debrief.lite.time.TimeListener;
import org.mwc.debrief.lite.time.TimeView;
import org.mwc.debrief.lite.views.NarrativeTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.CenterSupport;
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
	final static Logger logger = LoggerFactory.getLogger(AbstractMain.class);
	
	protected ZoomSupport zoomSupport;
	
	protected static JPanel mainPanel;
	protected static JPanel plotPanel;
	protected static JPanel leftPanel;
	protected static JPanel topPanel;
	protected static JPanel bottomPanel;
	protected static JSplitPane mainSplitPane;
	protected static JSplitPane leftSplitPane;
	protected static JPanel timePanel;
	public static JPanel narrativePanel;
	protected static JPanel layersPanel;
	
	protected List<Action> actions;

	protected static MapBean mapBean;
	protected static OverlayMapPanel map;
	protected String lookAndFeel;
	protected ButtonGroup lookAndFeelRadioGroup;
	private OpenAction openAction;
	private static TimeController timeController;
	public static TimeView timeView;
	public static JTable narrativeTable;
	public static FitToWindowAction fitToWindow;
	public static PanAction panAction;
	public static ZoomInAction zoomInAction;
	public static ZoomOutAction zoomOutAction;
	public static RangeBearingAction rangeBearingAction;
	//public static RedrawAction redrawAction;
	
	private static CenterSupport centerSupport;
	

	protected static void setBaseLookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			logger.error("L&F", e);
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
		timeController = new TimeController();
		configurePanels();
		zoomSupport = new ZoomSupport(this);
		actions = createActions();
		createMap();
		configureActions();
		setJMenuBar(createMenuBar(actions));
		createToolBar();
		centerSupport = new CenterSupport(map.getMapBean());
		centerSupport.add(map.getMapBean());
	}

	private void configureActions() {
		panAction.setMap(map);
		zoomInAction.setZoomDelegate(zoomSupport);
		zoomOutAction.setZoomDelegate(zoomSupport);
		fitToWindow.setMap(map);
		rangeBearingAction.setMap(map);
		openAction.setMap(map);
		setActionEnabled(false);
	}

	/**
	 * @param enabled
	 */
	public static void setActionEnabled(boolean enabled) {
		panAction.setEnabled(enabled);
		zoomInAction.setEnabled(enabled);
		zoomOutAction.setEnabled(enabled);
		fitToWindow.setEnabled(enabled);
		rangeBearingAction.setEnabled(enabled);
	}
	
	public static void setTimeViewEnabled(boolean enabled) {
		timeView.setTimeViewEnabled(enabled);
	}

	protected List<Action> createActions() {
		List<Action> actions = new ArrayList<Action>();
		openAction = new OpenAction();
		actions.add(panAction = new PanAction());
		actions.add(zoomInAction = new ZoomInAction());
		actions.add(zoomOutAction = new ZoomOutAction());
		actions.add(fitToWindow = new FitToWindowAction());
		actions.add(rangeBearingAction = new RangeBearingAction());
		//actions.add(redrawAction = new RedrawAction());
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
		
		mainSplitPane = createSplitPane(JSplitPane.HORIZONTAL_SPLIT, 370,
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
		
		createTimePanel(topTabbedPane);
		
		JTabbedPane bottomTabbedPane = new JTabbedPane();
		narrativePanel = createPanel();
		bottomTabbedPane.add("Narrative", narrativePanel);
		bottomPanel.add(bottomTabbedPane);
		
		createNarrativeTable(narrativePanel);
	}

	/**
	 * @return the plotPanel
	 */
	public static JPanel getPlotPanel() {
		return plotPanel;
	}

	private void createTimePanel(JTabbedPane topTabbedPane) {
		timePanel = createPanel();
		topTabbedPane.add("Time", timePanel);
		topPanel.add(topTabbedPane);
		timeView = new TimeView();
		JScrollPane scrollPane = new JScrollPane(timeView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		timePanel.add(scrollPane);
		
	}

	/**
	 * @param panel 
	 * 
	 */
	private void createNarrativeTable(JPanel panel) {
		narrativeTable = new JTable(new NarrativeTableModel(null));
		
		JScrollPane scrollPane = new JScrollPane(narrativeTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
        narrativeTable.setPreferredScrollableViewportSize(narrativeTable.getPreferredSize());
        panel.add(scrollPane);
        
		narrativeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//narrativeTable.setAutoCreateRowSorter(true);
		narrativeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		narrativeTable.getTableHeader().setResizingAllowed(true);
		DebriefMain.getTimeController().addTimeListener(new TimeListener() {
			
			@Override
			public void newTime(TimeEvent event) {
				if (event.getSource() == AbstractMain.this) {
					return;
				}
				NarrativeTableModel model = (NarrativeTableModel) narrativeTable.getModel();
				if (model.getRowCount() <= 0) {
					return;
				}
				long newTime = event.getTime();
				List<NarrativeEntry> entries = model.getNarrativeEntries();
				if (entries == null || entries.size() <= 0) {
					return;
				}
				int index = -1;
				long difference = Long.MAX_VALUE;
				for (NarrativeEntry entry:entries) {
					Temporal date = entry.getDate();
					if (date != null) {
						long time = date.getTime();
						long diff = Math.abs(time - newTime);
						if (diff < difference) {
							difference = diff;
							index = entries.indexOf(entry);
						}
					}
				}
				if (index > -1 && difference < 1*1000*60) {
					narrativeTable.setRowSelectionInterval(index, index);
				}
			}
			
			@Override
			public Object getSource() {
				return AbstractMain.this;
			}
		});
		
		narrativeTable.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		    	if (e.getClickCount() == 2) {
		    		Point p = e.getPoint();
		    		int row = narrativeTable.rowAtPoint(p);
		    		NarrativeTableModel model = (NarrativeTableModel) narrativeTable.getModel();
					List<NarrativeEntry> entries = model.getNarrativeEntries();
					if (entries != null && row >= 0 && entries.size() >= row) {
						NarrativeEntry entry = entries.get(row);
						if (entry != null && entry.getDate() != null) {
							DebriefMain.getTimeController().notifyListeners(new TimeEvent(entry.getDate().getTime(), AbstractMain.this));
						}
					}
		        }
		    }
		});
	}
	
	protected void createToolBar() {
		JToolBar toolBar = new JToolBar("Debrief toolbar");
		add(toolBar, BorderLayout.PAGE_START);
		
		addButton(toolBar, openAction);
		toolBar.addSeparator();
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
        
        // Open action
        JMenuItem openItem = new JMenuItem();
        openItem.setAction(openAction);
        fileMenu.add(openItem);
        
        // Close action
        JMenuItem closeItem = new JMenuItem();
        closeItem.setAction(new CloseAction(map));
        fileMenu.add(closeItem);
        
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
				} catch (Exception e1) {
					logger.error("L&F", e1);
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

	/**
	 * @return the centerSupport
	 */
	public static CenterSupport getCenterSupport() {
		return centerSupport;
	}

	/**
	 * @return the timeController
	 */
	public static TimeController getTimeController() {
		return timeController;
	}

	/**
	 * @return the map
	 */
	public static OverlayMapPanel getMap() {
		return map;
	}

}
