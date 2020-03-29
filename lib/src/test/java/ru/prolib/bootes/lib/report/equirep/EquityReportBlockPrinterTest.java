package ru.prolib.bootes.lib.report.equirep;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.aquila.core.data.timeframe.ZTFHours;

public class EquityReportBlockPrinterTest {
	private static ZoneId zoneID = ZoneId.of("Europe/Moscow");
	
	static Instant ZT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(zoneID).toInstant();
	}
	
	private EventQueue queue;
	private OHLCScalableSeries report;
	private EquityReportBlockPrinter service;

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueFactory().createDefault();
		report = new OHLCScalableSeries(queue, "TEST", 10, zoneID);
		service = new EquityReportBlockPrinter(report, "Test Report");
	}
	
	@Test
	public void testCtor2() {
		assertSame(report, service.getReport());
		assertEquals("Test Report", service.getTitle());
		assertEquals("EquityReport_v0.1.0", service.getReportID());
	}
	
	@Test
	public void testCtor1() {
		service = new EquityReportBlockPrinter(report);
		assertSame(report, service.getReport());
		assertEquals("Default", service.getTitle());
		assertEquals("EquityReport_v0.1.0", service.getReportID());
	}
	
	@After
	public void tearDown() throws Exception {
		queue.shutdown();
	}

	@Test
	public void testPrint_Minutes() throws Exception {
		report.append(ofRUB2("12.34"), ZT("2019-04-07T00:00:00"));
		report.append(ofRUB2("12.80"), ZT("2019-04-07T00:00:00"));
		report.append(ofRUB2("11.95"), ZT("2019-04-07T00:00:00"));
		report.append(ofRUB2("10.12"), ZT("2019-04-07T00:01:00"));
		report.append(ofRUB2("15.10"), ZT("2019-04-07T00:02:00"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		service.print(new PrintStream(baos));
		
		List<String> actual = Arrays.asList(StringUtils.split(baos.toString(), System.lineSeparator()));
		List<String> expected = FileUtils.readLines(new File("fixture/equirep/equirep-printer-case1.txt"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPrint_Hours() throws Exception {
		report.append(ofRUB5("102.13025"), ZT("2019-04-07T00:00:00"));
		report.append(ofRUB5("104.70091"), ZT("2019-04-07T01:00:00"));
		report.append(ofRUB5("105.19882"), ZT("2019-04-07T02:00:00"));
		report.append(ofRUB5("106.00080"), ZT("2019-04-07T03:00:00"));
		report.append(ofRUB5("107.90012"), ZT("2019-04-07T04:00:00"));
		report.append(ofRUB5("108.71260"), ZT("2019-04-07T05:00:00"));
		report.append(ofRUB5("109.10644"), ZT("2019-04-07T06:00:00"));
		report.append(ofRUB5("110.56201"), ZT("2019-04-07T07:00:00"));
		report.append(ofRUB5("111.77129"), ZT("2019-04-07T08:00:00"));
		report.append(ofRUB5("112.40355"), ZT("2019-04-07T09:00:00"));
		report.append(ofRUB5("113.34960"), ZT("2019-04-07T10:00:00"));
		report.append(ofRUB5("114.20061"), ZT("2019-04-07T11:00:00"));
		report.append(ofRUB5("115.89012"), ZT("2019-04-07T12:00:00"));
		report.append(ofRUB5("116.38761"), ZT("2019-04-07T13:00:00"));
		assertEquals(new ZTFHours(1, zoneID), report.getTimeFrame());
		assertEquals(14, report.getLength());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		service.print(new PrintStream(baos));
		
		List<String> actual = Arrays.asList(StringUtils.split(baos.toString(), System.lineSeparator()));
		List<String> expected = FileUtils.readLines(new File("fixture/equirep/equirep-printer-case2.txt"));
		assertEquals(expected, actual);
	}

}
