package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.utils.Variant;

public class AppConfig2Test {
	
	@Rule
	public ExpectedException eex = ExpectedException.none();

	private IMocksControl control;
	private OptionProvider opMock1, opMock2;
	private Map<String, Object> sections;
	private AppConfig2 service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sections = new LinkedHashMap<>();
		opMock1 = control.createMock(OptionProvider.class);
		opMock2 = control.createMock(OptionProvider.class);
		service = new AppConfig2(sections, opMock1);
	}
	
	@Test
	public void testGetSection() {
		Integer a = Integer.valueOf(12);
		Boolean b = Boolean.TRUE;
		sections.put("foo", a);
		sections.put("bar", b);
		
		Integer a_ = service.getSection("foo");
		assertSame(a, a_);
		
		Boolean b_ = service.getSection("bar");
		assertSame(b, b_);
	}
	
	@Test
	public void testGetSection_ThrowsIfNotExists() throws Exception {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Section not exists: foobar");

		service.getSection("foobar");
	}

	@Test
	public void testGetOptions() {
		assertSame(opMock1, service.getOptions());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		sections.put("foo", "gamma");
		sections.put("bar", "beta");
		Map<String, Object> map1 = new LinkedHashMap<>();
		map1.put("foo", "gamma");
		map1.put("bar", "beta");
		Map<String, Object> map2 = new LinkedHashMap<>();
		map2.put("zoo", 123);
		map2.put("bum", false);
		Variant<Map<String, Object>> vMap = new Variant<>(map1, map2);
		Variant<OptionProvider> vOp = new Variant<>(vMap, opMock1, opMock2);
		Variant<?> iterator = vOp;
		int found_cnt = 0;
		AppConfig2 x, found = null;
		do {
			x = new AppConfig2(vMap.get(), vOp.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(map1, sections);
		assertEquals(opMock1, found.getOptions());
	}

}
