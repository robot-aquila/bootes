package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;
import static ru.prolib.bootes.lib.report.FixtureHelper.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.difflib.patch.AbstractDelta;

public class STRComparatorImplTest {
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Rule public ExpectedException eex = ExpectedException.none();
	Map<String, STRBComparator> comparators;
	STRComparatorImpl service;

	@Before
	public void setUp() throws Exception {
		comparators = new HashMap<>();
		comparators.put("SummaryReport_v0.1.0", new STRBComparatorDumb(""));
		comparators.put("S3Report_v0.1.0", new STRBComparatorDumb(""));
		comparators.put("EquityReport_v0.1.0", new STRBComparatorDumb(""));
		service = new STRComparatorImpl(comparators);
	}
	
	@Test
	public void testDiff_ExpectedReportHasNoBlocks() throws Exception {
		TextLineReader exp_reader = createReader("");
		TextLineReader act_reader = createReader(fixture_report_all());
		eex.expect(ParseException.class);
		eex.expectMessage("Expected report has no blocks");
		
		service.diff(exp_reader, act_reader);
	}

	@Test
	public void testDiff_ActualReportHasNoBlocks() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report_all());
		TextLineReader act_reader = createReader("");
		
		STRCmpResult actual = service.diff(exp_reader, act_reader);
		
		STRBCmpResultType type = STRBCmpResultType.NOT_EXISTS;
		String desc = "Report block not found";
		STRCmpResult expected = new STRCmpResult(false, Arrays.asList(
				new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"), type, desc),
				new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"), type, desc),
				new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"), type, desc)
			));
		assertEquals(expected, actual);
	}

	@Test
	public void testDiff_ComparatorNotFound() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report_all());
		TextLineReader act_reader = createReader(fixture_report_all());
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Comparator not found: S3Report_v0.1.0");
		comparators.remove("S3Report_v0.1.0");
		
		service.diff(exp_reader, act_reader);
	}
	
	@Test
	public void testDiff_MalformedHeaderInExpectedReport() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report_all().append("CAP=12 totem=zulu24\n\n"));
		TextLineReader act_reader = createReader(fixture_report_all());
		int expected_line_no = toStringList(fixture_report_all()).size();
		
		try {
			service.diff(exp_reader, act_reader);
			fail("Expected exception: " + ParseException.class);
		} catch ( ParseException e ) {
			assertEquals("Error parsing expected report: Malformed header at line "
					+ expected_line_no + ": CAP=12 totem=zulu24", e.getMessage());
			assertEquals(expected_line_no, e.getErrorOffset()); // line no
		}
	}
	
	@Test
	public void testDiff_MalformedHeaderInActualReport() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report_all());
		TextLineReader act_reader = createReader(fixture_report_all().append("charlie-delta-gamma title=yep\n"));
		int expected_line_no = toStringList(fixture_report_all()).size();
		
		STRCmpResult actual = service.diff(exp_reader, act_reader);
		
		STRBCmpResultType type = STRBCmpResultType.HEADER_MALFORMED;
		String desc = "Error parsing actual report: Malformed header at line "
				+ expected_line_no + ": charlie-delta-gamma title=yep";
		STRCmpResult expected = new STRCmpResult(false, Arrays.asList(
				new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"), type, desc),
				new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"), type, desc),
				new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"), type, desc)
			));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiff_HeaderMismatch() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report_all());
		StringBuilder sb = fixture_report2_1(fixture_report3(fixture_report1(new StringBuilder())));
		TextLineReader act_reader = createReader(sb);
		
		STRCmpResult actual = service.diff(exp_reader, act_reader);
		
		STRCmpResult expected = new STRCmpResult(false, Arrays.asList(
				new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"),
						STRBCmpResultType.IDENTICAL, "Identical"),
				new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"),
						STRBCmpResultType.HEADER_MISMATCH,
						"Unexpected header: STRBHeader[reportID=EquityReport_v0.1.0,title=Equity]"),
				new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"),
						STRBCmpResultType.HEADER_MISMATCH,
						"Unexpected header: STRBHeader[reportID=S3Report_v0.1.0,title=Trades]")
			));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiff_ContentMismatch() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report_all());
		StringBuilder sb = fixture_report3(fixture_report2_2(fixture_report1(new StringBuilder())));
		TextLineReader act_reader = createReader(sb);
		
		STRCmpResult actual = service.diff(exp_reader, act_reader);
		
		STRCmpResult expected = new STRCmpResult(false, Arrays.asList(
			new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"),
				STRBCmpResultType.IDENTICAL, "Identical"),
			new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"),
				STRBCmpResultType.REPORT_MISMATCH, "Found differences",
				diff(fixture_report2_1(fixture_report1()), fixture_report2_2(fixture_report1()))),
			new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"),
				STRBCmpResultType.IDENTICAL, "Identical")
		));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiff_ComparatorFailed() throws Exception {
		comparators.put("S3Report_v0.1.0", new STRBComparator() {
			@Override public String getReportID() { return "Stub"; }
			@Override public List<AbstractDelta<String>> diff(List<TextLine> expected, List<TextLine> actual) {
				throw new RuntimeException("Test error");
			}
		});
		TextLineReader exp_reader = createReader(fixture_report_all());
		TextLineReader act_reader = createReader(fixture_report_all());
		
		STRCmpResult actual = service.diff(exp_reader, act_reader);
		
		STRCmpResult expected = new STRCmpResult(false, Arrays.asList(
			new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"),
				STRBCmpResultType.IDENTICAL, "Identical"),
			new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"),
				STRBCmpResultType.REPORT_COMPARE_FAILED, "Test error"),
			new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"),
				STRBCmpResultType.IDENTICAL, "Identical")
		));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiff_ActualReportContainsLessBlocksThanExpected() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report_all());
		TextLineReader act_reader = createReader(fixture_report2_1(fixture_report1(new StringBuilder())));
		
		STRCmpResult actual = service.diff(exp_reader, act_reader);
		
		STRCmpResult expected = new STRCmpResult(false, Arrays.asList(
			new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"),
				STRBCmpResultType.IDENTICAL, "Identical"),
			new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"),
				STRBCmpResultType.IDENTICAL, "Identical"),
			new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"),
				STRBCmpResultType.NOT_EXISTS, "Report block not found")
		));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiff_ActualReportContainsMoreBlocksThanExpected() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report2_1(fixture_report1(new StringBuilder())));
		TextLineReader act_reader = createReader(fixture_report_all());
		
		STRCmpResult actual = service.diff(exp_reader, act_reader);
		
		STRCmpResult expected = new STRCmpResult(false, Arrays.asList(
			new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"),
				STRBCmpResultType.IDENTICAL, "Identical"),
			new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"),
				STRBCmpResultType.IDENTICAL, "Identical"),
			new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"),
				STRBCmpResultType.UNEXPECTED, "Report block is not expected")
		));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiff_AllMatched() throws Exception {
		TextLineReader exp_reader = createReader(fixture_report_all());
		TextLineReader act_reader = createReader(fixture_report_all());
		
		STRCmpResult actual = service.diff(exp_reader, act_reader);
		
		STRBCmpResultType type = STRBCmpResultType.IDENTICAL;
		String desc = "Identical";
		STRCmpResult expected = new STRCmpResult(true, Arrays.asList(
			new STRBCmpResult(new STRBHeader("SummaryReport_v0.1.0", "Summary"), type, desc),
			new STRBCmpResult(new STRBHeader("S3Report_v0.1.0", "Trades"), type, desc),
			new STRBCmpResult(new STRBHeader("EquityReport_v0.1.0", "Equity"), type, desc)
		));
		assertEquals(expected, actual);
	}
	
}
