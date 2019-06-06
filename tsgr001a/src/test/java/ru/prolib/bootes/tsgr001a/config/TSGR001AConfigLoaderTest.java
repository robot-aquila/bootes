package ru.prolib.bootes.tsgr001a.config;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.bootes.lib.config.BasicConfig;
import ru.prolib.bootes.lib.config.BasicConfigBuilder;

public class TSGR001AConfigLoaderTest {
	private IMocksControl control;
	private OptionProvider opMock;
	private BasicConfig basicConfig;
	private TSGR001AConfigBuilder builderMock;
	private TSGR001AConfigLoader service;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		opMock = control.createMock(OptionProvider.class);
		builderMock = control.createMock(TSGR001AConfigBuilder.class);
		basicConfig = new BasicConfigBuilder().build();
		service = new TSGR001AConfigLoader();
	}
	
	@Test
	public void testConfigureOptions() {
		Options options = new Options();
		
		service.configureOptions(options);

		assertEquals(1, options.getOptions().size());
		Option actual = options.getOption("tsgr001a-inst-config");
		assertEquals(Option.builder()
			.longOpt("tsgr001a-inst-config")
			.desc("Path to TSGR001A instances configuration file")
			.hasArg()
			//.required()
			.build(), actual);
		assertTrue(actual.hasArg());
		assertFalse(actual.isRequired());
	}
	
	@Test
	public void testLoad_OnMocks() throws Exception {
		expect(opMock.getFile("tsgr001a-inst-config")).andReturn(new File("xxx"));
		expect(builderMock.withInstancesConfig(new File("xxx"))).andReturn(builderMock);
		control.replay();
		
		service.load(builderMock, opMock, basicConfig);
		
		control.verify();
	}

}
