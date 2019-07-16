package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

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
	public void testWithDriverID() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));
		
		assertSame(service, service.withDriverID("qforts"));
		
		TerminalConfig actual = service.build(basicConfig);
		assertEquals("qforts", actual.getDriverID());
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
	public void testBuild_TransaqDriver() throws Exception {
		service.withDriverID("transaq");
		assertSame(service, service.withTransaqLogPath(new File("D:\\temp\\transaq")));
		assertSame(service, service.withTransaqLogLevel(2));
		assertSame(service, service.withTransaqLogin("vasya"));
		assertSame(service, service.withTransaqPassword("foobar"));
		assertSame(service, service.withTransaqHost("sampje"));
		assertSame(service, service.withTransaqPort(7707));
		
		TerminalConfig actual = service.build(basicConfig);
		
		TerminalConfig expected = new TerminalConfig(
				"transaq",
				new Account("TEST-ACCOUNT"),
				ofRUB2("1000000"),
				null,
				new File("D:\\temp\\transaq"),
				2,
				"vasya",
				"foobar",
				"sampje",
				7707
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuild_Defaults() throws Exception {
		service.withQFortsDataDirectory(new File("foo"));

		TerminalConfig actual = service.build(basicConfig);
		
		TerminalConfig expected = new TerminalConfig(
				"default",
				new Account("TEST-ACCOUNT"),
				ofRUB2("1000000"),
				new File("foo"),
				null,
				0,
				null,
				null,
				null,
				3900
			);
		assertEquals(expected, actual);
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
	
	@Test
	public void testBuild_Transaq_ThrowsIfLogPathWasNotSpecified() throws Exception {
		service.withDriverID("transaq")
			.withTransaqLogin("foo")
			.withTransaqPassword("bar")
			.withTransaqHost("buz");
		
		try {
			service.build(basicConfig);
			fail("Expected: " + ConfigException.class.getSimpleName());
		} catch ( ConfigException e ) {
			assertEquals("Transaq log path was not defined", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_Transaq_ThrowsIfLoginWasNotSpecified() throws Exception {
		service.withDriverID("transaq")
			.withTransaqLogPath(new File("D:\\foo\\bar"))
			.withTransaqPassword("12345")
			.withTransaqHost("domain.met");
		
		try {
			service.build(basicConfig);
			fail("Expected: " + ConfigException.class.getSimpleName());
		} catch ( ConfigException e ) {
			assertEquals("Transaq login was not defined", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_Transaq_ThrowsIfPasswordWasNotSpecified() throws Exception {
		service.withDriverID("transaq")
			.withTransaqLogPath(new File("D:\\foo\\bar"))
			.withTransaqLogin("foo")
			.withTransaqHost("domain.met");

		try {
			service.build(basicConfig);
			fail("Expected: " + ConfigException.class.getSimpleName());
		} catch ( ConfigException e ) {
			assertEquals("Transaq password was not defined", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_Transaq_ThrowsIfHostWasNotSpecified() throws Exception {
		service.withDriverID("transaq")
			.withTransaqLogPath(new File("D:\\foo\\bar"))
			.withTransaqLogin("foo")
			.withTransaqPassword("12345");

		try {
			service.build(basicConfig);
			fail("Expected: " + ConfigException.class.getSimpleName());
		} catch ( ConfigException e ) {
			assertEquals("Transaq host was not defined", e.getMessage());
		}
	}

}
