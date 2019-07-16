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
				"default",
				new Account("FOO-TEST-001"),
				CDecimalBD.ofRUB2("150000"),
				new File("foo/data"),
				new File("D:\\phantom\\logs"),
				2,
				"user",
				"12345",
				"localhost",
				5190
			);
	}
	
	@Test
	public void testCtor() {
		assertEquals("default", service.getDriverID());
		assertEquals(new Account("FOO-TEST-001"), service.getQForstTestAccount());
		assertEquals(CDecimalBD.ofRUB2("150000"), service.getQForstTestBalance());
		assertEquals(new File("foo/data"), service.getQFortsDataDirectory());
		assertEquals(new File("D:\\phantom\\logs"), service.getTransaqLogPath());
		assertEquals(2, service.getTransaqLogLevel());
		assertEquals("user", service.getTransaqLogin());
		assertEquals("12345", service.getTransaqPassword());
		assertEquals("localhost", service.getTransaqHost());
		assertEquals(5190, service.getTransaqPort());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<String> vDrv = new Variant<>("default", "qforts");
		Variant<Account> vQFAcc = new Variant<>(vDrv, new Account("FOO-TEST-001"), new Account("BAR-12"));
		Variant<CDecimal> vQFBal = new Variant<>(vQFAcc, CDecimalBD.ofRUB2("150000"), CDecimalBD.ofRUB2("300000"));
		Variant<File> vQFDir = new Variant<>(vQFBal, new File("foo/data"), new File("foo/data.txt"));
		Variant<File> vTQLogPath = new Variant<File>(vQFDir)
				.add(new File("D:\\phantom\\logs"))
				.add(new File("C:\\paramount\\caboose"));
		Variant<Integer> vTQLogLevel = new Variant<Integer>(vTQLogPath, 2, 0);
		Variant<String> vTQLogin = new Variant<>(vTQLogLevel, "user", "boo");
		Variant<String> vTQPass = new Variant<>(vTQLogin, "12345", "tutumbr");
		Variant<String> vTQHost = new Variant<>(vTQPass, "localhost", "remote");
		Variant<Integer> vTQPort = new Variant<>(vTQHost, 5190, 21355);
		Variant<?> iterator = vTQPort;
		int foundCnt = 0;
		TerminalConfig x, found = null;
		do {
			x = new TerminalConfig(
					vDrv.get(),
					vQFAcc.get(),
					vQFBal.get(),
					vQFDir.get(),
					vTQLogPath.get(),
					vTQLogLevel.get(),
					vTQLogin.get(),
					vTQPass.get(),
					vTQHost.get(),
					vTQPort.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("default", found.getDriverID());
		assertEquals(new Account("FOO-TEST-001"), found.getQForstTestAccount());
		assertEquals(CDecimalBD.ofRUB2("150000"), found.getQForstTestBalance());
		assertEquals(new File("foo/data"), found.getQFortsDataDirectory());
		assertEquals(new File("D:\\phantom\\logs"), found.getTransaqLogPath());
		assertEquals(2, found.getTransaqLogLevel());
		assertEquals("user", found.getTransaqLogin());
		assertEquals("12345", found.getTransaqPassword());
		assertEquals("localhost", found.getTransaqHost());
		assertEquals(5190, found.getTransaqPort());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(83427, 200505)
			.append("default")
			.append(new Account("FOO-TEST-001"))
			.append(CDecimalBD.ofRUB2("150000"))
			.append(new File("foo/data"))
			.append(new File("D:\\phantom\\logs"))
			.append(2)
			.append("user")
			.append("12345")
			.append("localhost")
			.append(5190)
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String fs = File.separator;
		String expected = new StringBuilder()
				.append("TerminalConfig[")
				.append("driverID=default,")
				.append("qfTestAccount=FOO-TEST-001,")
				.append("qfTestBalance=150000.00 RUB,")
				.append("qfDataDir=foo" + fs + "data,")
				.append("tqLogPath=D:" + fs + "phantom" + fs + "logs,")
				.append("tqLogLevel=2,")
				.append("tqLogin=user,")
				.append("tqPassword=***,")
				.append("tqHost=localhost,")
				.append("tqPort=5190")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}

}
