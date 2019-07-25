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

public class OHLCHistoryConfig2Test {
	
	@Rule
	public ExpectedException eex = ExpectedException.none();

	private IMocksControl control;
	private Map<String, String> options1, options2;
	private OHLCHistoryConfig2 service1, service2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service1 = new OHLCHistoryConfig2(new OptionProviderKvs(new KVStoreHash(options1 = new LinkedHashMap<>())));
		service2 = new OHLCHistoryConfig2(new OptionProviderKvs(new KVStoreHash(options2 = new LinkedHashMap<>())));
	}
	
	@Test
	public void testGetDataDirectory() throws Exception {
		options1.put("ohlc-data-dir", "C:/cucumber");
		options2.put("ohlc-data-dir", "fixture/my/dir");
		
		assertEquals(new File("C:/cucumber"), service1.getDataDirectory());
		assertEquals(new File("fixture/my/dir"), service2.getDataDirectory());
	}
	
	@Test
	public void testGetDataDirectory_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("ohlc-data-dir option expected to be not null");
		
		service1.getDataDirectory();
	}
	
	@Test
	public void testGetCacheDirectory() throws Exception {
		options1.put("ohlc-cache-dir", "/temp/caramucha");
		options2.put("ohlc-cache-dir", "tutubmr");
		
		assertEquals(new File("/temp/caramucha"), service1.getCacheDirectory());
		assertEquals(new File("tutubmr"), service2.getCacheDirectory());
	}
	
	@Test
	public void testGetCacheDirectory_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("ohlc-cache-dir option expected to be not null");
		
		service2.getCacheDirectory();
	}

	@Test
	public void testEquals() {
		OptionProvider
			opMock1 = control.createMock(OptionProvider.class),
			opMock2 = control.createMock(OptionProvider.class);
		OHLCHistoryConfig2 service = new OHLCHistoryConfig2(opMock1);
		
		assertTrue(service.equals(service));
		assertTrue(service.equals(new OHLCHistoryConfig2(opMock1)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new OHLCHistoryConfig2(opMock2)));
	}

}
