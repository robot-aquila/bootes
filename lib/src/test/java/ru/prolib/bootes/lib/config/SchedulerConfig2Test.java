package ru.prolib.bootes.lib.config;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;

public class SchedulerConfig2Test {
	
	Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	private IMocksControl control;
	private Map<String, String> options1, options2;
	private SchedulerConfig2 service1, service2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service1 = new SchedulerConfig2(new OptionProviderKvs(new KVStoreHash(options1 = new LinkedHashMap<>())));
		service2 = new SchedulerConfig2(new OptionProviderKvs(new KVStoreHash(options2 = new LinkedHashMap<>())));
	}
	
	@Test
	public void testIsAutoStart() throws Exception {
		assertFalse(service1.isAutoStart());
		assertFalse(service2.isAutoStart());
		
		options1.put("probe-auto-start", "1");
		options2.put("probe-auto-start", "0");
		
		assertTrue(service1.isAutoStart());
		assertFalse(service2.isAutoStart());
	}
	
	@Test
	public void testIsAutoShutdown() throws Exception {
		assertFalse(service1.isAutoShutdown());
		assertFalse(service2.isAutoShutdown());
	
		options1.put("probe-auto-shutdown", "false");
		options2.put("probe-auto-shutdown", "true");
		
		assertFalse(service1.isAutoShutdown());
		assertTrue(service2.isAutoShutdown());
	}
	
	@Test
	public void testGetInitialTime() throws Exception {
		assertNull(service1.getInitialTime());
		assertNull(service2.getInitialTime());
		
		options1.put("probe-initial-time", "2019-07-25T20:16:00Z");
		options2.put("probe-initial-time", "1970-01-01T00:00:00Z");
		
		assertEquals(T("2019-07-25T20:16:00Z"), service1.getInitialTime());
		assertEquals(T("1970-01-01T00:00:00Z"), service2.getInitialTime());
	}

	@Test
	public void testGetStopTime() throws Exception {
		assertNull(service1.getStopTime());
		assertNull(service2.getStopTime());
		
		options1.put("probe-stop-time", "1950-05-15T12:35:16Z");
		options2.put("probe-stop-time", "2008-06-12T15:45:07Z");
		
		assertEquals(T("1950-05-15T12:35:16Z"), service1.getStopTime());
		assertEquals(T("2008-06-12T15:45:07Z"), service2.getStopTime());
	}
	
	@Test
	public void testEquals() throws Exception {
		OptionProvider
			opMock1 = control.createMock(OptionProvider.class),
			opMock2 = control.createMock(OptionProvider.class);
		SchedulerConfig2 service = new SchedulerConfig2(opMock1);
		
		assertTrue(service.equals(service));
		assertTrue(service.equals(new SchedulerConfig2(opMock1)));
		assertFalse(service.equals(new SchedulerConfig2(opMock2)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

}
