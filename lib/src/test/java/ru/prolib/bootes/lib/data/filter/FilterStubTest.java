package ru.prolib.bootes.lib.data.filter;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class FilterStubTest {
	private FilterStub service;

	@Before
	public void setUp() throws Exception {
		service = new FilterStub("foo", true);
	}
	
	@Test
	public void testCtor() {
		assertEquals("foo", service.getID());
		assertTrue(service.checkState());
	}
	
	@Test
	public void testCheckState() {
		assertTrue(new FilterStub("foo", true).checkState());
		assertFalse(new FilterStub("foo", false).checkState());
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
