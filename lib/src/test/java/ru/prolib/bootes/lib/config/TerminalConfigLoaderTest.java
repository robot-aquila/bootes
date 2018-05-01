package ru.prolib.bootes.lib.config;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.bootes.lib.config.kvstore.KVStoreHash;

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
		data.put("qforts-l1-dir", "/path/data");
		data.put("qforts-symbol-dir", "/path/symbol");
		data.put("qforts-test-account", "ZOO-215");
		data.put("qforts-test-balance", "550000");
		
		service.load(builder, op, basicConfig);
		
		TerminalConfig actual = builder.build(basicConfig);
		TerminalConfig expected = new TerminalConfigBuilder()
			.withQFortsL1Directory(new File("/path/data"))
			.withQFortsSymbolDirectory(new File("/path/symbol"))
			.withQFortsTestAccount(new Account("ZOO-215"))
			.withQFortsTestBalance(CDecimalBD.ofRUB2("550000"))
			.build(basicConfig);
		assertEquals(expected, actual);
	}

	@Test
	public void testLoad_OnMocks() throws Exception {
		expect(opMock.getFileNotNull("qforts-l1-dir", new File("/my/data"))).andReturn(new File("/path/data"));
		expect(opMock.getFileNotNull("qforts-symbol-dir", new File("/my/data"))).andReturn(new File("/path/symbol"));
		expect(opMock.getStringNotNull("qforts-test-account", "QFORTS-TEST")).andReturn("ZULU24");
		expect(opMock.getStringNotNull("qforts-test-balance", "1000000")).andReturn("28000");
		control.replay();
		
		service.load(builder, opMock, basicConfig);
		
		control.verify();
		TerminalConfig actual = builder.build(basicConfig);
		TerminalConfig expected = new TerminalConfigBuilder()
			.withQFortsL1Directory(new File("/path/data"))
			.withQFortsSymbolDirectory(new File("/path/symbol"))
			.withQFortsTestAccount(new Account("ZULU24"))
			.withQFortsTestBalance(CDecimalBD.ofRUB2("28000"))
			.build(basicConfig);
		assertEquals(expected, actual);
	}

	@Test
	public void testConfigureOptions() {
		Options options = new Options();
		
		service.configureOptions(options);
		
		assertEquals(4, options.getOptions().size());
		assertEquals(Option.builder()
				.longOpt("qforts-l1-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of L1 data in FINAM export format.")
				.build(), options.getOption("qforts-l1-dir"));
		assertEquals(Option.builder()
				.longOpt("qforts-symbol-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of symbol data in MOEX export format.")
				.build(), options.getOption("qforts-symbol-dir"));
		assertEquals(Option.builder()
				.longOpt("qforts-test-account")
				.hasArg()
				.argName("code")
				.desc("Code of test account.")
				.build(), options.getOption("qforts-test-account"));
		assertEquals(Option.builder()
				.longOpt("qforts-test-balance")
				.hasArg()
				.argName("amount")
				.desc("Initial balance of test account in RUB.")
				.build(), options.getOption("qforts-test-balance"));
	}

}
