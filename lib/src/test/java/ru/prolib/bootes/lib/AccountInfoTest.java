package ru.prolib.bootes.lib;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class AccountInfoTest {
	private static final Account ACC1 = new Account("foo");
	private static final Account ACC2 = new Account("bar");
	private AccountInfo service;

	@Before
	public void setUp() throws Exception {
		service = new AccountInfo(ACC1, ofRUB2("1886.12"));
	}
	
	@Test
	public void testCtor2() {
		assertEquals(ACC1, service.getAccount());
		assertEquals(ofRUB2("1886.12"), service.getBalance());
	}
	
	@Test
	public void testCtor1() {
		service = new AccountInfo(ACC2);
		assertEquals(ACC2, service.getAccount());
		assertNull(service.getBalance());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(76242347, 991)
				.append(ACC1)
				.append(ofRUB2("1886.12"))
				.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Account> vAcc = new Variant<>(ACC1, ACC2);
		Variant<CDecimal> vBal = new Variant<>(vAcc, ofRUB2("1886.12"), ofUSD2("12.47"));
		Variant<?> iterator = vBal;
		int foundCnt = 0;
		AccountInfo x, found = null;
		do {
			x = new AccountInfo(vAcc.get(), vBal.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(ACC1, found.getAccount());
		assertEquals(ofRUB2("1886.12"), found.getBalance());
	}

	@Test
	public void testToString() {
		assertEquals("AccountInfo[account=foo,balance=1886.12 RUB]", service.toString());
		
		service = new AccountInfo(ACC2);
		assertEquals("AccountInfo[account=bar,balance=<null>]", service.toString());
	}

}
