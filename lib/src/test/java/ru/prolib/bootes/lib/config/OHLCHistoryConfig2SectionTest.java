package ru.prolib.bootes.lib.config;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.io.File;
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

public class OHLCHistoryConfig2SectionTest {
	private IMocksControl control;
	private OHLCHistoryConfig2Section service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new OHLCHistoryConfig2Section();
	}
	
	@Test
	public void testConfigureDefaults() throws Exception {
		Map<String, String> defaults_data = new HashMap<>(), options_data = new HashMap<>();
		KVStoreHash defaults = new KVStoreHash(defaults_data);
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		options_data.put("data-dir", "fixture/my");
		
		service.configureDefaults(defaults, options);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("ohlc-data-dir", new File("fixture/my").getAbsolutePath());
		expected.put("ohlc-cache-dir", new File(System.getProperty("java.io.tmpdir"), "aquila-ohlcv-cache")
				.getAbsolutePath());
		assertEquals(expected, defaults_data);
	}

	@Test
	public void testConfigureOptions() throws Exception {
		Options options = new Options();
		
		service.configureOptions(options);
		
		assertEquals(2, options.getOptions().size());
		
		Option actual = options.getOption("ohlc-data-dir");
		assertEquals(Option.builder()
				.longOpt("ohlc-data-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of L1 data in FINAM export format.")
				.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("ohlc-cache-dir");
		assertEquals(Option.builder()
				.longOpt("ohlc-cache-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of cached OHLC data.")
				.build(), actual);
		assertTrue(actual.hasArg());
	}
	
	@Test
	public void testConfigure() throws Exception {
		OptionProvider opMock = control.createMock(OptionProvider.class);
		
		Object actual = service.configure(opMock);
		
		OHLCHistoryConfig2 expected = new OHLCHistoryConfig2(opMock);
		assertEquals(expected, actual);
	}

}
