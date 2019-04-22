package ru.prolib.bootes.protos.ui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.utils.RunnableStub;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.protos.PROTOSRobotState;

public class PROTOSRobotUI implements S3RobotStateListener  {
	private final AppServiceLocator serviceLocator;
	private final PROTOSRobotState state;
	private Runnable chartsViewUpdateAll;
	private PROTOSChartsView chartsView;
	
	public PROTOSRobotUI(AppServiceLocator serviceLocator,
						 PROTOSRobotState state)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
		this.chartsViewUpdateAll = RunnableStub.getInstance();
	}
	
	private void initialize() {
		UIService uis = serviceLocator.getUIService();
		
		chartsView = new PROTOSChartsView(serviceLocator, state);
		chartsViewUpdateAll = new Runnable() { public void run() { chartsView.updateView(); } };
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Charts", chartsView);
		
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

	}

	@Override
	public void speculationUpdate() {

	}

	@Override
	public void speculationClosed() {

	}

}
