package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.utils.RunnableStub;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerStub;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.ui.BOOTESCommonMsg;
import ru.prolib.bootes.lib.ui.EquityCurveView;
import ru.prolib.bootes.tsgr001a.robot.TSGR001AReports;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class RobotUIService extends S3RobotStateListenerStub {
	private final AppServiceLocator serviceLocator;
	private final TSGR001AReports roboServices;
	private final RobotState state;
	private final String title;
	private Runnable chartsViewUpdateConfig, chartsViewUpdateAll, reportsViewUpdateAll;
	private ChartsView chartsView;
	private ReportsView reportsView;
	private EquityCurveView equityView;
	
	public RobotUIService(AppServiceLocator serviceLocator,
						  TSGR001AReports reports,
						  RobotState state,
						  String title)
	{
		this.serviceLocator = serviceLocator;
		this.roboServices = reports;
		this.state = state;
		this.title = title;
		this.chartsViewUpdateConfig = RunnableStub.getInstance();
		this.chartsViewUpdateAll = RunnableStub.getInstance();
		this.reportsViewUpdateAll = RunnableStub.getInstance();
	}
	
	private void initialize() {
		UIService uis = serviceLocator.getUIService();
		IMessages messages = serviceLocator.getMessages();
		
		chartsView = new ChartsView(serviceLocator, roboServices, state);
		chartsViewUpdateConfig = new Runnable() { public void run() { chartsView.updateConfigView(); } };
		chartsViewUpdateAll = new Runnable() { public void run() { chartsView.updateView(); } };
		
		reportsView = new ReportsView(serviceLocator, roboServices, state);
		reportsViewUpdateAll = new Runnable() { public void run() { reportsView.updateView(); } };
		reportsView.updateView();
		
		equityView = new EquityCurveView(roboServices.getEquityReport());
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(messages.get(BOOTESCommonMsg.CHARTS), chartsView);
		tabs.addTab(messages.get(BOOTESCommonMsg.REPORTS), reportsView);
		tabs.addTab(messages.get(BOOTESCommonMsg.EQUITY_CURVE), equityView);
		
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.X_AXIS));
		root.add(tabs, BorderLayout.CENTER);
		
		uis.getTabPanel().addTab(title, root);
	}

	@Override
	public void robotStarted() {
		SwingUtilities.invokeLater(new Runnable() { public void run() { initialize(); } });
	}

	@Override
	public void contractSelected() {
		SwingUtilities.invokeLater(chartsViewUpdateConfig);
	}
	
	@Override
	public void sessionDataAvailable() {
		SwingUtilities.invokeLater(chartsViewUpdateAll);
	}
	
	@Override
	public void riskManagementUpdate() {
		SwingUtilities.invokeLater(chartsViewUpdateConfig);
	}

	@Override
	public void sessionDataCleanup() {
		SwingUtilities.invokeLater(chartsViewUpdateConfig);
		// Do not clear to avoid blinking
		// Do not repaint to keep chart data on screen
	}
	
	@Override
	public void speculationOpened() {
		SwingUtilities.invokeLater(reportsViewUpdateAll);
	}

	@Override
	public void speculationClosed() {
		SwingUtilities.invokeLater(reportsViewUpdateAll);
	}

	@Override
	public void speculationUpdate() {
		SwingUtilities.invokeLater(reportsViewUpdateAll);
	}

}
