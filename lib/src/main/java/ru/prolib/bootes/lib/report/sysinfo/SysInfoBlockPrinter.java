package ru.prolib.bootes.lib.report.sysinfo;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import ru.prolib.bootes.lib.report.IReportBlockPrinter;

public class SysInfoBlockPrinter implements IReportBlockPrinter {
	public static final String DEFAULT_TITLE = "Default";
	public static final String REPORT_ID = "SysInfoReport_v0.1.0";
	private final String title;
	private final DateTimeFormatter dtf;
	private final SysInfoReportHandler handler;
	
	public SysInfoBlockPrinter(SysInfoReportHandler handler, String title, ZoneId zoneID) {
		this.handler = handler;
		this.title = title;
		this.dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(zoneID);
	}
	
	public SysInfoBlockPrinter(SysInfoReportHandler handler) {
		this(handler, DEFAULT_TITLE, ZoneId.of("Europe/Moscow"));
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
		SysInfoReport report = handler.getReport();
		String na = "N/A";
		Instant started = report.getJobStarted(), finished = report.getJobFinished();
		stream.println(" Job started: " + (started == null ? na : dtf.format(started)));
		stream.println("Job finished: " + (finished == null ? na : dtf.format(finished)));
		stream.println("  Time spent: " + (started == null || finished == null
				? na : ChronoUnit.SECONDS.between(started, finished) + " seconds"));
	}

}
