package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class ConsecutiveTradesTest {
	private ConsecutiveTrades service;

	@Before
	public void setUp() throws Exception {
		service = new ConsecutiveTrades(ofRUB5("12.345"), 512);
	}
	
	@Test
	public void testGetters() {
		assertEquals(ofRUB5("12.34500"), service.getPnL());
		assertEquals(512, service.getCount());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("ConsecutiveTrades[")
				.append("pnl=12.34500 RUB,")
				.append("count=512")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(1955721, 307)
				.append(ofRUB5("12.345"))
				.append(512)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertTrue(service.equals(new ConsecutiveTrades(ofRUB5("12.345"), 512)));
		assertFalse(service.equals(new ConsecutiveTrades(ofRUB5("10.345"), 512)));
		assertFalse(service.equals(new ConsecutiveTrades(ofRUB5("12.345"), 124)));
	}

}
