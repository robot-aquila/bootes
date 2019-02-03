package ru.prolib.bootes.lib.report.msr2;

import java.util.ArrayList;
import java.util.List;

import org.threeten.extra.Interval;

public class ReportStorage implements IReportStorage {
	private final List<IReport> reports;
	
	public ReportStorage(List<IReport> reports) {
		this.reports = reports;
	}
	
	public ReportStorage() {
		this(new ArrayList<>());
	}

	@Override
	public synchronized List<IReport> getReports(Interval interval) {
		List<IReport> result = new ArrayList<>();
		for ( IReport report : reports ) {
			for ( IBlock block : report.getBlocks() ) {
				if ( block.getTime() != null && interval.contains(block.getTime()) ) {
					result.add(report);
					break;
				}
			}
		}
		return result;
	}

	@Override
	public synchronized void addReport(IReport report) {
		reports.add(report);
	}

}
