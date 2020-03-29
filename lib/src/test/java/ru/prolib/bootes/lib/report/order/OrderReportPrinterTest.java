package ru.prolib.bootes.lib.report.order;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class OrderReportPrinterTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	OrderReport report;
	OrderReportPrinter service;

	@Before
	public void setUp() throws Exception {
		report = new OrderReport();
		service = new OrderReportPrinter(report);
	}
	
	@Test
	public void testCtor1() {
		assertSame(report, service.getReport());
		assertEquals("OrderReport_v0.1.0", service.getReportID());
		assertEquals("Default", service.getTitle());
		assertEquals(ZoneId.of("Europe/Moscow"), service.getZoneId());
	}
	
	@Test
	public void testCtor2_WithZoneId() {
		service = new OrderReportPrinter(report, ZoneId.of("Europe/London"));
		assertSame(report, service.getReport());
		assertEquals("OrderReport_v0.1.0", service.getReportID());
		assertEquals("Default", service.getTitle());
		assertEquals(ZoneId.of("Europe/London"), service.getZoneId());
	}
	
	@Test
	public void testCtor2_WithTitle() {
		service = new OrderReportPrinter(report, "Zulu24");
		assertSame(report, service.getReport());
		assertEquals("OrderReport_v0.1.0", service.getReportID());
		assertEquals("Zulu24", service.getTitle());
		assertEquals(ZoneId.of("Europe/Moscow"), service.getZoneId());
	}
	
	@Test
	public void testCtor3() {
		service = new OrderReportPrinter(report, ZoneId.of("Asia/Novosibirsk"), "Zamba25");
		assertSame(report, service.getReport());
		assertEquals("OrderReport_v0.1.0", service.getReportID());
		assertEquals("Zamba25", service.getTitle());
		assertEquals(ZoneId.of("Asia/Novosibirsk"), service.getZoneId());
	}

	@Test
	public void testPrint() throws Exception {
		report.addOrder(new OrderInfo(OrderAction.BUY, new Symbol("RTS-3.18"), 3, T("2020-02-29T08:06:33.901Z"),
			of(120980L), of(11L), ofRUB2("459871.13"), "o1", Arrays.asList(
				new OrderExecInfo(1, T("2020-02-29T08:06:33.957Z"), of(120950L), of(5L), ofRUB2("218623.02"), "o1e1"),
				new OrderExecInfo(2, T("2020-02-29T08:49:17.208Z"), of(120980L), of(2L), ofRUB2( "81806.45"), "o1e2"),
				new OrderExecInfo(3, T("2020-02-29T09:15:29.102Z"), of(120870L), of(4L), ofRUB2("159441.66"), "o1e3")
			)
		));
		report.addOrder(new OrderInfo(OrderAction.SELL, new Symbol("Si-3.19"), 2, T("2020-03-01T12:01:12.707Z"),
			of(131010L), of( 3L), ofRUB2("135907.12"), "o2", Arrays.asList(
				new OrderExecInfo(1, T("2020-03-01T12:02:14.089Z"), of(131140L), of(2L), ofRUB2( "90600.02"), "o2e1"),
				new OrderExecInfo(2, T("2020-03-01T12:04:47.190Z"), of(131140L), of(1L), ofRUB2( "45307.10"), "o2e2")
			)
		));
		report.addOrder(new OrderInfo(OrderAction.BUY, new Symbol("GAZP"), 1, T("2020-04-27T05:10:35.144Z"),
			null, of(10L), ofRUB2("54297.06"), "o3", Arrays.asList(
				new OrderExecInfo(1, T("2020-04-27T05:10:35.482Z"), of( 98120L), of(10L), ofRUB2("54297.06"), "o3e1")
			)
		));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		service.print(new PrintStream(baos));
		
		List<String> actual = Arrays.asList(StringUtils.split(baos.toString(), System.lineSeparator()));
		List<String> expected = FileUtils.readLines(new File("fixture/order-report/order1.report"));
		assertEquals(expected, actual);
	}

}
