package ru.prolib.bootes.lib.report.blockrep;

import java.util.ArrayList;
import java.util.List;

import org.threeten.extra.Interval;

public class BlockReportStorage implements IBlockReportStorage {
	private final List<IBlockReport> reports;
	
	public BlockReportStorage(List<IBlockReport> reports) {
		this.reports = reports;
	}
	
	public BlockReportStorage() {
		this(new ArrayList<>());
	}

	@Override
	public synchronized List<IBlockReport> getReports(Interval interval) {
		List<IBlockReport> result = new ArrayList<>();
		for ( IBlockReport report : reports ) {
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
	public synchronized void addReport(IBlockReport report) {
		reports.add(report);
	}

}
