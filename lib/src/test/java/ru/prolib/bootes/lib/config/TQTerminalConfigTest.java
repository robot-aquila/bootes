package ru.prolib.bootes.lib.config;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;

public class TQTerminalConfigTest {
	
	@Rule
	public ExpectedException eex = ExpectedException.none();

	private IMocksControl control;
	private Map<String, String> options1, options2;
	private TQTerminalConfig service1, service2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service1 = new TQTerminalConfig(new OptionProviderKvs(new KVStoreHash(options1 = new LinkedHashMap<>())));
		service2 = new TQTerminalConfig(new OptionProviderKvs(new KVStoreHash(options2 = new LinkedHashMap<>())));
	}
	
	@Test
	public void testGetLogPath() throws Exception {
		options1.put("transaq-log-path", "/foo/bar");
		options2.put("transaq-log-path", "karamba");
		
		assertEquals(new File("/foo/bar"), service1.getLogPath());
		assertEquals(new File("karamba"), service2.getLogPath());
	}
	
	@Test
	public void testGetLogPath_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("transaq-log-path option expected to be not null");

		service2.getLogPath();
	}
	
	@Test
	public void testGetLogLevel() throws Exception {
		options1.put("transaq-log-level", "0");
		options2.put("transaq-log-level", "2");
		
		assertEquals(0, service1.getLogLevel());
		assertEquals(2, service2.getLogLevel());
	}
	
	@Test
	public void testGetLogLevel_ThrowsIfBadValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("transaq-log-level option expected to be an integer but: kabucha");
		options2.put("transaq-log-level", "kabucha");

		service2.getLogLevel();
	}
	
	@Test
	public void testGetLogin() throws Exception {
		options1.put("transaq-login", "user543");
		
		assertEquals("user543", service1.getLogin());
	}
	
	@Test
	public void testGetLogin_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("transaq-login option expected to be not null");

		service2.getLogin();
	}
	
	@Test
	public void testGetPassword() throws Exception {
		options2.put("transaq-password", "gotcha");
		
		assertEquals("gotcha", service2.getPassword());
	}
	
	@Test
	public void testGetPassword_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("transaq-password option expected to be not null");

		service2.getPassword();
	}
	
	@Test
	public void testGetHost() throws Exception {
		options1.put("transaq-host", "localhost");
		
		assertEquals("localhost", service1.getHost());
	}

	@Test
	public void testGetHost_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("transaq-host option expected to be not null");

		service2.getHost();
	}
	
	@Test
	public void testGetPort() throws Exception {
		options2.put("transaq-port", "5512");
		
		assertEquals(5512, service2.getPort());
	}
	
	@Test
	public void testGetPort_ThrowsIfBadValue() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("transaq-port option expected to be not null");

		service2.getPort();
	}
	
	@Test
	public void testIsMsgDumpEnabled() throws Exception {
		assertFalse(service1.isMsgDumpEnabled());
		
		options1.put("transaq-msg-dump-enable", "1");
		assertTrue(service1.isMsgDumpEnabled());
		
		options1.put("transaq-msg-dump-enable", "0");
		assertFalse(service1.isMsgDumpEnabled());
		
		options1.put("transaq-msg-dump-enable", null);
		assertFalse(service1.isMsgDumpEnabled());
	}
	
	@Test
	public void testGetMsgDumpFile() {
		assertNull(service1.getMsgDumpFile());
		
		options1.put("transaq-msg-dump-file", "zulu/charlie");
		assertEquals(new File("zulu/charlie"), service1.getMsgDumpFile());
	}
	
	@Test
	public void testEquals() {
		OptionProvider
			opMock1 = control.createMock(OptionProvider.class),
			opMock2 = control.createMock(OptionProvider.class);
		TQTerminalConfig service = new TQTerminalConfig(opMock1);
		
		assertTrue(service.equals(service));
		assertTrue(service.equals(new TQTerminalConfig(opMock1)));
		assertFalse(service.equals(new TQTerminalConfig(opMock2)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

}
