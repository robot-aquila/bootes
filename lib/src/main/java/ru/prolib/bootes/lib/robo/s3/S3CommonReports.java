package ru.prolib.bootes.lib.robo.s3;

import java.time.ZoneId;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3Report;
import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.report.summarep.SummaryReportTracker;
import ru.prolib.bootes.lib.robo.rh.EquityCurveReportHandler;
import ru.prolib.bootes.lib.robo.s3.rh.S3ReportHandler;
import ru.prolib.bootes.lib.robo.s3.rh.S3SummaryReportHandler;

/**
 * Service provides set of reports of typical  speculative strategy.
 */
public class S3CommonReports {
	private final ISummaryReportTracker srt;
	private final IS3Report atr;
	private final OHLCScalableSeries erc, erf;
	
	public S3CommonReports(AppServiceLocator serviceLocator) {
		ZoneId zone_id = serviceLocator.getZoneID();
		EventQueue queue = serviceLocator.getEventQueue();
		srt = new SummaryReportTracker();
		atr = new S3Report();
		erc = new OHLCScalableSeries(queue, "EQUITY_CURVE_COMP",   50, zone_id);
		erf = new OHLCScalableSeries(queue, "EQUITY_CURVE_FULL", 1000, zone_id);
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
	
	public void registerHandlers(S3RobotState state) {
		 S3RobotStateListenerComp stateListener = state.getStateListener();
		stateListener.addListener(new S3SummaryReportHandler(state, srt));
		stateListener.addListener(new S3ReportHandler(state, atr));
		stateListener.addListener(new EquityCurveReportHandler(state, erf));
		stateListener.addListener(new EquityCurveReportHandler(state, erc));
	}
	
}
