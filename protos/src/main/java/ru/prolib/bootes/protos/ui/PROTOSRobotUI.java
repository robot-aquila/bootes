package ru.prolib.bootes.protos.ui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.utils.RunnableStub;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.s3.S3CommonReports;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.robo.s3.ui.S3CommonReportsView;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.ui.BOOTESCommonMsg;
import ru.prolib.bootes.lib.ui.EquityCurveView;
import ru.prolib.bootes.protos.PROTOSRobotState;

public class PROTOSRobotUI implements S3RobotStateListener  {
	private final AppServiceLocator serviceLocator;
	private final PROTOSRobotState state;
	private final S3CommonReports reports;
	private Runnable chartsViewUpdateAll, reportsViewUpdateAll;
	private PROTOSChartsView chartsView;
	private S3CommonReportsView reportsView;
	private EquityCurveView equityView;
	
	public PROTOSRobotUI(AppServiceLocator serviceLocator,
						 PROTOSRobotState state,
						 S3CommonReports reports)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
		this.reports = reports;
		this.chartsViewUpdateAll = RunnableStub.getInstance();
		this.reportsViewUpdateAll = RunnableStub.getInstance();
	}
	
	private void initialize() {
		UIService uis = serviceLocator.getUIService();
		IMessages messages = serviceLocator.getMessages();
		
		chartsView = new PROTOSChartsView(serviceLocator, state);
		chartsViewUpdateAll = new Runnable() { public void run() { chartsView.updateView(); } };
		
		reportsView = new S3CommonReportsView(serviceLocator, reports);
		reportsViewUpdateAll = new Runnable() { public void run() { reportsView.updateView(); } };
		reportsView.updateView();
		
		equityView = new EquityCurveView(reports.getEquityReport());
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(messages.get(BOOTESCommonMsg.CHARTS), chartsView);
		tabs.addTab(messages.get(BOOTESCommonMsg.REPORTS), reportsView);
		tabs.addTab(messages.get(BOOTESCommonMsg.EQUITY_CURVE), equityView);
		
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.X_AXIS));
		root.add(tabs, BorderLayout.CENTER);
		
		uis.getTabPanel().addTab("PROTOS", root);
	}

	@Override
	public void robotStarted() {
		SwingUtilities.invokeLater(new Runnable() { public void run() { initialize(); } });
	}

	@Override
	public void accountSelected() {
		
	}

	@Override
	public void contractSelected() {
		
	}

	@Override
	public void sessionDataAvailable() {
		SwingUtilities.invokeLater(chartsViewUpdateAll);		
	}

	@Override
	public void riskManagementUpdate() {
		
	}

	@Override
	public void sessionDataCleanup() {
		// Do not clear to avoid blinking
		// Do not repaint to keep chart data on screen
	}

	@Override
	public void robotStopped() {
		
	}

	@Override
	public void speculationOpened() {
		SwingUtilities.invokeLater(reportsViewUpdateAll);
	}

	@Override
	public void speculationUpdate() {
		SwingUtilities.invokeLater(reportsViewUpdateAll);
	}

	@Override
	public void speculationClosed() {
		SwingUtilities.invokeLater(reportsViewUpdateAll);
	}

}
