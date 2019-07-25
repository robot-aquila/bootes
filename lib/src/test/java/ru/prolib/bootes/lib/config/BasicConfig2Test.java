package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;

public class BasicConfig2Test {
	private IMocksControl control;
	private Map<String, String> options1, options2;
	private BasicConfig2 service1, service2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service1 = new BasicConfig2(new OptionProviderKvs(new KVStoreHash(options1 = new LinkedHashMap<>())));
		service2 = new BasicConfig2(new OptionProviderKvs(new KVStoreHash(options2 = new LinkedHashMap<>())));
	}
	
	@Test
	public void testIsShowHelp() throws Exception {
		assertFalse(service1.isShowHelp());
		assertFalse(service2.isShowHelp());
		
		options1.put("help", "true");
		options2.put("help", "false");
		
		assertTrue(service1.isShowHelp());
		assertFalse(service2.isShowHelp());
	}
	
	@Test
	public void testIsHeadless() throws Exception {
		assertFalse(service1.isHeadless());
		assertFalse(service2.isHeadless());
		
		options1.put("headless", "1");
		options2.put("headless", "0");
		
		assertTrue(service1.isHeadless());
		assertFalse(service2.isHeadless());
	}
	
	@Test
	public void testIsNoOrders() throws Exception {
		assertFalse(service1.isNoOrders());
		assertFalse(service2.isNoOrders());
		
		options1.put("no-orders", "false");
		options2.put("no-orders", "1");
		
		assertFalse(service1.isNoOrders());
		assertTrue(service2.isNoOrders());
	}
	
	@Test
	public void testGetDataDirectory() throws Exception {
		assertNull(service1.getDataDirectory());
		assertNull(service2.getDataDirectory());
		
		options1.put("data-dir", "/foo/bar");
		options2.put("data-dir", "/boo/buz");
		
		assertEquals(new File("/foo/bar"), service1.getDataDirectory());
		assertEquals(new File("/boo/buz"), service2.getDataDirectory());
	}
	
	@Test
	public void testGetConfigFile() throws Exception {
		assertNull(service1.getConfigFile());
		assertNull(service2.getConfigFile());
		
		options1.put("config-file", "/my/config.ini");
		options2.put("config-file", "karamba.ini");
		
		assertEquals(new File("/my/config.ini"), service1.getConfigFile());
		assertEquals(new File("karamba.ini"), service2.getConfigFile());
	}

	@Test
	public void testReportDirectory() throws Exception {
		assertNull(service1.getReportDirectory());
		assertNull(service2.getReportDirectory());
		
		options1.put("report-dir", "/tmp/12monkeys");
		options2.put("report-dir", "kakabucha");
		
		assertEquals(new File("/tmp/12monkeys"), service1.getReportDirectory());
		assertEquals(new File("kakabucha"), service2.getReportDirectory());
	}
	
	@Test
	public void testEquals() {
		OptionProvider opMock = control.createMock(OptionProvider.class);
		BasicConfig2 service = new BasicConfig2(opMock);
		
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertTrue(service.equals(new BasicConfig2(opMock)));
		assertFalse(service.equals(new BasicConfig2(control.createMock(OptionProvider.class))));
	}

}
