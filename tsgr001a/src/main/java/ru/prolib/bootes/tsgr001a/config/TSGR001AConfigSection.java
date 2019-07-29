package ru.prolib.bootes.tsgr001a.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.bootes.lib.config.ConfigSection;

public class TSGR001AConfigSection implements ConfigSection {
	private static final String OPT_INST_CONF = "tsgr001a-inst-config";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt(OPT_INST_CONF)
				.hasArg()
				.desc("Path to TSGR001A instances configuration file")
				.build());
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new TSGR001AConfigBuilder()
			.withInstancesConfig(op.getFileNotNull(OPT_INST_CONF))
			.build();
	}

}
