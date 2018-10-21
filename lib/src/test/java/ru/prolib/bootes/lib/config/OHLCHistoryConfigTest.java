package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class OHLCHistoryConfigTest {
	private OHLCHistoryConfig service;

	@Before
	public void setUp() throws Exception {
		service = new OHLCHistoryConfig(new File("foo/bar"), new File("gaz/baz"));
	}
	
	@Test
	public void testCtor() {
		assertEquals(new File("foo/bar"), service.getDataDirectory());
		assertEquals(new File("gaz/baz"), service.getCacheDirectory());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<File> vDDir = new Variant<>(new File("foo/bar"), new File("zulu/charlie"), null);
		Variant<File> vCDir = new Variant<>(vDDir, new File("gaz/baz"), new File("foo/bar"), null);
		Variant<?> iterator = vCDir;
		int foundCnt = 0;
		OHLCHistoryConfig x, found = null;
		do {
			x = new OHLCHistoryConfig(vDDir.get(), vCDir.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new File("foo/bar"), found.getDataDirectory());
		assertEquals(new File("gaz/baz"), found.getCacheDirectory());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(71245, 11519)
			.append(new File("foo/bar"))
			.append(new File("gaz/baz"))
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String fs = File.separator;
		String expected = "OHLCHistoryConfig[dataDir=foo" + fs + "bar,cacheDir=gaz" + fs + "baz]";
		
		assertEquals(expected, service.toString());
	}

}
