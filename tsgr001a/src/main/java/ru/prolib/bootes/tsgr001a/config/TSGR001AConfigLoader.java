package ru.prolib.bootes.tsgr001a.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.bootes.lib.config.BasicConfig;
import ru.prolib.bootes.lib.config.OptionProvider;

public class TSGR001AConfigLoader {
	private static final String OPT_INST_CONF = "tsgr001a-inst-config";
	
	public void load(TSGR001AConfigBuilder builder,
					 OptionProvider option_provider,
					 BasicConfig basic_config)
	{
		builder.withInstancesConfig(option_provider.getFile(OPT_INST_CONF));
	}
	
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
			.longOpt(OPT_INST_CONF)
			.hasArg()
			//.required()
			.desc("Path to TSGR001A instances configuration file")
			.build());
	}

}
