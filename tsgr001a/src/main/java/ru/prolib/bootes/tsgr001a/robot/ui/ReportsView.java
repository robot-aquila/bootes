package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.report.summarep.ui.SummaryReportView;
import ru.prolib.bootes.tsgr001a.robot.RoboServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class ReportsView extends JPanel {
	private static final long serialVersionUID = 1L;
	private final RoboServiceLocator roboServices;
	private SummaryReportView statsPanel;
	private JPanel reportPanel;
	private JSplitPane splitPane;
	
	public ReportsView(AppServiceLocator serviceLocator, RoboServiceLocator roboServices, RobotState state) {
		super(new GridLayout(1, 1));
		this.roboServices = roboServices;
		statsPanel = new SummaryReportView(serviceLocator.getMessages());
		reportPanel = new JPanel();
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(statsPanel);
		splitPane.setRightComponent(reportPanel);
		splitPane.setDividerLocation(0.2d);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.2d);
		
		add(splitPane);
	}
	
	public void updateView() {
		statsPanel.update(roboServices.getSummaryReportTracker().getCurrentStats());
	}

}