package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.config.ConfigException;

public class TerminalConfigBuilderTest {
	private TerminalConfigBuilder service;
	private BasicConfig basicConfig; 

	@Before
	public void setUp() throws Exception {
		service = new TerminalConfigBuilder();
		basicConfig = new BasicConfigBuilder().build();
	}
	
	@Test
	public void testWithQFortsTestAccount() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));
		
		assertSame(service, service.withQFortsTestAccount(new Account("ZULU24")));
		TerminalConfig actual = service.build(basicConfig);
		assertEquals(new Account("ZULU24"), actual.getQForstTestAccount());
	}
	
	@Test
	public void testWithQFortsTestBalance() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));

		assertSame(service, service.withQFortsTestBalance(CDecimalBD.ofRUB2("500000")));
		TerminalConfig actual = service.build(basicConfig);
		assertEquals(CDecimalBD.ofRUB2("500000"), actual.getQForstTestBalance());
	}
	
	@Test
	public void testWithQFortsDataDirectory() throws Exception {
		assertSame(service, service.withQFortsDataDirectory(new File("my/symbol-dir")));
		TerminalConfig actual = service.build(basicConfig);
		assertEquals(new File("my/symbol-dir"), actual.getQFortsDataDirectory());
	}
	
	@Test
	public void testBuild_Defaults() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));

		TerminalConfig actual = service.build(basicConfig);
		assertEquals(new Account("TEST-ACCOUNT"), actual.getQForstTestAccount());
		assertEquals(CDecimalBD.ofRUB2("1000000"), actual.getQForstTestBalance());
	}
	
	@Test
	public void testBuild_ThrowsIfDataDirIsNotSpecified() throws Exception {
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("Data directory was not specified", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfTestAccountIsNotSpecified() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));

		service.withQFortsTestAccount(null);
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("Test account was not specified", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfTestBalanceIsNotSpecified() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));

		service.withQFortsTestBalance(null);
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("Test balance was not specified", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfTestBalanceIsOfWrongScale() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));
		
		service.withQFortsTestBalance(CDecimalBD.ofRUB5("1000000"));
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("Expected scale of test balance is 2 but: 5", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfTestBalanceIsOfWrongCurrency() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));
		
		service.withQFortsTestBalance(CDecimalBD.ofUSD2("1000000"));
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("Expected currency of test balance is RUB but: USD", e.getMessage());
		}
	}

}
