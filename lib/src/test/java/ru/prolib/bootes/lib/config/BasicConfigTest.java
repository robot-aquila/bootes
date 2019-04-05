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
		service = new BasicConfig(false,
				true,
				true,
				new File("foo/bar"),
				new File("my-conf.ini"),
				new File("reports")
			);
	}
	
	@Test
	public void testCtor() {
		assertFalse(service.isShowHelp());
		assertTrue(service.isHeadless());
		assertTrue(service.isNoOrders());
		assertEquals(new File("foo/bar"), service.getDataDirectory());
		assertEquals(new File("my-conf.ini"), service.getConfigFile());
		assertEquals(new File("reports"), service.getReportsDirectory());
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
		Variant<Boolean> vIsNoOrd = new Variant<>(vIsHdls, true, false);
		Variant<File> vDDir = new Variant<>(vIsNoOrd, new File("foo/bar"), null, new File("bar/foo"));
		Variant<File> vFCfg = new Variant<>(vDDir, new File("my-conf.ini"), new File("old-conf.ini"));
		Variant<File> vRDir = new Variant<>(vFCfg, new File("reports"), new File("my/reports"));
		Variant<?> iterator = vRDir;
		int foundCnt = 0;
		BasicConfig x, found = null;
		do {
			x = new BasicConfig(
					vIsHelp.get(),
					vIsHdls.get(),
					vIsNoOrd.get(),
					vDDir.get(),
					vFCfg.get(),
					vRDir.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertFalse(found.isShowHelp());
		assertTrue(found.isHeadless());
		assertTrue(found.isNoOrders());
		assertEquals(new File("foo/bar"), found.getDataDirectory());
		assertEquals(new File("my-conf.ini"), found.getConfigFile());
		assertEquals(new File("reports"), found.getReportsDirectory());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(7117529, 995)
			.append(false)
			.append(true)
			.append(true)
			.append(new File("foo/bar"))
			.append(new File("my-conf.ini"))
			.append(new File("reports"))
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("BasicConfig[")
				.append("showHelp=false,")
				.append("headless=true,")
				.append("noOrders=true,")
				.append("dataDir=foo").append(File.separator).append("bar,")
				.append("configFile=my-conf.ini,")
				.append("reportsDir=reports")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}

}
