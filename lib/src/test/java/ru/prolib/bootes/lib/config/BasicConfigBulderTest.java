package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class BasicConfigBulderTest {
	private BasicConfigBuilder service;

	@Before
	public void setUp() throws Exception {
		service = new BasicConfigBuilder();
	}
	
	@Test
	public void testWithShowHelp() throws Exception {
		BasicConfig actual = service.build();
		assertFalse(actual.isShowHelp());
		
		assertSame(service, service.withShowHelp(true));
		actual = service.build();
		assertTrue(actual.isShowHelp());
		
		assertSame(service, service.withShowHelp(false));
		actual = service.build();
		assertFalse(actual.isShowHelp());
	}
	
	@Test
	public void testWithHeadless() throws Exception {
		BasicConfig actual = service.build();
		assertFalse(actual.isHeadless());
		
		assertSame(service, service.withHeadless(true));
		actual = service.build();
		assertTrue(actual.isHeadless());
		
		assertSame(service, service.withHeadless(false));
		actual = service.build();
		assertFalse(actual.isHeadless());
	}
	
	@Test
	public void testWithNoOrders() throws Exception {
		BasicConfig actual = service.build();
		assertFalse(actual.isNoOrders());
		
		assertSame(service, service.withNoOrders(true));
		actual = service.build();
		assertTrue(actual.isNoOrders());
		
		assertSame(service, service.withNoOrders(false));
		actual = service.build();
		assertFalse(actual.isNoOrders());
	}
	
	@Test
	public void testWithDataDirectory() throws Exception {
		BasicConfig actual = service.build();
		assertNull(actual.getDataDirectory());
		
		assertSame(service, service.withDataDirectory(new File("foo/bar")));
		actual = service.build();
		assertEquals(new File("foo/bar"), actual.getDataDirectory());
		
		assertSame(service, service.withDataDirectory(null));
		actual = service.build();
		assertNull(actual.getDataDirectory());
	}
	
	@Test
	public void testWithConfigFile() throws Exception {
		BasicConfig actual = service.build();
		assertNull(actual.getConfigFile());
		
		assertSame(service, service.withConfigFile(new File("my-conf.ini")));
		actual = service.build();
		assertEquals(new File("my-conf.ini"), actual.getConfigFile());
		
		assertSame(service, service.withConfigFile(null));
		actual = service.build();
		assertNull(actual.getConfigFile());
	}

}
