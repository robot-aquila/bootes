package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.ZoneId;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.report.s3rep.ui.S3ReportCounterView;
import ru.prolib.bootes.lib.report.s3rep.ui.S3ReportTableModel;
import ru.prolib.bootes.lib.report.summarep.ui.SummaryReportView;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.ui.BOOTESCommonMsg;
import ru.prolib.bootes.tsgr001a.robot.RoboServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class ReportsView extends JPanel {
	private static final long serialVersionUID = 1L;
	private final RoboServiceLocator roboServices;
	private SummaryReportView statsPanel;
	private JTabbedPane reportsTabPanel;
	private JSplitPane splitPane;
	
	public ReportsView(AppServiceLocator serviceLocator, RoboServiceLocator roboServices, RobotState state) {
		super(new GridLayout(1, 1));
		this.roboServices = roboServices;
		IMessages messages = serviceLocator.getMessages();
		UIService uis = serviceLocator.getUIService();
		ZoneId zone_id = uis.getZoneID();
		JFrame main_frame = uis.getFrame();
		
		statsPanel = new SummaryReportView(messages);
		reportsTabPanel = new JTabbedPane();
		
		S3ReportTableModel rtm = new S3ReportTableModel(messages, zone_id, roboServices.getTradesReport());
		JTable table = new JTable(rtm);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(rtm));
		JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.add(new JScrollPane(table));
		new TableModelController(rtm, main_frame);
		reportsTabPanel.addTab(messages.get(BOOTESCommonMsg.REPORT_ALL_TRADES), panel);
		
		rtm = new S3ReportTableModel(messages, zone_id, roboServices.getShortDurationTradesReport());
		table = new JTable(rtm);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(rtm));
		panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		panel.add(new S3ReportCounterView(messages, roboServices.getShortDurationTradesReport()), BorderLayout.PAGE_END);
		new TableModelController(rtm, main_frame);
		reportsTabPanel.addTab(messages.get(BOOTESCommonMsg.REPORT_SHORT_DURATION_TRADES), panel);
		
		rtm = new S3ReportTableModel(messages, zone_id, roboServices.getMidDayClearingTradesReport());
		table = new JTable(rtm);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(rtm));
		panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		panel.add(new S3ReportCounterView(messages, roboServices.getMidDayClearingTradesReport()), BorderLayout.PAGE_END);
		new TableModelController(rtm, main_frame);
		reportsTabPanel.addTab(messages.get(BOOTESCommonMsg.REPORT_CROSS_MIDCLEARING_TRADES), panel);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(statsPanel);
		splitPane.setRightComponent(reportsTabPanel);
		splitPane.setDividerLocation(0.2d);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.2d);
		
		add(splitPane);
	}
	
	public void updateView() {
		statsPanel.update(roboServices.getSummaryReportTracker().getCurrentStats());
	}

}
