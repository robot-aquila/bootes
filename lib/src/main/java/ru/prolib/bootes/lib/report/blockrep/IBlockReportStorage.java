package ru.prolib.bootes.lib.report.blockrep;

import java.util.List;

import org.threeten.extra.Interval;

public interface IBlockReportStorage {
	List<IBlockReport> getReports(Interval interval);
	void addReport(IBlockReport report);
}
