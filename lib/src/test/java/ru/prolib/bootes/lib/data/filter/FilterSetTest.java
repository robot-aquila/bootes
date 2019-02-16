package ru.prolib.bootes.lib.data.filter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class FilterSetTest {
	private Map<String, IFilter> filters;
	private FilterSet service;
	private IFilter filterStub1, filterStub2, filterStub3;

	@Before
	public void setUp() throws Exception {
		filters = new LinkedHashMap<>();
		service = new FilterSet(filters);
		filterStub1 = new FilterStub("foo", true);
		filterStub2 = new FilterStub("bar", false);
		filterStub3 = new FilterStub("buz", true);
	}
	
	@Test
	public void testAddFilter() {
		filters.put("bar", filterStub2);
		
		assertSame(service, service.addFilter(filterStub1));
		assertSame(service, service.addFilter(filterStub3));
		
		Map<String, IFilter> expected = new LinkedHashMap<>();
		expected.put("bar", filterStub2);
		expected.put("foo", filterStub1);
		expected.put("buz", filterStub3);
		assertEquals(expected, filters);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAddFilter_ThrowsIfExists() {
		filters.put("bar", filterStub2);
		
		service.addFilter(filterStub2);
	}
	
	@Test
	public void testRemoveFilter() {
		filters.put("foo", filterStub1);
		filters.put("bar", filterStub2);
		filters.put("buz", filterStub3);
		
		assertSame(service, service.removeFilted("bar"));
		
		Map<String, IFilter> expected = new LinkedHashMap<>();
		expected.put("foo", filterStub1);
		expected.put("buz", filterStub3);
		assertEquals(expected, filters);
	}

	@Test
	public void testCheckState() {
		filters.put("foo", filterStub1);
		filters.put("bar", filterStub2);
		filters.put("buz", filterStub3);

		IFilterSetState actual = service.checkState();
		
		List<IFilterState> expected_states = new ArrayList<>();
		expected_states.add(new FilterState("foo", true));
		expected_states.add(new FilterState("bar", false));
		expected_states.add(new FilterState("buz", true));
		IFilterSetState expected = new FilterSetState(expected_states);
		assertEquals(expected, actual);
	}

}
