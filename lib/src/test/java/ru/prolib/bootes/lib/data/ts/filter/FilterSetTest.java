package ru.prolib.bootes.lib.data.ts.filter;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.FilterSet;
import ru.prolib.bootes.lib.data.ts.filter.FilterSetState;
import ru.prolib.bootes.lib.data.ts.filter.FilterState;
import ru.prolib.bootes.lib.data.ts.filter.FilterStub;
import ru.prolib.bootes.lib.data.ts.filter.IFilter;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSetState;
import ru.prolib.bootes.lib.data.ts.filter.IFilterState;

public class FilterSetTest {
	private IMocksControl control;
	private IFilter filterMock1, filterMock2, filterMock3;
	private S3TradeSignal signalMock;
	private Map<String, IFilter> filters;
	private FilterSet service;
	private IFilter filterStub1, filterStub2, filterStub3;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		filterMock1 = control.createMock(IFilter.class);
		filterMock2 = control.createMock(IFilter.class);
		filterMock3 = control.createMock(IFilter.class);
		signalMock = control.createMock(S3TradeSignal.class);
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
	public void testApprove() {
		filters.put("foo", filterMock1);
		filters.put("bar", filterMock2);
		filters.put("buz", filterMock3);
		expect(filterMock1.approve(signalMock)).andReturn(true);
		expect(filterMock2.approve(signalMock)).andReturn(false);
		expect(filterMock3.approve(signalMock)).andReturn(true);
		control.replay();

		IFilterSetState actual = service.approve(signalMock);
		
		control.verify();
		List<IFilterState> expected_states = new ArrayList<>();
		expected_states.add(new FilterState("foo", true));
		expected_states.add(new FilterState("bar", false));
		expected_states.add(new FilterState("buz", true));
		IFilterSetState expected = new FilterSetState(expected_states);
		assertEquals(expected, actual);
	}

}
