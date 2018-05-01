package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

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
		service.withQFortsSymbolDirectory(new File("foo"));
		service.withQFortsL1Directory(new File("bar"));
		
		assertSame(service, service.withQFortsTestAccount(new Account("ZULU24")));
		TerminalConfig actual = service.build(basicConfig);
		assertEquals(new Account("ZULU24"), actual.getQForstTestAccount());
	}
	
	@Test
	public void testWithQFortsTestBalance() throws Exception {
		service.withQFortsSymbolDirectory(new File("foo"));
		service.withQFortsL1Directory(new File("bar"));

		assertSame(service, service.withQFortsTestBalance(CDecimalBD.ofRUB2("500000")));
		TerminalConfig actual = service.build(basicConfig);
		assertEquals(CDecimalBD.ofRUB2("500000"), actual.getQForstTestBalance());
	}
	
	@Test
	public void testWithQFortsSymbolDirectory() throws Exception {
		service.withQFortsL1Directory(new File("bar"));
		
		assertSame(service, service.withQFortsSymbolDirectory(new File("my/symbol-dir")));
		TerminalConfig actual = service.build(basicConfig);
		assertEquals(new File("my/symbol-dir"), actual.getQFortsSymbolDirectory());
	}
	
	@Test
	public void testWithQFortsL1Directory() throws Exception {
		service.withQFortsSymbolDirectory(new File("foo"));
		
		assertSame(service, service.withQFortsL1Directory(new File("my/l1-dir")));
		TerminalConfig actual = service.build(basicConfig);
		assertEquals(new File("my/l1-dir"), actual.getQFortsL1Directory());
	}
	
	@Test
	public void testBuild_Defaults() throws Exception {
		service.withQFortsSymbolDirectory(new File("foo"));
		service.withQFortsL1Directory(new File("bar"));

		TerminalConfig actual = service.build(basicConfig);
		assertEquals(new Account("TEST-ACCOUNT"), actual.getQForstTestAccount());
		assertEquals(CDecimalBD.ofRUB2("1000000"), actual.getQForstTestBalance());
	}
	
	@Test
	public void testBuild_ThrowsIfSymbolDirIsNotSpecified() throws Exception {
		service.withQFortsL1Directory(new File("bar"));
		
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("Directory of symbol data was not specified", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfL1DirIsNotSpecified() throws Exception {
		service.withQFortsSymbolDirectory(new File("foo"));
		
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("Directory of L1 data was not specified", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfTestAccountIsNotSpecified() throws Exception {
		service.withQFortsSymbolDirectory(new File("foo"));
		service.withQFortsL1Directory(new File("bar"));

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
		service.withQFortsSymbolDirectory(new File("foo"));
		service.withQFortsL1Directory(new File("bar"));

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
		service.withQFortsSymbolDirectory(new File("foo"));
		service.withQFortsL1Directory(new File("bar"));
		
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
		service.withQFortsSymbolDirectory(new File("foo"));
		service.withQFortsL1Directory(new File("bar"));
		
		service.withQFortsTestBalance(CDecimalBD.ofUSD2("1000000"));
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("Expected currency of test balance is RUB but: USD", e.getMessage());
		}
	}

}
