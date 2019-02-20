package ru.prolib.bootes.lib.data.ts.filter;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.data.ts.filter.FilterStub;

public class FilterStubTest {
	private FilterStub service;

	@Before
	public void setUp() throws Exception {
		service = new FilterStub("foo", true);
	}
	
	@Test
	public void testCtor() {
		assertEquals("foo", service.getID());
	}
	
	@Test
	public void testApprove() {
		assertTrue(new FilterStub("foo", true).approve(null));
		assertFalse(new FilterStub("foo", false).approve(null));
	}
	
	@Test
	public void testToString() {
		String expected = "FilterStub[id=foo,result=true]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(199873, 4009)
				.append("foo")
				.append(true)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new FilterStub("foo", true)));
		assertFalse(service.equals(new FilterStub("bar", true)));
		assertFalse(service.equals(new FilterStub("foo", false)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

}
