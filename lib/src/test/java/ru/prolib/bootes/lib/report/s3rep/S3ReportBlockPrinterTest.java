package ru.prolib.bootes.lib.report.s3rep;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class S3ReportBlockPrinterTest {
	private static ZoneId zoneID = ZoneId.of("Europe/Moscow");
	
	static Instant ZT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(zoneID).toInstant();
	}
	
	private List<S3RRecord> records;
	private S3Report report;
	private S3ReportBlockPrinter service;

	@Before
	public void setUp() throws Exception {
		records = new ArrayList<>();
		report = new S3Report(records, new LinkedHashSet<>());
		service = new S3ReportBlockPrinter(report, zoneID);
	}
	
	@Test
	public void testCtor2() {
		assertSame(report, service.getReport());
		assertEquals(zoneID, service.getZoneID());
		assertEquals("Default", service.getTitle());
	}

	@Test
	public void testPrint_WoMsec() throws Exception {
		records.add(new S3RRecord(1,
				S3RType.LONG,
				ZT("2019-04-05T13:14:27"),
				of("120450"),
				of(5L),
				of("120900"),
				of("120300"),
				of("120500"),
				ZT("2019-04-05T14:00:00"),
				of("120700"),
				ofRUB5("13528.12")));
		records.add(new S3RRecord(2,
				S3RType.SHORT,
				ZT("2019-04-05T14:05:00"),
				of("140320"),
				of(1L),
				of("120000"),
				of("140400"),
				of("140350"),
				ZT("2019-04-05T14:30:00"),
				of("140200"),
				ofRUB5("-27651.42")));
		records.add(new S3RRecord(3,
				S3RType.LONG,
				ZT("2019-04-05T15:40:00"),
				of("120450"),
				of(15L),
				of("120900"),
				of("120300"),
				of("120500"),
				null,
				null,
				null));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		service.print(new PrintStream(baos));

		List<String> actual = Arrays.asList(StringUtils.split(baos.toString(), System.lineSeparator()));
		List<String> expected = FileUtils.readLines(new File("fixture/s3r/s3r-printer-case1.txt"));
		assertEquals(expected, actual);
	}

	@Test
	public void testPrint_WithMsec_EntryTime() throws Exception {
		records.add(new S3RRecord(1,
				S3RType.LONG,
				ZT("2019-04-05T13:14:27.052"),
				of("120450"),
				of(5L),
				of("120900"),
				of("120300"),
				of("120500"),
				ZT("2019-04-05T14:00:00"),
				of("120700"),
				ofUSD5("13528.12")));
		records.add(new S3RRecord(2,
				S3RType.SHORT,
				ZT("2019-04-05T14:05:00"),
				of("140320"),
				of(1L),
				of("120000"),
				of("140400"),
				of("140350"),
				ZT("2019-04-05T14:30:00"),
				of("140200"),
				ofUSD5("-27651.42")));
		records.add(new S3RRecord(3,
				S3RType.LONG,
				ZT("2019-04-05T15:40:00"),
				of("120450"),
				of(15L),
				of("120900"),
				of("120300"),
				of("120500"),
				null,
				null,
				null));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		service.print(new PrintStream(baos));

		List<String> actual = Arrays.asList(StringUtils.split(baos.toString(), System.lineSeparator()));
		List<String> expected = FileUtils.readLines(new File("fixture/s3r/s3r-printer-case2.txt"));
		assertEquals(expected, actual);
	}

	@Test
	public void testPrint_WithMsec_ExitTime() throws Exception {
		records.add(new S3RRecord(1,
				S3RType.LONG,
				ZT("2019-04-05T13:14:27"),
				of("120450"),
				of(5L),
				of("120900"),
				of("120300"),
				of("120500"),
				ZT("2019-04-05T14:00:00.905"),
				of("120700"),
				ofUSD5("13528.12")));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		service.print(new PrintStream(baos));

		List<String> actual = Arrays.asList(StringUtils.split(baos.toString(), System.lineSeparator()));
		List<String> expected = FileUtils.readLines(new File("fixture/s3r/s3r-printer-case3.txt"));
		assertEquals(expected, actual);
	}

	@Test
	public void testGetReportID() {
		String expected = "S3Report_v0.1.0";
		
		assertEquals(expected, service.getReportID());
	}
	
	@Test
	public void testGetTitle() {
		assertEquals("Default", service.getTitle());
		service = new S3ReportBlockPrinter(report, "All Trades", zoneID);
		assertEquals("All Trades", service.getTitle());
	}
	
}
