package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class OHLCHistoryConfigLoader {
	private static final String DEFAULT_CACHE_DIRNAME = "aquila-ohlcv-cache";
	
	public void load(OHLCHistoryConfigBuilder builder, OptionProvider optionProvider, BasicConfig basicConfig)
			throws ConfigException
	{
		builder.withDataDirectory(optionProvider.getFile("ohlc-data-dir", basicConfig.getDataDirectory()))
			.withCacheDirectory(optionProvider.getFile("ohlc-cache-dir", getDefaultCacheDirectory()));
	}
	
	private File getDefaultCacheDirectory() {
		return new File(System.getProperty("java.io.tmpdir") + File.separator + DEFAULT_CACHE_DIRNAME);
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
