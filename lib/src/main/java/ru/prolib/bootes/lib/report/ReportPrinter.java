package ru.prolib.bootes.lib.report;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.bootes.lib.report.equirep.EquityReportBlockPrinter;
import ru.prolib.bootes.lib.report.hello.HelloBlockPrinter;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3ReportBlockPrinter;
import ru.prolib.bootes.lib.report.summarep.ISummaryReport;
import ru.prolib.bootes.lib.report.summarep.SummaryReportBlockPrinter;

public class ReportPrinter {
	public static final String DEFAULT_TITLE = "Default";
	private final ZoneId zoneID;
	private final List<STRBHandler<IReportBlockPrinter>> blocks;
	
	ReportPrinter(ZoneId zoneID, List<STRBHandler<IReportBlockPrinter>> blocks) {
		this.zoneID = zoneID;
		this.blocks = blocks;
	}
	
	public ReportPrinter(ZoneId zoneID) {
		this(zoneID, new ArrayList<>());
	}
	
	public ReportPrinter() {
		this(ZoneId.systemDefault());
	}
	
	public ReportPrinter add(IReportBlockPrinter block_printer, String title) {
		blocks.add(new STRBHandler<>(block_printer.getReportID(), title, block_printer));
		return this;
	}
	
	public ReportPrinter add(IReportBlockPrinter block_printer) {
		blocks.add(new STRBHandler<>(block_printer.getReportID(), DEFAULT_TITLE, block_printer));
		return this;
	}
	
	public ReportPrinter add(IS3Report report, String title) {
		return add(new S3ReportBlockPrinter(report, title, zoneID), title);
	}
	
	public ReportPrinter add(ISummaryReport report, String title) {
		return add(new SummaryReportBlockPrinter(report, title, zoneID), title);
	}
	
	public ReportPrinter add(OHLCScalableSeries equity_report, String title) {
		return add(new EquityReportBlockPrinter(equity_report, title), title);
	}
	
	public ReportPrinter addHello(String text) {
		return add(new HelloBlockPrinter(text));
	}
	
	private void printBlock(PrintStream stream, STRBHandler<IReportBlockPrinter> block) {
		stream.println(new StringBuilder()
			.append("# ReportID=").append(block.getHeader().getReportID())
			.append(" Title=").append(block.getHeader().getTitle())
			.toString());
		block.getHandler().print(stream);
		stream.println();		
	}
	
	public void print(PrintStream stream) {
		for ( STRBHandler<IReportBlockPrinter> block : blocks ) {
			printBlock(stream, block);
		}
	}
	
	public void save(File file) throws IOException {
		try ( PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file))) ) {
			print(stream);
		}
	}

}
