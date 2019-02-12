package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.bootes.lib.report.blockrep.BlockReportStorage;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3Report;
import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.report.summarep.SummaryReportTracker;

public class RoboServiceLocator {
	private final ISummaryReportTracker srt;
	private final IBlockReportStorage brs;
	private final IS3Report s3r;
	
	public RoboServiceLocator() {
		srt = new SummaryReportTracker();
		brs = new BlockReportStorage();
		s3r = new S3Report();
	}
	
	public ISummaryReportTracker getSummaryReportTracker() {
		return srt;
	}
	
	public IBlockReportStorage getBlockReportStorage() {
		return brs;
	}
	
	public IS3Report getS3Report() {
		return s3r;
	}
	
}
