package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.time.LocalDateTime;
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

public class BasicConfig2SectionTest {
	private IMocksControl control;
	private BasicConfig2Section service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new BasicConfig2Section();
	}
	
	@Test
	public void testConfigureDefaults() throws Exception {
		Map<String, String> defaults_data = new HashMap<>(), options_data = new HashMap<>();
		KVStoreHash defaults = new KVStoreHash(defaults_data);
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		
		service.configureDefaults(defaults, options);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("help", "0");
		expected.put("headless", "0");
		expected.put("no-orders", "0");
		LocalDateTime ldt = LocalDateTime.now();
		expected.put("report-dir", new File("reports", String.format("%4d%02d%02d%02d%02d%02d",
				ldt.getYear(),
				ldt.getMonthValue(),
				ldt.getDayOfMonth(),
				ldt.getHour(),
				ldt.getMinute(),
				ldt.getSecond()
			)).getAbsolutePath());
		assertEquals(expected, defaults_data);
	}
	
	@Test
	public void testConfigureOptions() throws Exception {
		Options options = new Options();
		
		service.configureOptions(options);
		
		assertEquals(6, options.getOptions().size());
		
		Option actual = options.getOption("help");
		assertEquals(Option.builder()
			.longOpt("help")
			.desc("Show help.")
			.build(), actual);
		assertFalse(actual.hasArg());
		
		actual = options.getOption("headless");
		assertEquals(Option.builder()
			.longOpt("headless")
			.desc("Enable headless mode.")
			.build(), actual);
		assertFalse(actual.hasArg());
		
		actual = options.getOption("no-orders");
		assertEquals(Option.builder()
			.longOpt("no-orders")
			.desc("Avoid to use orders where possible. Use simulated results instead.")
			.build(), actual);
		assertFalse(actual.hasArg());
		
		actual = options.getOption("data-dir");
		assertEquals(Option.builder()
			.longOpt("data-dir")
			.hasArg()
			.desc("Data directory by default. This may be used as an alternative for some other options.")
			.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("config-file");
		assertEquals(Option.builder()
			.longOpt("config-file")
			.hasArg() 
			.desc("Path to configuration file. All settings from this file will be loaded prior to applying "
				+ "other command line options. Options passed via command line have higher priority and will "
				+ "override options of configuration file.")
			.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("report-dir");
		assertEquals(Option.builder()
			.longOpt("report-dir")
			.hasArg()
			.desc("Directory to store reports. If omitted then default path "
				+ "will be used. Following template is used to build default path: "
				+ "reports/YYYTMMDDhhmmSS")
			.build(), actual);
		assertTrue(actual.hasArg());
	}

	@Test
	public void testConfigure() throws Exception {
		OptionProvider opMock = control.createMock(OptionProvider.class);
		
		Object actual = service.configure(opMock);
		
		BasicConfig2 expected = new BasicConfig2(opMock);
		assertEquals(expected, actual);
	}

}
