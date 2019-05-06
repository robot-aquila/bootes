package ru.prolib.bootes.tsgr001a.robot.filter;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.FilterSet;
import ru.prolib.bootes.lib.data.ts.filter.IFilter;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;

@SuppressWarnings("unchecked")
public class S3TSFilterSetFactoryTest {
	private IMocksControl control;
	private IFilter<S3TradeSignal> filterMock1, filterMock2, filterMock3;
	private IS3TSFilterFactory factoryMock1, factoryMock2;
	private S3TSFilterSetFactory service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		filterMock1 = control.createMock(IFilter.class);
		filterMock2 = control.createMock(IFilter.class);
		filterMock3 = control.createMock(IFilter.class);
		factoryMock1 = control.createMock(IS3TSFilterFactory.class);
		factoryMock2 = control.createMock(IS3TSFilterFactory.class);
		service = new S3TSFilterSetFactory(factoryMock1);
	}

	@Test
	public void testProduce() {
		expect(factoryMock1.produce("foo")).andReturn(filterMock1);
		expect(filterMock1.getID()).andReturn("IM_FOO");
		expect(factoryMock1.produce("buz")).andReturn(filterMock2);
		expect(filterMock2.getID()).andReturn("IM_BUZ");
		expect(factoryMock1.produce("bar")).andReturn(filterMock3);
		expect(filterMock3.getID()).andReturn("IM_BAR");
		control.replay();
		
		IFilterSet<S3TradeSignal> actual = service.produce("foo, buz, bar");

		control.verify();
		Map<String, IFilter<S3TradeSignal>> expected_map = new LinkedHashMap<>();
		expected_map.put("IM_FOO", filterMock1);
		expected_map.put("IM_BUZ", filterMock2);
		expected_map.put("IM_BAR", filterMock3);
		IFilterSet<S3TradeSignal> expected = new FilterSet<>(expected_map);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new S3TSFilterSetFactory(factoryMock1)));
		assertFalse(service.equals(new S3TSFilterSetFactory(factoryMock2)));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(891212365, 54411)
				.append(factoryMock1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
}
