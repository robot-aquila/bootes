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

public class QFTerminalConfigSectionTest {
	private IMocksControl control;
	private QFTerminalConfigSection service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new QFTerminalConfigSection();
	}
	
	@Test
	public void testConfigureDefaults() throws Exception {
		Map<String, String> defaults_data = new HashMap<>(), options_data = new HashMap<>();
		KVStoreHash defaults = new KVStoreHash(defaults_data);
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		options_data.put("data-dir", "/foo/bar");
		
		service.configureDefaults(defaults, options);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("qforts-test-account", "QFORTS-TEST");
		expected.put("qforts-test-balance", "1000000.00");
		expected.put("qforts-data-dir", "/foo/bar");
		assertEquals(expected, defaults_data);
	}
	
	@Test
	public void testConfigureOptions() throws Exception {
		Options options = new Options();
		
		service.configureOptions(options);
		
		assertEquals(3, options.getOptions().size());
		
		Option actual = options.getOption("qforts-data-dir");
		assertEquals(Option.builder()
				.longOpt("qforts-data-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of combined storage of L1 and symbol data.")
				.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("qforts-test-account");
		assertEquals(Option.builder()
				.longOpt("qforts-test-account")
				.hasArg()
				.argName("code")
				.desc("Code of test account.")
				.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("qforts-test-balance");
		assertEquals(Option.builder()
				.longOpt("qforts-test-balance")
				.hasArg()
				.argName("amount")
				.desc("Initial balance of test account in RUB.")
				.build(), actual);
		assertTrue(actual.hasArg());
	}
	
	@Test
	public void testConfigure() throws Exception {
		OptionProvider opMock = control.createMock(OptionProvider.class);
		
		Object actual = service.configure(opMock);
		
		QFTerminalConfig expected = new QFTerminalConfig(opMock);
		assertEquals(expected, actual);
	}

}
