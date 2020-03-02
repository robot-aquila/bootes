package ru.prolib.bootes.lib.robo.s3;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;

import org.apache.commons.io.FileUtils;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.report.ReportPrinter;
import ru.prolib.bootes.lib.report.order.OrderReport;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3Report;
import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.report.summarep.SummaryReportTracker;
import ru.prolib.bootes.lib.report.sysinfo.SysInfoBlockPrinter;
import ru.prolib.bootes.lib.report.sysinfo.SysInfoReportHandler;
import ru.prolib.bootes.lib.robo.s3.rh.EquityCurveReportHandler;
import ru.prolib.bootes.lib.robo.s3.rh.OrderReportHandler;
import ru.prolib.bootes.lib.robo.s3.rh.S3ReportHandler;
import ru.prolib.bootes.lib.robo.s3.rh.S3SummaryReportHandler;

/**
 * Service provides set of reports of typical speculative strategy.
 */
public class S3CommonReports {
	private final AppServiceLocator serviceLocator;
	private final SysInfoReportHandler sysInfoHandler;
	private final ISummaryReportTracker srt;
	private final IS3Report atr;
	private final OHLCScalableSeries erc, erf;
	private final OrderReport orderReport;
	private String header;
	
	public S3CommonReports(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
		ZoneId zone_id = serviceLocator.getZoneID();
		EventQueue queue = serviceLocator.getEventQueue();
		srt = new SummaryReportTracker();
		atr = new S3Report();
		erc = new OHLCScalableSeries(queue, "EQUITY_CURVE_COMP",   50, zone_id);
		erf = new OHLCScalableSeries(queue, "EQUITY_CURVE_FULL", 1000, zone_id);
		sysInfoHandler = new SysInfoReportHandler();
		orderReport = new OrderReport();
	}
	
	public ISummaryReportTracker getSummaryReportTracker() {
		return srt;
	}
	
	public IS3Report getTradesReport() {
		return atr;
	}
	
	public OHLCScalableSeries getEquityReportCompact() {
		return erc;
	}
	
	public OHLCScalableSeries getEquityReport() {
		return erf;
	}
	
	public OrderReport getOrderReport() {
		return orderReport;
	}
	
	public synchronized void setHeader(String text) {
		this.header = text;
	}
	
	public void registerHandlers(S3RobotState state) {
		S3RobotStateListenerComp stateListener = state.getStateListener();
		stateListener.addListener(new S3SummaryReportHandler(state, srt));
		stateListener.addListener(new S3ReportHandler(state, atr));
		stateListener.addListener(new EquityCurveReportHandler(state, erf));
		stateListener.addListener(new EquityCurveReportHandler(state, erc));
		stateListener.addListener(sysInfoHandler);
		stateListener.addListener(new OrderReportHandler(orderReport));
	}
	
	public void save(File report_dir, String filename) throws IOException {
		String hello = null;
		synchronized ( this ) {
			hello = header;
		}
		FileUtils.forceMkdir(report_dir);
		File report_file = new File(report_dir, filename);
		ReportPrinter printer = new ReportPrinter(serviceLocator.getZoneID());
		if ( hello != null ) {
			printer.addHello(hello);
		}
		printer.add(new SysInfoBlockPrinter(sysInfoHandler))
			.add(srt.getCurrentStats(), "Summary")
			.add(atr, "Trades")
			.add(erc, "Equity")
			.add(orderReport, "Orders")
			.save(report_file);
	}
	
}
