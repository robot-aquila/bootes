package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3ReportBlockPrinter;
import ru.prolib.bootes.lib.report.summarep.ISummaryReport;
import ru.prolib.bootes.lib.report.summarep.SummaryReportBlockPrinter;

public class ReportPrinterTest {
	private static ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
	private IMocksControl control;
	private IReportBlockPrinter rbpMock1, rbpMock2, rbpMock3;
	private List<IReportBlockPrinter> blocks;
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
	public void testAdd_RBP() {
		assertSame(service, service.add(rbpMock1));
		assertSame(service, service.add(rbpMock2));
		assertSame(service, service.add(rbpMock3));
		
		List<IReportBlockPrinter> expected = new ArrayList<>();
		expected.add(rbpMock1);
		expected.add(rbpMock2);
		expected.add(rbpMock3);
		assertEquals(expected, blocks);
	}
	
	@Test
	public void testAdd_S3R() {
		IS3Report reportMock = control.createMock(IS3Report.class);
		
		assertSame(service, service.add(reportMock, "foobar"));
		
		S3ReportBlockPrinter actual = (S3ReportBlockPrinter) blocks.get(0);
		assertEquals(reportMock, actual.getReport());
		assertEquals(ZONE_ID, actual.getZoneID());
		assertEquals("foobar", actual.getTitle());
	}
	
	@Test
	public void testAdd_Summarep() {
		ISummaryReport reportMock = control.createMock(ISummaryReport.class);
		
		assertSame(service, service.add(reportMock, "zulu24"));
		
		SummaryReportBlockPrinter actual = (SummaryReportBlockPrinter) blocks.get(0);
		assertEquals(reportMock, actual.getReport());
		assertEquals(ZONE_ID, actual.getZoneID());
		assertEquals("zulu24", actual.getTitle());
	}

	@Test
	public void testPrint() {
		PrintStream streamMock = control.createMock(PrintStream.class);
		blocks.add(rbpMock1);
		blocks.add(rbpMock2);
		expect(rbpMock1.getReportID()).andStubReturn("foo_v1.0");
		expect(rbpMock1.getTitle()).andStubReturn("Title of foo");
		expect(rbpMock2.getReportID()).andStubReturn("bar_v2.0");
		expect(rbpMock2.getTitle()).andStubReturn("Title of bar");
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
		blocks.add(new IReportBlockPrinter() {

			@Override
			public String getReportID() {
				return "Zulu24_v1.0";
			}

			@Override
			public String getTitle() {
				return "Zulu24 test";
			}

			@Override
			public void print(PrintStream stream) {
				stream.println("Zulu report body");
			}
			
		});
		blocks.add(new IReportBlockPrinter() {

			@Override
			public String getReportID() {
				return "Kappa_v12.05";
			}

			@Override
			public String getTitle() {
				return "Homer is alive!";
			}

			@Override
			public void print(PrintStream stream) {
				stream.println("Bump-dump");
			}
			
		});
		
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
