package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class OHLCHistoryConfigLoader {
	
	public void load(OHLCHistoryConfigBuilder builder, OptionProvider optionProvider, BasicConfig basicConfig)
			throws ConfigException
	{
		builder.withDataDirectory(optionProvider.getFileNotNull("ohlc-data-dir", basicConfig.getDataDirectory()))
			.withCacheDirectory(optionProvider.getFileNotNull("ohlc-cache-dir"));
	}
	
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt("ohlc-data-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of L1 data in FINAM export format.")
				.build());
		options.addOption(Option.builder()
				.longOpt("ohlc-cache-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of cached OHLC data.")
				.build());
	}

}
