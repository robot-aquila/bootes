package ru.prolib.bootes.tsgr001a.config;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.bootes.tsgr001a.TSGR001AInstConfig;

public class TSGR001AConfigBuilderTest {
	private TSGR001AConfigBuilder service;

	@Before
	public void setUp() throws Exception {
		service = new TSGR001AConfigBuilder();
	}

	@Test
	public void testBuild() throws Exception {
		assertSame(service, service.withInstancesConfig(new File("fixture/test-inst-conf.ini")));
		
		TSGR001AConfig actual = service.build();
		
		List<TSGR001AInstConfig> expected_list = new ArrayList<>();
		expected_list.add(new TSGR001AInstConfig(
				new Account("TSGR001A-A"),
				"MY-CONFIG-1",
				"foo, bar, buz",
				"Hello, my config 1"
			));
		expected_list.add(new TSGR001AInstConfig(
				new Account("TSGR001A-B"),
				"MY-CONFIG-2",
				"",
				"foobar"
			));
		expected_list.add(new TSGR001AInstConfig(
				new Account("TSGR001A-D"),
				"MY-CONFIG-4",
				"SLgtATR",
				"common text"
			));
		assertEquals(expected_list, actual.getListOfInstances());
	}

}
