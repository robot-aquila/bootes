package ru.prolib.bootes.tsgr001a.robot;

import static java.time.LocalTime.*;
import java.time.ZoneId;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.aquila.core.utils.LocalTimePeriod;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.report.blockrep.BlockReportStorage;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3Report;
import ru.prolib.bootes.lib.report.s3rep.filter.S3RCrossingIntradayPeriod;
import ru.prolib.bootes.lib.report.s3rep.filter.S3RShortDurationRecords;
import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.report.summarep.SummaryReportTracker;

public class RoboServiceLocator {
	private final ISummaryReportTracker srt;
	private final IBlockReportStorage brs;
	private final IS3Report tradesReport, shortDurationTradesReport, midDayClearingTradesReport;
	private final OHLCScalableSeries eqr_s, eqr_l;
	
	public RoboServiceLocator(AppServiceLocator serviceLocator) {
		ZoneId zone_id = serviceLocator.getZoneID();
		EventQueue queue = serviceLocator.getEventQueue();
		srt = new SummaryReportTracker();
		brs = new BlockReportStorage();
		tradesReport = new S3Report();
		shortDurationTradesReport = new S3Report(new S3RShortDurationRecords(15L));
		LocalTimePeriod mdc_p = new LocalTimePeriod(of(14, 0), of(14, 5), zone_id);
		midDayClearingTradesReport = new S3Report(new S3RCrossingIntradayPeriod(mdc_p));
		eqr_l = new OHLCScalableSeries(queue, "EQUITY_CURVE_REPORT_L", 1000, zone_id);
		eqr_s = new OHLCScalableSeries(queue, "EQUITY_CURVE_REPORT_S", 50, zone_id);
	}
	
	public ISummaryReportTracker getSummaryReportTracker() {
		return srt;
	}
	
	public IBlockReportStorage getBlockReportStorage() {
		return brs;
	}
	
	public IS3Report getTradesReport() {
		return tradesReport;
	}
	
	public IS3Report getShortDurationTradesReport() {
		return shortDurationTradesReport;
	}
	
	public IS3Report getMidDayClearingTradesReport() {
		return midDayClearingTradesReport;
	}
	
	public OHLCScalableSeries getEquityCurveReportL() {
		return eqr_l;
	}
	
	public OHLCScalableSeries getEquityCurveReportS() {
		return eqr_s;
	}
	
}
