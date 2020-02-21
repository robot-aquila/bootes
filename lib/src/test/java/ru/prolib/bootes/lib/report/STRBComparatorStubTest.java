package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.difflib.patch.AbstractDelta;

public class STRBComparatorStubTest {
	List<TextLine> expectedLines, actualLines;
	STRBComparatorStub service;

	@Before
	public void setUp() throws Exception {
		expectedLines = Arrays.asList(
				new TextLine(0, "foo"),
				new TextLine(1, "bar"),
				new TextLine(2, "raw")
			);
		actualLines = Arrays.asList(
				new TextLine(0, "zoo"),
				new TextLine(1, "boo")
			);
		service = new STRBComparatorStub("mamba");
	}
	
	@Test
	public void testGetReportID() {
		assertEquals("mamba", service.getReportID());
	}

	@Test
	public void testDiff() throws Exception {
		List<AbstractDelta<String>> actual = service.diff(expectedLines, actualLines);
		
		assertEquals(new ArrayList<>(), actual);
	}

}
