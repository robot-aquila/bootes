package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.ZoneId;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class ChartsView extends JPanel {
	private static final long serialVersionUID = 1L;
	private final RobotState state;
	private final JPanel t0Panel, t1Panel, t2Panel;
	private final JSplitPane extSplitPanel, extBotSplitPanel, intBotSplitPanel;
	private final SecurityChartPanel t0,t1,t2;
	private final StrategyConfigPanel cfg;

	public ChartsView(AppServiceLocator serviceLocator, RobotState state) {
		super(new GridLayout(1, 1));
		this.state = state;
		UIService uis = serviceLocator.getUIService();
		ZoneId zoneID = uis.getZoneID();
		
		t0 = new ChartT0(zoneID, state.getReportStorage());
		t1 = new ChartT1(zoneID);
		t2 = new ChartT2(zoneID);
		cfg = new StrategyConfigPanel(serviceLocator.getMessages(), state, zoneID);
		
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
		
		add(extSplitPanel);
		
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
	}
	
	public void updateConfigView() {
		cfg.updateView();
	}
	
	public void updateChartViews() {
		updateChartView(t0, state.isSeriesHandlerT0Defined() ? state.getSeriesHandlerT0().getSeries() : null);
		updateChartView(t1, state.isSeriesHandlerT1Defined() ? state.getSeriesHandlerT1().getSeries() : null);
		updateChartView(t2, state.isSeriesHandlerT2Defined() ? state.getSeriesHandlerT2().getSeries() : null);
	}
	
	public void updateView() {
		updateConfigView();
		updateChartViews();
	}
	
	private void updateChartView(SecurityChartPanel panel, STSeries ss) {
		if ( ss != null ) {
			panel.update(ss, state.getSecurity());
		} else {
			//panel.clear(); // Do not clear to avoid blocking
		}
		panel.paint();
	}
	
}
