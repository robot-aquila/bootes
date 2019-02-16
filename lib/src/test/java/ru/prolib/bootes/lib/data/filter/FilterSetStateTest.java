package ru.prolib.bootes.lib.data.filter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class FilterSetStateTest {
	private List<IFilterState> states;
	private FilterSetState service;

	@Before
	public void setUp() throws Exception {
		states = new ArrayList<>();
		service = new FilterSetState(states);
	}
	
	@Test
	public void testHasApproved() {
		states.add(new FilterState("foo", false));
		states.add(new FilterState("bar", false));
		states.add(new FilterState("buz", false));
		
		assertFalse(service.hasApproved());
		
		states.clear();
		states.add(new FilterState("foo", true));
		states.add(new FilterState("bar", false));
		states.add(new FilterState("buz", true));
		
		assertTrue(service.hasApproved());
	}
	
	@Test
	public void testHasDeclined() {
		states.add(new FilterState("foo", true));
		states.add(new FilterState("bar", true));
		states.add(new FilterState("buz", true));
		
		assertFalse(service.hasDeclined());
		
		states.clear();
		states.add(new FilterState("foo", false));
		states.add(new FilterState("bar", true));
		states.add(new FilterState("buz", false));
		
		assertTrue(service.hasDeclined());
	}
	
	@Test
	public void testToString() {
		states.add(new FilterState("foo", false));
		states.add(new FilterState("bar", true));
		states.add(new FilterState("buz", false));
		String expected = "FilterSetState[states=" + states + "]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		states.add(new FilterState("foo", false));
		states.add(new FilterState("bar", true));
		states.add(new FilterState("buz", false));
		int expected = new HashCodeBuilder(1987221, 551)
				.append(states)
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
		states.add(new FilterState("foo", false));
		states.add(new FilterState("bar", true));
		states.add(new FilterState("buz", false));
		List<IFilterState> states1 = new ArrayList<>();
		states1.add(new FilterState("foo", false));
		states1.add(new FilterState("bar", true));
		states1.add(new FilterState("buz", false));
		List<IFilterState> states2 = new ArrayList<>();
		states2.add(new FilterState("foo", false));
		states2.add(new FilterState("bar", true));
		FilterSetState x1 = new FilterSetState(states1);
		FilterSetState x2 = new FilterSetState(states2);
		assertTrue(service.equals(x1));
		assertFalse(service.equals(x2));
	}

}
