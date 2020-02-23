package ru.prolib.bootes.lib.app;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.config.BasicConfig2;
import ru.prolib.bootes.lib.config.ConfigSection;

public class AppConfigService2Test {
	
	static class ConfigSection1Data {
		private final OptionProvider op;
		
		public ConfigSection1Data(OptionProvider op) {
			this.op = op;
		}
		
		public String getKappa() {
			return op.getString("kappa");
		}
		
		public Integer getBekta() throws ConfigException {
			return op.getInteger("bekta");
		}
		
	}

	static class ConfigSection1 implements ConfigSection {

		@Override
		public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
			defaults.add("kappa", "bormental");
			defaults.add("bekta", "1");
		}

		@Override
		public void configureOptions(Options options) {
			options.addOption(Option.builder()
					.longOpt("kappa")
					.hasArg()
					.build());
			options.addOption(Option.builder()
					.longOpt("bekta")
					.hasArg()
					.build());
		}

		@Override
		public Object configure(OptionProvider op) {
			return new ConfigSection1Data(op);
		}
		
	}
	
	static class ConfigSection2Data {
		private final OptionProvider op;
		
		public ConfigSection2Data(OptionProvider op) {
			this.op = op;
		}
		
		public boolean getFoo() throws ConfigException {
			return op.getBoolean("foo");
		}
		
		public File getBar() throws Exception {
			return op.getFile("bar");
		}
		
	}
	
	static class ConfigSection2 implements ConfigSection {

		@Override
		public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
			defaults.add("foo", "false");
		}

		@Override
		public void configureOptions(Options options) {
			options.addOption(Option.builder()
					.longOpt("foo")
					.hasArg()
					.build());
			options.addOption(Option.builder()
					.longOpt("bar")
					.hasArg()
					.build());
		}

		@Override
		public Object configure(OptionProvider op) throws ConfigException {
			return new ConfigSection2Data(op);
		}
		
	}

	private AppConfigService2 service;
	
	@Before
	public void setUp() throws Exception {
		service = new AppConfigService2();
		service.addSection("section1", new ConfigSection1());
		service.addSection("section2", new ConfigSection2());
	}

	@Test
	public void testLoadConfig() throws Exception {
		String[] args = {
				"--kappa=zuzumba",
				"--config-file=fixture/app-test-config.ini",
				"--driver=hello"
		};
		AppConfig2 config = service.loadConfig(args);
		
		BasicConfig2 conf0 = config.getBasicConfig();
		assertEquals("hello", conf0.getDriver());
		
		ConfigSection1Data conf1 = config.getSection("section1");
		assertEquals("zuzumba", conf1.getKappa());
		assertEquals(Integer.valueOf(1), conf1.getBekta());

		ConfigSection2Data conf2 = config.getSection("section2");
		assertEquals(true, conf2.getFoo());
		assertEquals(new File("D:" + File.separator + "temp"), conf2.getBar());
	}

}
