package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.ZoneId;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.core.utils.RunnableStub;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

public class RobotUIService implements RobotStateListener {
	private final AppServiceLocator serviceLocator;
	private RobotState state;
	private Runnable chartsViewUpdateConfig, chartsViewUpdateAll;
	private ChartsView chartsView;
	
	public RobotUIService(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
		this.chartsViewUpdateConfig = RunnableStub.getInstance();
		this.chartsViewUpdateAll = RunnableStub.getInstance();
	}
	
	public void initialize(RobotState state) {
		this.state = state;
		UIService uis = serviceLocator.getUIService();
		
		chartsView = new ChartsView(serviceLocator, state);
		chartsViewUpdateConfig = new Runnable() { public void run() { chartsView.updateConfigView(); } };
		chartsViewUpdateConfig = new Runnable() { public void run() { chartsView.updateView(); } };
		
		uis.getTabPanel().addTab("Test", chartsView);
	}

	@Override
	public void robotStarted() {
		
	}
	
	@Override
	public void accountSelected() {
		
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
	public void robotStopped() {

	}
	
	@Override
	public void speculationOpened() {
		
	}

	@Override
	public void speculationClosed() {
		
	}

	@Override
	public void speculationUpdate() {
		
	}

}
