package ru.prolib.bootes.lib.report;

import java.io.PrintStream;

public interface IReportBlockPrinter {
	String getReportID();
	@Deprecated
	String getTitle();
	void print(PrintStream stream);
}
