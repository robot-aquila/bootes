package ru.prolib.bootes.protos.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.ZoneId;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.report.blockrep.BlockReportStorage;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.robo.s3.rh.S3BlockReportHandler;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.protos.PROTOSDataHandler;
import ru.prolib.bootes.protos.PROTOSRobotState;

public class PROTOSChartsView extends JPanel {
	private static final long serialVersionUID = 1L;
	private final PROTOSRobotState state;
	private final JPanel t0Panel, t1Panel;
	private final JSplitPane splitPanel;
	private final SecurityChartPanel t0, t1;
	private final IBlockReportStorage blockReport;
	
	public PROTOSChartsView(AppServiceLocator serviceLocator,
							PROTOSRobotState state)
	{
		super(new GridLayout(1, 1));
		this.state = state;
		UIService uis = serviceLocator.getUIService();
		ZoneId zoneID = uis.getZoneID();
		blockReport = new BlockReportStorage();
		state.getStateListener().addListener(new S3BlockReportHandler(state, blockReport));
		
		t0 = new PROTOSChartT0(zoneID, blockReport);
		t1 = new PROTOSChartT1(zoneID);
		
		double left_weight = 0.8;
		
		splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPanel.setLeftComponent(t0Panel = t0.create());
		splitPanel.setRightComponent(t1Panel = t1.create());
		splitPanel.setDividerLocation(left_weight);
		splitPanel.setResizeWeight(0.5d);
		splitPanel.setOneTouchExpandable(true);
		add(splitPanel);
		
		int dw = 1024, dh = 768;
		int lw = (int)(dw * left_weight);
		int rw = dw - lw;
		t0Panel.setPreferredSize(new Dimension(lw, dh));
		t1Panel.setPreferredSize(new Dimension(rw, dh));
	}
	
	private void updateChartView(SecurityChartPanel panel, STSeries ss) {
		if ( ss != null ) {
			panel.update(ss, state.getSecurity());
		} else {
			//panel.clear(); // Do not clear to avoid blocking
		}
		panel.paint();
	}
	
	private STSeries gs(STSeriesHandler h) {
		return h == null ? null : h.getSeries();
	}
	
	public void updateChartViews() {
		PROTOSDataHandler dh = state.getSessionDataHandler();
		updateChartView(t0, gs(dh.getSeriesHandlerT0()));
		updateChartView(t1, gs(dh.getSeriesHandlerT1()));
	}
	
	public void updateView() {
		updateChartViews();
	}

}
