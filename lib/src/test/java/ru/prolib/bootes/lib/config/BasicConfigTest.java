package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class BasicConfigTest {
	private BasicConfig service;

	@Before
	public void setUp() throws Exception {
		service = new BasicConfig(false, true, new File("foo/bar"), new File("my-conf.ini"));
	}
	
	@Test
	public void testCtor() {
		assertFalse(service.isShowHelp());
		assertTrue(service.isHeadless());
		assertEquals(new File("foo/bar"), service.getDataDirectory());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Boolean> vIsHelp = new Variant<>(false, true);
		Variant<Boolean> vIsHdls = new Variant<>(vIsHelp, true, false);
		Variant<File> vDDir = new Variant<>(vIsHdls, new File("foo/bar"), null, new File("bar/foo"));
		Variant<File> vFCfg = new Variant<>(vDDir, new File("my-conf.ini"), new File("old-conf.ini"));
		Variant<?> iterator = vFCfg;
		int foundCnt = 0;
		BasicConfig x, found = null;
		do {
			x = new BasicConfig(vIsHelp.get(), vIsHdls.get(), vDDir.get(), vFCfg.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertFalse(found.isShowHelp());
		assertTrue(found.isHeadless());
		assertEquals(new File("foo/bar"), found.getDataDirectory());
		assertEquals(new File("my-conf.ini"), found.getConfigFile());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(7117529, 995)
			.append(false)
			.append(true)
			.append(new File("foo/bar"))
			.append(new File("my-conf.ini"))
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String fs = File.separator;
		String expected = "BasicConfig[showHelp=false,headless=true,dataDir=foo" + fs + "bar,configFile=my-conf.ini]";
		
		assertEquals(expected, service.toString());
	}

}
