package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.bootes.lib.report.FixtureHelper.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class STRCmpResultTest {
	IMocksControl control;
	STRBCmpResult bcrMock1, bcrMock2, bcrMock3, bcrMock4, bcrMock5;
	List<STRBCmpResult> blockCmpResult1, blockCmpResult2;
	STRCmpResult service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		bcrMock1 = control.createMock(STRBCmpResult.class);
		bcrMock2 = control.createMock(STRBCmpResult.class);
		bcrMock3 = control.createMock(STRBCmpResult.class);
		bcrMock4 = control.createMock(STRBCmpResult.class);
		bcrMock5 = control.createMock(STRBCmpResult.class);
		blockCmpResult1 = Arrays.asList(bcrMock1, bcrMock2, bcrMock3);
		blockCmpResult2 = Arrays.asList(bcrMock4, bcrMock5);
		service = new STRCmpResult(true, blockCmpResult1);
	}
	
	@Test
	public void testGetters() {
		assertEquals(true, service.identical());
		assertEquals(blockCmpResult1, service.getReportBlockCmpResult());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(665891, 705)
				.append(true)
				.append(blockCmpResult1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new STRCmpResult(true, blockCmpResult1)));
		assertFalse(service.equals(new STRCmpResult(false, blockCmpResult1)));
		assertFalse(service.equals(new STRCmpResult(true, blockCmpResult2)));
		assertFalse(service.equals(new STRCmpResult(false, blockCmpResult2)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testToString() throws Exception {
		List<STRBCmpResult> block_cmp_result = Arrays.asList(
				
				new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"),
						STRBCmpResultType.IDENTICAL,
						"Identical"),
				
				new STRBCmpResult(new STRBHeader("Gamma_v0.5", "Boom"),
						STRBCmpResultType.HEADER_MALFORMED,
						"Malformed header found"),
				
				new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"),
						STRBCmpResultType.HEADER_MISMATCH,
						"Header mismatch"),
				
				new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"),
						STRBCmpResultType.REPORT_MISMATCH,
						"Report content mismatch",
						diff(fixture_report2_1(), fixture_report2_2())),
				
				new STRBCmpResult(new STRBHeader("Failed_v2.0", "TestME"),
						STRBCmpResultType.REPORT_COMPARE_FAILED,
						"An error occurred"),
				
				// Actually, those blocks cannot be here at same time due mutual exclusion
				// They are here just to test how it will be converted to string
				
				new STRBCmpResult(new STRBHeader("Lenny_v2.0", "Hello, World!"),
						STRBCmpResultType.NOT_EXISTS,
						"Expected report block not exists"),
				
				new STRBCmpResult(new STRBHeader("Gamma_v5.0", "Charlie"),
						STRBCmpResultType.UNEXPECTED,
						"The unexpected report block")
				
			);
		assertEquals(STRBCmpResultType.values().length, block_cmp_result.size());
		service = new STRCmpResult(false, block_cmp_result);
		
		String actual = service.toString();
		
		String expected = new StringBuilder()
			.append("STRCmpResult identical=false, blocks:\n")
			.append("EquityReport_v0.1.0[Equity] status IDENTICAL[Identical]\n")
			.append("Gamma_v0.5[Boom] status HEADER_MALFORMED[Malformed header found]\n")
			.append("SummaryReport_v0.1.0[Summary] status HEADER_MISMATCH[Header mismatch]\n")
			.append("S3Report_v0.1.0[Trades] status REPORT_MISMATCH[Report content mismatch]\n")
			.append("\tChange at line 1:\n")
			.append("\t\t[PU: N/A    MU: RUB    TZ: Europe/Moscow]\n")
			.append("\t\t[Seed: 33324]\n")
			.append("\tchanged to:\n")
			.append("\t\t[PU: N/A    MU: USD    TZ: America/New York]\n")
			.append("\t\t[Seed: 2597]\n")
			.append("\tInsert at line 6:\n")
			.append("\t\t[  -1 |  LONG | 12:00:00 | 100000 | 110000 |  10 |  98000 |    -24.00000 |    1]\n")
			.append("\tDelete at line 8:\n")
			.append("\t\t[   2 |  LONG | 12:25:27 | 117648 | 117640 |  42 | 119138 |   -411.37552 |   90]\n")
			.append("Failed_v2.0[TestME] status REPORT_COMPARE_FAILED[An error occurred]\n")
			.append("Lenny_v2.0[Hello, World!] status NOT_EXISTS[Expected report block not exists]\n")
			.append("Gamma_v5.0[Charlie] status UNEXPECTED[The unexpected report block]\n")
			.toString();
		assertEquals(expected, actual);
	}

}
