package ru.prolib.bootes.lib.report.msr2;

import java.util.List;

import org.threeten.extra.Interval;

public interface IReportStorage {
	List<IReport> getReports(Interval interval);
	void addReport(IReport report);
}
