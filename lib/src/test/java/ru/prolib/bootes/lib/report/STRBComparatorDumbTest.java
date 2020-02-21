package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;
import static java.util.Arrays.*;
import static ru.prolib.bootes.lib.report.FixtureHelper.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;

public class STRBComparatorDumbTest {
	static final String LN = System.lineSeparator();
	
	static String fixture1(int num_filler_lines) {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < num_filler_lines; i ++ ) {
			sb.append("-fill-fill-fill-").append(LN);
		}
		return sb
			.append("# ReportID=SummaryReport_v0.1.0 Title=Summary").append(LN)
			.append("Total net profit:  -185953.59386 RUB").append(LN)
			.append("    Gross profit:  2581053.29428 RUB").append(LN)
			.append("      Gross loss: -2767006.88814 RUB").append(LN)
			.append(LN)
			.toString();
	}
	
	static TextLineReader createReader(String data) {
		return new TextLineReaderBRImpl(new BufferedReader(new StringReader(data)));
	}
	
	static String fixture1() {
		return fixture1(0);
	}
	
	STRBComparatorDumb service;

	@Before
	public void setUp() throws Exception {
		service = new STRBComparatorDumb("Dumb");
	}
	
	@Test
	public void testGetReportID() {
		assertEquals("Dumb", service.getReportID());
	}

	@Test
	public void testDiff_NoDifferences() throws Exception {
		TextLineReader expected_reader = skipLines(createReader(fixture1(4)), 4);
		TextLineReader actual_reader = skipLines(createReader(fixture1(2)), 2);
		
		List<AbstractDelta<String>> actual = service.diff(read(expected_reader), read(actual_reader));
		
		assertEquals(0, actual.size());
	}
	
	@Test
	public void testDiff_Differences() throws Exception {
		TextLineReader expected_reader = skipLines(createReader(fixture1(7)), 7);
		TextLineReader actual_reader = skipLines(createReader(new StringBuilder()
				.append("-skip-skip-skip-").append(LN)
				.append("-skip-skip-skip-").append(LN)
				.append("# ReportID=SummaryReport_v0.1.2 Title=Summary").append(LN)
				.append("Total net profit:  -185953.59386 RUB").append(LN)
				.append("       New param:  2581053.29428 RUB").append(LN)
				.append("    Gross profit:  2581053.29428 RUB").append(LN)
				.append(LN)
				.toString()), 2);
		
		List<AbstractDelta<String>> actual = service.diff(read(expected_reader), read(actual_reader));
		
		List<AbstractDelta<String>> expected = new ArrayList<>();
		expected.add(new ChangeDelta<>(
				new Chunk<>(7, asList("# ReportID=SummaryReport_v0.1.0 Title=Summary")),
				new Chunk<>(2, asList("# ReportID=SummaryReport_v0.1.2 Title=Summary"))
			));
		expected.add(new InsertDelta<>(
				new Chunk<>(9, asList()),
				new Chunk<>(4, asList("       New param:  2581053.29428 RUB"))
			));
		expected.add(new DeleteDelta<>(
				new Chunk<>(10, asList("      Gross loss: -2767006.88814 RUB")),
				new Chunk<>(6, asList())
			));
		assertEquals(expected, actual);
	}

}
