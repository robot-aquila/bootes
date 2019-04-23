package ru.prolib.bootes.lib.robo.s3.ui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.ui.S3ReportTableModel;
import ru.prolib.bootes.lib.report.summarep.ui.SummaryReportView;
import ru.prolib.bootes.lib.robo.s3.S3CommonReports;

public class S3CommonReportsView extends JPanel {
	private static final long serialVersionUID = 1L;
	private final AppServiceLocator serviceLocator;
	private final S3CommonReports reports;
	private final JSplitPane split;
	private SummaryReportView summaryPanel;
	
	public S3CommonReportsView(AppServiceLocator serviceLocator,
							   S3CommonReports reports)
	{
		super(new GridLayout(1, 1));
		this.serviceLocator = serviceLocator;
		this.reports = reports;
		
		double lpw = getLeftPanelWeight();
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(createLeftPanel());
		split.setRightComponent(createRightPanel());
		split.setDividerLocation(lpw);
		split.setOneTouchExpandable(true);
		split.setResizeWeight(lpw);
		
		add(split);
	}
	
	protected double getLeftPanelWeight() {
		return 0.2d;
	}
	
	protected Component createLeftPanel() {
		return createSummaryPanel();
	}
	
	protected Component createRightPanel() {
		return createS3ReportPanel(reports.getTradesReport());
	}
	
	protected JPanel createS3ReportPanel(IS3Report report) {
		S3ReportTableModel rtm = new S3ReportTableModel(
				serviceLocator.getMessages(),
				serviceLocator.getZoneID(),
				reports.getTradesReport()
			);
		JTable table = new JTable(rtm);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(rtm));
		JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.add(new JScrollPane(table));
		new TableModelController(rtm, serviceLocator.getUIService().getFrame());
		return panel;
	}
	
	protected SummaryReportView createSummaryPanel() {
		 summaryPanel = new SummaryReportView(serviceLocator.getMessages());
		 return summaryPanel;
	}

	public void updateView() {
		if ( summaryPanel != null ) {
			summaryPanel.update(reports.getSummaryReportTracker().getCurrentStats());
		}
	}
	
}
