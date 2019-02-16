package ru.prolib.bootes.lib.data.filter;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class FilterStateTest {
	private FilterState service;

	@Before
	public void setUp() throws Exception {
		service = new FilterState("foo", false);
	}
	
	@Test
	public void testCtor() {
		assertEquals("foo", service.getID());
		assertFalse(service.isApproved());
		assertTrue(service.isDeclined());
	}
	
	@Test
	public void testToString() {
		String expected = "FilterState[id=foo,approved=false]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(4788123, 903)
				.append("foo")
				.append(false)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<String> vID = new Variant<>("foo", "bar");
		Variant<Boolean> vAPPR = new Variant<>(vID, false, true);
		Variant<?> iterator = vAPPR;
		int foundCnt = 0;
		FilterState x, found = null;
		do {
			x = new FilterState(vID.get(), vAPPR.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getID());
		assertEquals(false, found.isApproved());
	}

}
