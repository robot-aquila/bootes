package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.config.kvstore.KVStoreHash;

public class BasicConfigLoaderTest {
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
		data.put("data-dir", "/home/test/best");
		data.put("config-file", "my-conf.ini");
		
		service.load(builder, op);
		
		BasicConfig actual = builder.build();
		BasicConfig expected = new BasicConfigBuilder()
			.withShowHelp(false)
			.withHeadless(true)
			.withDataDirectory(new File("/home/test/best"))
			.withConfigFile(new File("my-conf.ini"))
			.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoad_OnMocks() throws Exception {
		expect(opMock.getBoolean("help", false)).andReturn(true);
		expect(opMock.getBoolean("headless", false)).andReturn(false);
		expect(opMock.getFile("data-dir")).andReturn(new File("/foo/bar"));
		expect(opMock.getFile("config-file")).andReturn(new File("my-conf.ini"));
		control.replay();
		
		service.load(builder, opMock);
		
		control.verify();
		BasicConfig actual = builder.build();
		BasicConfig expected = new BasicConfigBuilder()
			.withShowHelp(true)
			.withHeadless(false)
			.withDataDirectory(new File("/foo/bar"))
			.withConfigFile(new File("my-conf.ini"))
			.build();
		assertEquals(expected, actual);
	}

	@Test
	public void testConfigureOptions() {
		Options options = new Options();
		
		service.configureOptions(options);
		
		assertEquals(4, options.getOptions().size());
		
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
	}

}
