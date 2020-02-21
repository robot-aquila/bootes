package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ConditionStopAtEmptyLineTest {
	ConditionStopAtEmptyLine service;

	@Before
	public void setUp() throws Exception {
		service = new ConditionStopAtEmptyLine();
	}

	@Test
	public void testVaidate() throws Exception {
		assertTrue(service.validate(new TextLine(-1, "")));
		assertTrue(service.validate(new TextLine(-1, "   \t  ")));
		assertTrue(service.validate(new TextLine(-1, "\r\n")));
		assertTrue(service.validate(new TextLine(-1, "\n")));
		assertTrue(service.validate(new TextLine(-1, System.lineSeparator())));
		assertFalse(service.validate(new TextLine(-1, "x")));
	}
	
	@Test
	public void testHashCode() {
		assertEquals(1886443081, service.hashCode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new ConditionStopAtEmptyLine()));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testGetInstance() {
		ConditionStopAtEmptyLine actual = ConditionStopAtEmptyLine.getInstance();
		
		assertNotNull(actual);
		assertEquals(new ConditionStopAtEmptyLine(), actual);
		assertSame(actual, ConditionStopAtEmptyLine.getInstance());
	}

}
