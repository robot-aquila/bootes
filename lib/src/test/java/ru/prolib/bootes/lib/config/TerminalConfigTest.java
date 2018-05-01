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
				new File("foo/symbol-data"),
				new File("foo/l1-data")
			);
	}
	
	@Test
	public void testCtor() {
		assertEquals(new Account("FOO-TEST-001"), service.getQForstTestAccount());
		assertEquals(CDecimalBD.ofRUB2("150000"), service.getQForstTestBalance());
		assertEquals(new File("foo/symbol-data"), service.getQFortsSymbolDirectory());
		assertEquals(new File("foo/l1-data"), service.getQFortsL1Directory());
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
		Variant<File> vSDir = new Variant<>(vBal, new File("foo/symbol-data"), new File("foo/data.txt"));
		Variant<File> vLDir = new Variant<>(vSDir, new File("foo/l1-data"), new File("foo/vata.txt"));
		Variant<?> iterator = vLDir;
		int foundCnt = 0;
		TerminalConfig x, found = null;
		do {
			x = new TerminalConfig(vAcc.get(), vBal.get(), vSDir.get(), vLDir.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Account("FOO-TEST-001"), found.getQForstTestAccount());
		assertEquals(CDecimalBD.ofRUB2("150000"), found.getQForstTestBalance());
		assertEquals(new File("foo/symbol-data"), found.getQFortsSymbolDirectory());
		assertEquals(new File("foo/l1-data"), found.getQFortsL1Directory());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(83427, 200505)
			.append(new Account("FOO-TEST-001"))
			.append(CDecimalBD.ofRUB2("150000"))
			.append(new File("foo/symbol-data"))
			.append(new File("foo/l1-data"))
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "TerminalConfig[qfTestAccount=FOO-TEST-001,qfTestBalance=150000.00 RUB,"
				+ "qfSymbolDir=foo/symbol-data,qfL1Dir=foo/l1-data]";
		
		assertEquals(expected, service.toString());
	}

}
