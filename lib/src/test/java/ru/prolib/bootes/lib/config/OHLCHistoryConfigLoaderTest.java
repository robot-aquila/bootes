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

import ru.prolib.bootes.lib.config.kvstore.KVStoreHash;

public class OHLCHistoryConfigLoaderTest {
	private IMocksControl control;
	private Map<String, String> data;
	private OptionProvider op, opMock;
	private BasicConfig basicConfig;
	private OHLCHistoryConfigBuilder builder;
	private OHLCHistoryConfigLoader service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		data = new HashMap<>();
		op = new OptionProviderKvs(new KVStoreHash(data));
		opMock = control.createMock(OptionProvider.class);
		basicConfig = new BasicConfigBuilder().withDataDirectory(new File("foo/bar")).build();
		builder = new OHLCHistoryConfigBuilder();
		service = new OHLCHistoryConfigLoader();
	}
	
	@Test
	public void testLoad_OnRealData() throws Exception {
		data.put("ohlc-data-dir", "/my/path/data");
		data.put("ohlc-cache-dir", "/my/path/cache");
		
		service.load(builder, op, basicConfig);
		
		OHLCHistoryConfig actual = builder.build(basicConfig);
		OHLCHistoryConfig expected = new OHLCHistoryConfigBuilder()
			.withDataDirectory(new File("/my/path/data"))
			.withCacheDirectory(new File("/my/path/cache"))
			.build(basicConfig);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoad_OnMocks() throws Exception {
		expect(opMock.getFileNotNull("ohlc-data-dir", new File("foo/bar"))).andReturn(new File("/path/zulu24"));
		expect(opMock.getFileNotNull("ohlc-cache-dir")).andReturn(new File("/path/charlie"));
		control.replay();
		
		service.load(builder, opMock, basicConfig);
		
		control.verify();
		OHLCHistoryConfig actual = builder.build(basicConfig);
		OHLCHistoryConfig expected = new OHLCHistoryConfigBuilder()
			.withDataDirectory(new File("/path/zulu24"))
			.withCacheDirectory(new File("/path/charlie"))
			.build(basicConfig);
		assertEquals(expected, actual);
	}

	@Test
	public void testConfigureOptions() {
		Options options = new Options();
		
		service.configureOptions(options);
		
		assertEquals(2, options.getOptions().size());
		assertEquals(Option.builder()
				.longOpt("ohlc-data-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of L1 data in FINAM export format.")
				.build(), options.getOption("ohlc-data-dir"));
		assertEquals(Option.builder()
				.longOpt("ohlc-cache-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of cached OHLC data.")
				.build(), options.getOption("ohlc-cache-dir"));
	}

}
