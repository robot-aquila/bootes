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
import ru.prolib.bootes.lib.robo.s3.S3CommonReports;
import ru.prolib.bootes.lib.robo.s3.S3RobotState;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerComp;
import ru.prolib.bootes.lib.robo.s3.rh.S3ReportHandler;

public class TSGR001AReports extends S3CommonReports {
	private final IS3Report sdt, mdt;
	
	public TSGR001AReports(AppServiceLocator serviceLocator) {
		super(serviceLocator);
		ZoneId zone_id = serviceLocator.getZoneID();
		sdt = new S3Report(new S3RShortDurationRecords(15L));
		LocalTimePeriod mdc_p = new LocalTimePeriod(of(14, 0), of(14, 5), zone_id);
		mdt = new S3Report(new S3RCrossingIntradayPeriod(mdc_p));
	}
	
	public IS3Report getShortDurationTradesReport() {
		return sdt;
	}
	
	public IS3Report getMidDayClearingTradesReport() {
		return mdt;
	}
	
	public void registerHandlers(S3RobotState state) {
		super.registerHandlers(state);
		S3RobotStateListenerComp stateListener = state.getStateListener();
		stateListener.addListener(new S3ReportHandler(state, sdt));
		stateListener.addListener(new S3ReportHandler(state, mdt));
	}

}
