package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;

public class TerminalConfigLoaderTest {
	private IMocksControl control;
	private Map<String, String> data;
	private OptionProvider op, opMock;
	private BasicConfig basicConfig;
	private TerminalConfigBuilder builder;
	private TerminalConfigLoader service;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		data = new HashMap<>();
		op = new OptionProviderKvs(new KVStoreHash(data));
		opMock = control.createMock(OptionProvider.class);
		basicConfig = new BasicConfigBuilder().withDataDirectory(new File("/my/data")).build();
		builder = new TerminalConfigBuilder();
		service = new TerminalConfigLoader();
	}
	
	@Test
	public void testLoad_OnRealData() throws Exception {
		data.put("driver", "transaq");
		data.put("qforts-data-dir", "/path/data");
		data.put("qforts-test-account", "ZOO-215");
		data.put("qforts-test-balance", "550000");
		data.put("transaq-log-path", "/bambr/two");
		data.put("transaq-log-level", "1");
		data.put("transaq-login", "username");
		data.put("transaq-password", "12345");
		data.put("transaq-host", "localhost");
		data.put("transaq-port", "3194");
		
		service.load(builder, op, basicConfig);
		
		TerminalConfig actual = builder.build(basicConfig);
		
		TerminalConfig expected = new TerminalConfig(
				"transaq",
				new Account("ZOO-215"),
				ofRUB2("550000"),
				new File("/path/data"),
				new File("/bambr/two"),
				1,
				"username",
				"12345",
				"localhost",
				3194
			);
		assertEquals(expected, actual);
	}

	@Test
	public void testLoad_OnMocks() throws Exception {
		expect(opMock.getFileNotNull("qforts-data-dir", new File("/my/data"))).andReturn(new File("/path/data"));
		expect(opMock.getStringNotNull("qforts-test-account", "QFORTS-TEST")).andReturn("ZULU24");
		expect(opMock.getStringNotNull("qforts-test-balance", "1000000")).andReturn("28000");
		expect(opMock.getStringNotNull("driver", "default")).andReturn("qforts");
		expect(opMock.getFile("transaq-log-path")).andReturn(new File("/super/duper"));
		expect(opMock.getIntegerPositiveNotNull("transaq-log-level", 0)).andReturn(1);
		expect(opMock.getString("transaq-login")).andReturn("Buzz");
		expect(opMock.getString("transaq-password")).andReturn("Lightyear");
		expect(opMock.getString("transaq-host")).andReturn("Toystory");
		expect(opMock.getIntegerPositiveNonZeroNotNull("transaq-port", 3900)).andReturn(9511);
		control.replay();
		
		service.load(builder, opMock, basicConfig);
		
		control.verify();
		TerminalConfig actual = builder.build(basicConfig);
		TerminalConfig expected = new TerminalConfig(
				"qforts",
				new Account("ZULU24"),
				ofRUB2("28000"),
				new File("/path/data"),
				new File("/super/duper"),
				1,
				"Buzz",
				"Lightyear",
				"Toystory",
				9511
			);
		assertEquals(expected, actual);
	}

	@Test
	public void testConfigureOptions() {
		Options options = new Options();
		
		service.configureOptions(options);
		
		assertEquals(10, options.getOptions().size());
		
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
		
		actual = options.getOption("driver");
		assertEquals(Option.builder()
				.longOpt("driver")
				.hasArg()
				.argName("driverID")
				.desc("Terminal driver ID.")
				.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("transaq-log-path");
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

}
