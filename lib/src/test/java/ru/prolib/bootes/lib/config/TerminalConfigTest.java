package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.utils.Variant;

public class TerminalConfigTest {
	private TerminalConfig service;

	@Before
	public void setUp() throws Exception {
		service = new TerminalConfig(
				new Account("FOO-TEST-001"),
				CDecimalBD.ofRUB2("150000"),
				new File("foo/data")
			);
	}
	
	@Test
	public void testCtor() {
		assertEquals(new Account("FOO-TEST-001"), service.getQForstTestAccount());
		assertEquals(CDecimalBD.ofRUB2("150000"), service.getQForstTestBalance());
		assertEquals(new File("foo/data"), service.getQFortsDataDirectory());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<Account> vAcc = new Variant<>(new Account("FOO-TEST-001"), new Account("BAR-12"));
		Variant<CDecimal> vBal = new Variant<>(vAcc, CDecimalBD.ofRUB2("150000"), CDecimalBD.ofRUB2("300000"));
		Variant<File> vDDir = new Variant<>(vBal, new File("foo/data"), new File("foo/data.txt"));
		Variant<?> iterator = vDDir;
		int foundCnt = 0;
		TerminalConfig x, found = null;
		do {
			x = new TerminalConfig(vAcc.get(), vBal.get(), vDDir.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Account("FOO-TEST-001"), found.getQForstTestAccount());
		assertEquals(CDecimalBD.ofRUB2("150000"), found.getQForstTestBalance());
		assertEquals(new File("foo/data"), found.getQFortsDataDirectory());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(83427, 200505)
			.append(new Account("FOO-TEST-001"))
			.append(CDecimalBD.ofRUB2("150000"))
			.append(new File("foo/data"))
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "TerminalConfig[qfTestAccount=FOO-TEST-001,qfTestBalance=150000.00 RUB,qfDataDir=foo/data]";
		
		assertEquals(expected, service.toString());
	}

}
