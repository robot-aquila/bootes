package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.bootes.lib.report.blockrep.BlockReportStorage;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.report.summarep.SummaryReportTracker;

public class RoboServiceLocator {
	private final ISummaryReportTracker srt;
	private final IBlockReportStorage brs;
	
	public RoboServiceLocator() {
		srt = new SummaryReportTracker();
		brs = new BlockReportStorage();
	}
	
	public ISummaryReportTracker getSummaryReportTracker() {
		return srt;
	}
	
	public IBlockReportStorage getBlockReportStorage() {
		return brs;
	}
	
}
