package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.aquila.core.data.timeframe.ZTFMinutes;
import ru.prolib.bootes.lib.report.equirep.EquityReportBlockPrinter;
import ru.prolib.bootes.lib.report.hello.HelloBlockPrinter;
import ru.prolib.bootes.lib.report.order.OrderReport;
import ru.prolib.bootes.lib.report.order.OrderReportPrinter;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3ReportBlockPrinter;
import ru.prolib.bootes.lib.report.summarep.ISummaryReport;
import ru.prolib.bootes.lib.report.summarep.SummaryReportBlockPrinter;

public class ReportPrinterTest {
	private static ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
	private IMocksControl control;
	private IReportBlockPrinter rbpMock1, rbpMock2, rbpMock3;
	private List<STRBHandler<IReportBlockPrinter>> blocks;
	private ReportPrinter service;
	private File temp;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rbpMock1 = control.createMock(IReportBlockPrinter.class);
		rbpMock2 = control.createMock(IReportBlockPrinter.class);
		rbpMock3 = control.createMock(IReportBlockPrinter.class);
		blocks = new ArrayList<>();
		service = new ReportPrinter(ZONE_ID, blocks);
		temp = File.createTempFile("rpt-aquila-test", ".tmp");
		temp.deleteOnExit();
	}
	
	@Test
	public void testAdd1() {
		expect(rbpMock1.getReportID()).andStubReturn("one");
		expect(rbpMock2.getReportID()).andStubReturn("two");
		expect(rbpMock3.getReportID()).andStubReturn("foo");
		control.replay();
		
		assertSame(service, service.add(rbpMock1));
		assertSame(service, service.add(rbpMock2));
		assertSame(service, service.add(rbpMock3));
		
		control.verify();
		List<STRBHandler<IReportBlockPrinter>> expected = Arrays.asList(
				new STRBHandler<>("one", "Default", rbpMock1),
				new STRBHandler<>("two", "Default", rbpMock2),
				new STRBHandler<>("foo", "Default", rbpMock3)
			);
		assertEquals(expected, blocks);
	}
	
	@Test
	public void testAdd2() {
		expect(rbpMock1.getReportID()).andStubReturn("bar");
		expect(rbpMock2.getReportID()).andStubReturn("tom");
		control.replay();
		
		assertSame(service, service.add(rbpMock1, "Honey8"));
		assertSame(service, service.add(rbpMock2, "Zulu24"));
		
		control.verify();
		List<STRBHandler<IReportBlockPrinter>> expected = Arrays.asList(
				new STRBHandler<>("bar", "Honey8", rbpMock1),
				new STRBHandler<>("tom", "Zulu24", rbpMock2)
			);
		assertEquals(expected, blocks);
	}
	
	@Test
	public void testAdd2_S3ReportWithTitle() {
		IS3Report reportMock = control.createMock(IS3Report.class);
		
		assertSame(service, service.add(reportMock, "foobar"));
		
		STRBHandler<IReportBlockPrinter> actual = blocks.get(0);
		assertEquals(new STRBHeader(S3ReportBlockPrinter.REPORT_ID, "foobar"), actual.getHeader());
		S3ReportBlockPrinter printer = (S3ReportBlockPrinter) actual.getHandler();
		assertEquals(reportMock, printer.getReport());
		assertEquals(ZONE_ID, printer.getZoneID());
		assertEquals("foobar", printer.getTitle());
	}
	
	@Test
	public void testAdd2_SummarepWithTitle() {
		ISummaryReport reportMock = control.createMock(ISummaryReport.class);
		
		assertSame(service, service.add(reportMock, "zulu24"));
		
		STRBHandler<IReportBlockPrinter> actual = blocks.get(0);
		assertEquals(new STRBHeader(SummaryReportBlockPrinter.REPORT_ID, "zulu24"), actual.getHeader());
		SummaryReportBlockPrinter printer = (SummaryReportBlockPrinter) actual.getHandler();
		assertEquals(reportMock, printer.getReport());
		assertEquals(ZONE_ID, printer.getZoneID());
		assertEquals("zulu24", printer.getTitle());
	}
	
	@Test
	public void testAdd2_EquityWithTitle() {
		OHLCScalableSeries reportMock = control.createMock(OHLCScalableSeries.class);
		expect(reportMock.getTimeFrame()).andReturn(new ZTFMinutes(1, ZONE_ID));
		control.replay();
		
		assertSame(service, service.add(reportMock, "hello_dolly"));
		
		control.verify();
		STRBHandler<IReportBlockPrinter> actual = blocks.get(0);
		assertEquals(new STRBHeader(EquityReportBlockPrinter.REPORT_ID, "hello_dolly"), actual.getHeader());
		EquityReportBlockPrinter printer = (EquityReportBlockPrinter) actual.getHandler();
		assertEquals(reportMock, printer.getReport());
		assertEquals("hello_dolly", printer.getTitle());
	}
	
	@Test
	public void testAddHello() {
		control.replay();
		
		assertSame(service, service.addHello("Welcome, Bobby!"));
		
		control.verify();
		STRBHandler<IReportBlockPrinter> actual = blocks.get(0);
		assertEquals(new STRBHeader(HelloBlockPrinter.REPORT_ID, "Default"), actual.getHeader());
		HelloBlockPrinter printer = (HelloBlockPrinter) actual.getHandler();
		assertEquals(HelloBlockPrinter.REPORT_ID, printer.getReportID());
		assertEquals("Default", printer.getTitle());
		
		control.resetToStrict();
		PrintStream streamMock = control.createMock(PrintStream.class);
		streamMock.println("Welcome, Bobby!");
		control.replay();
		printer.print(streamMock);
		control.verify();
	}
	
	@Test
	public void testAdd2_OrderReport() {
		OrderReport report = new OrderReport();
		
		assertSame(service, service.add(report, "foobar"));
		
		STRBHandler<IReportBlockPrinter> actual = blocks.get(0);
		assertEquals(new STRBHeader(OrderReportPrinter.REPORT_ID, "foobar"), actual.getHeader());
		OrderReportPrinter printer = (OrderReportPrinter) actual.getHandler();
		assertEquals(report, printer.getReport());
		assertEquals("foobar", printer.getTitle());
	}

	@Test
	public void testPrint() {
		PrintStream streamMock = control.createMock(PrintStream.class);
		blocks.add(new STRBHandler<>("foo_v1.0", "Title of foo", rbpMock1));
		blocks.add(new STRBHandler<>("bar_v2.0", "Title of bar", rbpMock2));
		streamMock.println("# ReportID=foo_v1.0 Title=Title of foo");
		rbpMock1.print(streamMock);
		streamMock.println();
		streamMock.println("# ReportID=bar_v2.0 Title=Title of bar");
		rbpMock2.print(streamMock);
		streamMock.println();
		control.replay();
		
		service.print(streamMock);
		
		control.verify();
	}
	
	@Test
	public void testSave() throws Exception {
		String NA = "N/A";
		blocks.add(new STRBHandler<>("Zulu24_v1.0", "Zulu24 test", new IReportBlockPrinter() {
			@Override public String getReportID() { return NA; }
			@Override public String getTitle() { return NA; }
			@Override public void print(PrintStream stream) {
				stream.println("Zulu report body");
			}
		}));
		blocks.add(new STRBHandler<>("Kappa_v12.05", "Homer is alive!", new IReportBlockPrinter() {
			@Override public String getReportID() { return NA; }
			@Override public String getTitle() { return "N/A"; }
			@Override public void print(PrintStream stream) {
				stream.println("Bump-dump");
			}
			
		}));
		
		service.save(temp);
		
		List<String> actual = FileUtils.readLines(temp);
		List<String> expected = new ArrayList<>();
		expected.add("# ReportID=Zulu24_v1.0 Title=Zulu24 test");
		expected.add("Zulu report body");
		expected.add("");
		expected.add("# ReportID=Kappa_v12.05 Title=Homer is alive!");
		expected.add("Bump-dump");
		expected.add("");
		assertEquals(expected, actual);
	}

}
