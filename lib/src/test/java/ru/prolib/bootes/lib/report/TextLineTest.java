package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class TextLineTest {
	TextLine service;

	@Before
	public void setUp() throws Exception {
		service = new TextLine(15, "Hello, Bobby!");
	}

	@Test
	public void testGetters() {
		assertEquals(15, service.getLineNo());
		assertEquals("Hello, Bobby!", service.getLineText());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(66518901, 41)
				.append(15)
				.append("Hello, Bobby!")
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new TextLine(15, "Hello, Bobby!")));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new TextLine(15, "Hello, Kitty!")));
		assertFalse(service.equals(new TextLine(22, "Hello, Bobby!")));
		assertFalse(service.equals(new TextLine(22, "Hello, Kitty!")));
	}

}
