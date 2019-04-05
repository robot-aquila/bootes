package ru.prolib.bootes.lib.report.summarep;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

public class SummaryReportBlockPrinterTest {
	private static ZoneId zoneID = ZoneId.of("Europe/Moscow");
	private ISummaryReport report;
	private SummaryReportBlockPrinter service;

	@Before
	public void setUp() throws Exception {
		report = new SummaryReport(
				ofRUB5( "1734.24"),		// gross profit
				ofRUB5("-1352.93"),		// gross loss
				ofRUB5(  "143.06"),		// absolute drawdown
				ofRUB5(  "194.58"),		// maximal drawdown			
				ofRUB5(  "207.36"),		// largest profit trade
				ofRUB5( "-188.20"),		// largest loss trade
				150,					// short positions
				117,					// winning short positions
				154,					// long positions
				123,					// winning long positions
				  5,					// average consecutive wins
				  7,					// average consecutive losses
				new SRTradeSSI(ofRUB5(  "46.36"), 19),
				new SRTradeSSI(ofRUB5("-114.36"),  3),
				new SRTradeSSI(ofRUB5( "123.45"), 10),
				new SRTradeSSI(ofRUB5( "543.21"), 15)
			);
		service = new SummaryReportBlockPrinter(report, zoneID);
	}
	
	@Test
	public void testCtor2() {
		assertSame(report, service.getReport());
		assertEquals(zoneID, service.getZoneID());
		assertEquals("Default", service.getTitle());
		assertEquals("SummaryReport_v0.1.0", service.getReportID());
	}
	
	@Test
	public void testCtor3() {
		service = new SummaryReportBlockPrinter(report, "foobar", zoneID);
		assertSame(report, service.getReport());
		assertEquals(zoneID, service.getZoneID());
		assertEquals("foobar", service.getTitle());
		assertEquals("SummaryReport_v0.1.0", service.getReportID());
	}
	
	@Test
	public void testPrint() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		service.print(new PrintStream(baos));
		
		String ln = System.lineSeparator();
		String expected = new StringBuilder()
			.append("    Total net profit:   381.31000 RUB").append(ln)
			.append("        Gross profit:  1734.24000 RUB").append(ln)
			.append("          Gross loss: -1352.93000 RUB").append(ln)
			.append("       Profit factor: 1.28184").append(ln)
			.append("     Expected payoff:     1.25431 RUB").append(ln)
			.append("   Absolute drawdown:   143.06000 RUB").append(ln)
			.append("    Maximal drawdown:   194.58000 RUB").append(ln)
			.append("        Total trades: 304").append(ln)
			.append("     Short positions: 150").append(ln)
			.append("       Short winners: 117").append(ln)
			.append("      Long positions: 154").append(ln)
			.append("        Long winners: 123").append(ln)
			.append("       Profit trades: 240").append(ln)
			.append("         Lost trades:  64").append(ln)
			.append("Largest profit trade:   207.36000 RUB").append(ln)
			.append("  Largest loss trade:  -188.20000 RUB").append(ln)
			.append("Average profit trade:     7.22600 RUB").append(ln)
			.append("  Average loss trade:   -21.13953 RUB").append(ln)
			.append("   Max.cons.wins/cnt:   46.36000 RUB/19").append(ln)
			.append(" Max.cons.losses/cnt: -114.36000 RUB/3").append(ln)
			.append("Mxml.cons.profit/cnt:  123.45000 RUB/10").append(ln)
			.append("  Mxml.cons.loss/cnt:  543.21000 RUB/15").append(ln)
			.append("       Avg.cons.wins:   5").append(ln)
			.append("     Avg.cons.losses:   7").append(ln)
			.toString();
		assertEquals(expected, baos.toString());
	}

}
