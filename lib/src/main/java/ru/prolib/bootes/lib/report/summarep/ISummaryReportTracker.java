package ru.prolib.bootes.lib.report.summarep;

public interface ISummaryReportTracker {
	void add(SREntry sr);
	ISummaryReport getCurrentStats();
}
