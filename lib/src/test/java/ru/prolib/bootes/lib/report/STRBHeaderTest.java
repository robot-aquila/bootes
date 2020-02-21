package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class STRBHeaderTest {
	STRBHeader service;

	@Before
	public void setUp() throws Exception {
		service = new STRBHeader("foo", "bar");
	}
	
	@Test
	public void testGetters() {
		assertEquals("foo", service.getReportID());
		assertEquals("bar", service.getTitle());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(5551327, 3)
				.append("foo")
				.append("bar")
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new STRBHeader("foo", "bar")));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new STRBHeader("xxx", "bar")));
		assertFalse(service.equals(new STRBHeader("foo", "xxx")));
		assertFalse(service.equals(new STRBHeader("xxx", "xxx")));
	}

	@Test
	public void testToString() {
		assertEquals("STRBHeader[reportID=foo,title=bar]", service.toString());
	}

}
