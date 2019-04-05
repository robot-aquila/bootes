package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.BasicConfigurator;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.bootes.lib.config.kvstore.KVStoreHash;

public class BasicConfigLoaderTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private Map<String, String> data;
	private OptionProvider op, opMock;
	private BasicConfigBuilder builder;
	private BasicConfigLoader service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		data = new HashMap<>();
		op = new OptionProviderKvs(new KVStoreHash(data));
		opMock = control.createMock(OptionProvider.class);
		builder = new BasicConfigBuilder();
		service = new BasicConfigLoader();
	}
	
	@Test
	public void testLoad_OnRealData() throws Exception {
		data.put("help", "false");
		data.put("headless", "true");
		data.put("no-orders", "false");
		data.put("data-dir", "/home/test/best");
		data.put("config-file", "my-conf.ini");
		data.put("reports-dir", "my/reports");
		
		service.load(builder, op);
		
		BasicConfig actual = builder.build();
		BasicConfig expected = new BasicConfigBuilder()
			.withShowHelp(false)
			.withHeadless(true)
			.withNoOrders(false)
			.withDataDirectory(new File("/home/test/best"))
			.withConfigFile(new File("my-conf.ini"))
			.withReportsDirectory(new File("my/reports"))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoad_OnMocks() throws Exception {
		expect(opMock.getBoolean("help", false)).andReturn(true);
		expect(opMock.getBoolean("headless", false)).andReturn(false);
		expect(opMock.getBoolean("no-orders", false)).andReturn(true);
		expect(opMock.getFile("data-dir")).andReturn(new File("/foo/bar"));
		expect(opMock.getFile("config-file")).andReturn(new File("my-conf.ini"));
		Capture<File> captured_file = newCapture();
		expect(opMock.getFile(eq("reports-dir"), capture(captured_file)))
			.andReturn(new File("my/reports"));
		control.replay();
		
		service.load(builder, opMock);
		
		LocalDateTime ldt = LocalDateTime.now();
		control.verify();
		BasicConfig actual = builder.build();
		BasicConfig expected = new BasicConfigBuilder()
			.withShowHelp(true)
			.withHeadless(false)
			.withNoOrders(true)
			.withDataDirectory(new File("/foo/bar"))
			.withConfigFile(new File("my-conf.ini"))
			.withReportsDirectory(new File("my/reports"))
			.build();
		assertEquals(expected, actual);
		File expected_default_file =
				new File("reports", String.format("%4d%02d%02d%02d%02d%02d",
						ldt.getYear(),
						ldt.getMonthValue(),
						ldt.getDayOfMonth(),
						ldt.getHour(),
						ldt.getMinute(),
						ldt.getSecond()
				));
		assertEquals(expected_default_file, captured_file.getValue());
	}

	@Test
	public void testConfigureOptions() {
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
		
		actual = options.getOption("reports-dir");
		assertEquals(Option.builder()
			.longOpt("reports-dir")
			.hasArg()
			.desc("Directory to store reports. If omitted then default path "
				+ "will be used. Following template is used to build default path: "
				+ "reports/YYYTMMDDhhmmSS")
			.build(), actual);
		assertTrue(actual.hasArg());
	}

}
