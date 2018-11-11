package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class BasicConfigLoader {
	
	public void load(BasicConfigBuilder builder, OptionProvider optionProvider) throws ConfigException {
		builder.withShowHelp(optionProvider.getBoolean("help", false))
			.withHeadless(optionProvider.getBoolean("headless", false))
			.withDataDirectory(optionProvider.getFile("data-dir"))
			.withConfigFile(optionProvider.getFile("config-file"));
	}
	
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
			.longOpt("help")
			.desc("Show help.")
			.build());
		options.addOption(Option.builder()
			.longOpt("headless")
			.desc("Enable headless mode.")
			.build());
		options.addOption(Option.builder()
			.longOpt("data-dir")
			.hasArg()
			.desc("Data directory by default. This may be used as an alternative for some other options.")
			.build());
		options.addOption(Option.builder()
			.longOpt("config-file")
			.hasArg()
			.desc("Path to configuration file. All settings from this file will be loaded prior to applying "
				+ "other command line options. Options passed via command line have higher priority and will "
				+ "override options of configuration file.")
			.build());
	}

}
