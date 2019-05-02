package ru.prolib.bootes.lib.report.hello;

import java.io.PrintStream;

import ru.prolib.bootes.lib.report.IReportBlockPrinter;

public class HelloBlockPrinter implements IReportBlockPrinter {
	public static final String DEFAULT_TITLE = "Default";
	public static final String REPORT_ID = "Hello_v0.1.0";
	private final String title, text;
	
	public HelloBlockPrinter(String title, String text) {
		this.title = title;
		this.text = text;
	}
	
	public HelloBlockPrinter(String text) {
		this(DEFAULT_TITLE, text);
	}
	
	@Override
	public String getReportID() {
		return REPORT_ID;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void print(PrintStream stream) {
		stream.println(text);
	}

}
