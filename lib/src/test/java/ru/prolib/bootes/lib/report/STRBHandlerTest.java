package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class STRBHandlerTest {
	STRBHeader header1, header2;
	Object handler1, handler2;
	STRBHandler<Object> service;

	@Before
	public void setUp() throws Exception {
		header1 = new STRBHeader("foo", "bar");
		header2 = new STRBHeader("zoo", "gap");
		handler1 = new Object();
		handler2 = new Object();
		service = new STRBHandler<>(header1, handler1);
	}
	
	@Test
	public void testCtor3() {
		service = new STRBHandler<>("bob", "mob", handler1);
		assertEquals(new STRBHeader("bob", "mob"), service.getHeader());
		assertEquals(handler1, service.getHandler());
	}
	
	@Test
	public void testCtor2() {
		assertEquals(header1, service.getHeader());
		assertEquals(handler1, service.getHandler());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(97315003, 37)
				.append(header1)
				.append(handler1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new STRBHandler<>("foo", "bar", handler1)));
		assertTrue(service.equals(new STRBHandler<>(header1, handler1)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new STRBHandler<>(header2, handler1)));
		assertFalse(service.equals(new STRBHandler<>(header2, handler2)));
		assertFalse(service.equals(new STRBHandler<>(header1, handler2)));
	}

}
