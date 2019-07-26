package ru.prolib.bootes.lib.config;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;

public class TQTerminalConfigSectionTest {
	private IMocksControl control;
	private TQTerminalConfigSection service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new TQTerminalConfigSection();
	}
	
	@Test
	public void testConfigureDefaults() throws Exception {
		Map<String, String> defaults_data = new HashMap<>(), options_data = new HashMap<>();
		KVStoreHash defaults = new KVStoreHash(defaults_data);
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		
		service.configureDefaults(defaults, options);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("transaq-log-level", "0");
		expected.put("transaq-port", "3900");
		assertEquals(expected, defaults_data);
	}
	
	@Test
	public void testConfigureOptions() throws Exception {
		Options options = new Options();
		
		service.configureOptions(options);

		assertEquals(6, options.getOptions().size());
		
		Option actual = options.getOption("transaq-log-path");
		assertEquals(Option.builder()
				.longOpt("transaq-log-path")
				.hasArg()
				.argName("path")
				.desc("Path to transaq log files.")
				.build(), actual);
		assertTrue(actual.hasArg());

		actual = options.getOption("transaq-log-level");
		assertEquals(Option.builder()
				.longOpt("transaq-log-level")
				.hasArg()
				.argName("level")
				.desc("Transaq log level. Possible values: 0, 1 or 2.")
				.build(), actual);
		assertTrue(actual.hasArg());

		actual = options.getOption("transaq-login");
		assertEquals(Option.builder()
				.longOpt("transaq-login")
				.hasArg()
				.argName("login")
				.desc("Transaq login.")
				.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("transaq-password");
		assertEquals(Option.builder()
				.longOpt("transaq-password")
				.hasArg()
				.argName("pwd")
				.desc("Transaq password.")
				.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("transaq-host");
		assertEquals(Option.builder()
				.longOpt("transaq-host")
				.hasArg()
				.argName("host")
				.desc("Transaq host.")
				.build(), actual);
		assertTrue(actual.hasArg());

		actual = options.getOption("transaq-port");
		assertEquals(Option.builder()
				.longOpt("transaq-port")
				.hasArg()
				.argName("port")
				.desc("Transaq port.")
				.build(), actual);
		assertTrue(actual.hasArg());

	}

	@Test
	public void testConfigure() throws Exception {
		OptionProvider opMock = control.createMock(OptionProvider.class);
		
		Object actual = service.configure(opMock);
		
		TQTerminalConfig expected = new TQTerminalConfig(opMock);
		assertEquals(expected, actual);
	}

}
