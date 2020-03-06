package ru.prolib.bootes.lib.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import ru.prolib.bootes.lib.report.equirep.EquityReportBlockPrinter;
import ru.prolib.bootes.lib.report.hello.HelloBlockPrinter;
import ru.prolib.bootes.lib.report.order.OrderReportComparator;
import ru.prolib.bootes.lib.report.order.OrderReportPrinter;
import ru.prolib.bootes.lib.report.s3rep.S3ReportBlockPrinter;
import ru.prolib.bootes.lib.report.summarep.SummaryReportBlockPrinter;
import ru.prolib.bootes.lib.report.sysinfo.SysInfoBlockPrinter;

public class ReportComparator {
	private static final ReportComparator instance = new ReportComparator();
	
	static void stub(Map<String, STRBComparator> comparators, String report_id) {
		comparators.put(report_id, new STRBComparatorStub(report_id));
	}
	
	static void dumb(Map<String, STRBComparator> comparators, String report_id) {
		comparators.put(report_id, new STRBComparatorDumb(report_id));
	}
	
	static STRComparator defaultComparator() {
		Map<String, STRBComparator> comparators = new HashMap<>();
		stub(comparators, SysInfoBlockPrinter.REPORT_ID);
		dumb(comparators, HelloBlockPrinter.REPORT_ID);
		dumb(comparators, EquityReportBlockPrinter.REPORT_ID);
		dumb(comparators, SummaryReportBlockPrinter.REPORT_ID);
		dumb(comparators, S3ReportBlockPrinter.REPORT_ID); // TODO: 
		comparators.put(OrderReportPrinter.REPORT_ID, new OrderReportComparator(OrderReportPrinter.REPORT_ID));
		return new STRComparatorImpl(comparators);
	}
	
	static TextLineReader createReader(File file) throws IOException {
		return new TextLineReaderBRImpl(new BufferedReader(new FileReader(file)));
	}
	
	public static ReportComparator getInstance() {
		return instance;
	}
	
	private final STRComparator comparator;
	
	public ReportComparator(STRComparator comparator) {
		this.comparator = comparator;
	}
	
	public ReportComparator() {
		this(defaultComparator());
	}
	
	public STRCmpResult compare(File expected, File actual) throws IOException, ParseException {
		try ( TextLineReader e_r = createReader(expected) ) {
			try ( TextLineReader a_r = createReader(actual) ) {
				return comparator.diff(e_r, a_r);
			}
		}
		
	}
	
}
