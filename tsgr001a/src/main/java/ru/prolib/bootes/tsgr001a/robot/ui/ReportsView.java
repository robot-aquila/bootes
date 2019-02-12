package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.report.s3rep.ui.S3ReportTableModel;
import ru.prolib.bootes.lib.report.summarep.ui.SummaryReportView;
import ru.prolib.bootes.lib.service.UIService;
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
		IMessages messages = serviceLocator.getMessages();
		UIService uis = serviceLocator.getUIService();
		
		statsPanel = new SummaryReportView(messages);
		
		S3ReportTableModel rtm = new S3ReportTableModel(messages, uis.getZoneID(), roboServices.getS3Report());
		JTable table = new JTable(rtm);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(rtm));
		reportPanel = new JPanel(new GridLayout(1, 1));
		reportPanel.add(new JScrollPane(table));
		new TableModelController(rtm, uis.getFrame());
		
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
