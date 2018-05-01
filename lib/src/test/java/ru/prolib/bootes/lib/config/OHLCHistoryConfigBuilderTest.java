package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class OHLCHistoryConfigBuilderTest {
	private OHLCHistoryConfigBuilder service;
	private BasicConfig basicConfig;

	@Before
	public void setUp() throws Exception {
		service = new OHLCHistoryConfigBuilder();
		basicConfig = new BasicConfigBuilder().build();
	}

	@Test
	public void testBuild_ThrowsIfDataDirectoryUndefined() throws Exception {
		try {
			service.withCacheDirectory(new File("foo/cache")).build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("OHLC history data directory must be specified", e.getMessage());
		}
	}

	@Test
	public void testBuild_ThrowsIfCacheDirectoryUndefined() throws Exception {
		try {
			service.withDataDirectory(new File("foo/data")).build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("OHLC history cache directory must be specified", e.getMessage());
		}
	}

	@Test
	public void testBuild() throws Exception {
		assertSame(service, service.withDataDirectory(new File("foo/data")));
		assertSame(service, service.withCacheDirectory(new File("foo/cache")));
		
		OHLCHistoryConfig actual = service.build(basicConfig);
		
		assertEquals(new File("foo/data"), actual.getDataDirectory());
		assertEquals(new File("foo/cache"), actual.getCacheDirectory());
	}

}
