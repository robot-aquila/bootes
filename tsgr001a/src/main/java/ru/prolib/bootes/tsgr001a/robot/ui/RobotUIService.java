package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

public class RobotUIService implements RobotStateListener {
	private final AppServiceLocator serviceLocator;
	private RobotState state;
	private JPanel rootPanel, t0Panel, t1Panel, t2Panel;
	private JSplitPane extSplitPanel, extBotSplitPanel, intBotSplitPanel;
	private SecurityChartPanel t0,t1,t2;
	private StrategyConfigPanel cfg;
	
	public RobotUIService(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
	
	private void updateChart(SecurityChartPanel panel, STSeries ss) {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateChart(panel, ss);
				}
			});
		} else {
			if ( ss != null ) {
				panel.update(ss, state.getSecurity());
			} else {
				panel.clear();
			}
			panel.paint();
		}
	}
	
	private void updateConfigPanel() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateConfigPanel();
				}
			});
		} else {
			cfg.updateView();
		}
	}
	
	public void initialize(RobotState state) {
		this.state = state;
		UIService uis = serviceLocator.getUIService();
		
		t0 = new ChartT0();
		t1 = new ChartT1();
		t2 = new ChartT2();
		cfg = new StrategyConfigPanel(serviceLocator.getMessages(), state, uis.getZoneID());
		
		double top_weight = 0.8d, bot_charts_weight = 0.8d, cfg_weight = 0.2d;
		
		intBotSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		intBotSplitPanel.setLeftComponent(t1Panel = t1.create());
		intBotSplitPanel.setRightComponent(t2Panel = t2.create());
		intBotSplitPanel.setDividerLocation(0.5d);
		intBotSplitPanel.setOneTouchExpandable(true);
		intBotSplitPanel.setResizeWeight(0.5d);
		
		extBotSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		extBotSplitPanel.setLeftComponent(intBotSplitPanel);
		extBotSplitPanel.setRightComponent(cfg);
		extBotSplitPanel.setOneTouchExpandable(true);
		extBotSplitPanel.setDividerLocation(bot_charts_weight);
		extBotSplitPanel.setResizeWeight(1.0d);
		
		extSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		extSplitPanel.setTopComponent(t0Panel = t0.create());
		extSplitPanel.setBottomComponent(extBotSplitPanel);
		extSplitPanel.setOneTouchExpandable(true);
		extSplitPanel.setDividerLocation(top_weight);
		extSplitPanel.setResizeWeight(top_weight);
		
		rootPanel = new JPanel(new GridLayout(1, 1));
		rootPanel.add(extSplitPanel);
		
		// Dimension pd = uis.getTabPanel().getSize(); // this does not work
		// Dimension pd = uis.getMainPanel().getSize(); // also does not work
		// let's imagine virtual display 1024x768
		
		int d_w = 1024, d_h = 768;
		int t_w = d_w;
		int b3_w = (int)(cfg_weight * d_w);
		int b2_w = (d_w - b3_w) / 2;
		int b1_w = b2_w;
		int t_h = (int)(top_weight * d_h);
		int b_h = d_h - t_h;

		t0Panel.setPreferredSize(new Dimension(t_w, t_h));
		t1Panel.setPreferredSize(new Dimension(b1_w, b_h));
		t2Panel.setPreferredSize(new Dimension(b2_w, b_h));
		cfg.setPreferredSize(new Dimension(b3_w, b_h));
		
		uis.getTabPanel().addTab("Test", rootPanel);
	}

	@Override
	public void robotStarted() {
		
	}
	
	@Override
	public void accountSelected() {
		
	}

	@Override
	public void contractSelected() {
		updateConfigPanel();
	}
	
	@Override
	public void sessionDataAvailable() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					sessionDataAvailable();
				}
			});
		} else {
			updateConfigPanel();
			updateChart(t0, state.getSeriesHandlerT0().getSeries());
			updateChart(t1, state.getSeriesHandlerT1().getSeries());
			updateChart(t2, state.getSeriesHandlerT2().getSeries());
		}
	}
	
	@Override
	public void positionParamsUpdated() {
		updateConfigPanel();
	}

	@Override
	public void sessionDataCleanup() {
		updateConfigPanel();
		// Do not clear to avoid blinking
		// Do not repaint to keep chart data on screen
	}

	@Override
	public void robotStopped() {

	}

}
