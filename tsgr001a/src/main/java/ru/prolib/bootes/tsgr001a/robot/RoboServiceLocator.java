package ru.prolib.bootes.tsgr001a.robot;

import java.time.LocalTime;
import java.time.ZoneId;

import ru.prolib.aquila.core.utils.LocalTimePeriod;
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
	
	public RoboServiceLocator(ZoneId zoneID) {
		srt = new SummaryReportTracker();
		brs = new BlockReportStorage();
		tradesReport = new S3Report();
		shortDurationTradesReport = new S3Report(new S3RShortDurationRecords(15L));
		midDayClearingTradesReport = new S3Report(new S3RCrossingIntradayPeriod(new LocalTimePeriod(
				LocalTime.of(14, 0),
				LocalTime.of(14, 5),
				zoneID
			)));
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
	
}
