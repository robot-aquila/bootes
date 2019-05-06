package ru.prolib.bootes.tsgr001a;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.utils.Variant;

public class TSGR001AConfigTest {
	private static Account ACC1 = new Account("foo");
	private static Account ACC2 = new Account("bar");
	private TSGR001AConfig service;

	@Before
	public void setUp() throws Exception {
		service = new TSGR001AConfig(ACC1, "zulu", "charlie", "barbie");
	}

	@Test
	public void testCtor4() {
		assertEquals(ACC1, service.getAccount());
		assertEquals("zulu", service.getTitle());
		assertEquals("charlie", service.getFilterDefs());
		assertEquals("barbie", service.getReportHeader());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Account> vACC = new Variant<>(ACC1, ACC2);
		Variant<String>
			vTTL = new Variant<>(vACC, "zulu", "moon44"),
			vFLD = new Variant<>(vTTL, "charlie", "zumba"),
			vHDR = new Variant<>(vFLD, "barbie", "ken");
		Variant<?> iterator = vHDR;
		int foundCnt = 0;
		TSGR001AConfig x, found = null;
		do {
			x = new TSGR001AConfig(vACC.get(), vTTL.get(), vFLD.get(), vHDR.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(ACC1, found.getAccount());
		assertEquals("zulu", found.getTitle());
		assertEquals("charlie", found.getFilterDefs());
		assertEquals("barbie", found.getReportHeader());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(18274929, 5263)
				.append(ACC1)
				.append("zulu")
				.append("charlie")
				.append("barbie")
				.build();
		
		assertEquals(expected, service.hashCode());
	}

}
