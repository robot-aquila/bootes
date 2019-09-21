package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;

public class OHLCHistoryConfig2Section implements ConfigSection {
	public static final String LOPT_DATA_DIR = "ohlc-data-dir";
	public static final String LOPT_CACHE_DIR = "ohlc-cache-dir";
	private static final String DEFAULT_CACHE_DIRNAME = "aquila-ohlcv-cache";

	private File defaultCacheDirectory() {
		return new File(System.getProperty("java.io.tmpdir"), DEFAULT_CACHE_DIRNAME);
	}
	
	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		File x = op.getFile(BasicConfig2Section.LOPT_DATA_DIR);
		if ( x != null ) {
			defaults.add(LOPT_DATA_DIR, x.getAbsolutePath());
		}
		defaults.add(LOPT_CACHE_DIR, defaultCacheDirectory().getAbsolutePath());
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt(LOPT_DATA_DIR)
				.hasArg()
				.argName("path")
				.desc("Root directory of L1 data in FINAM export format.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_CACHE_DIR)
				.hasArg()
				.argName("path")
				.desc("Root directory of cached OHLC data.")
				.build());		
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new OHLCHistoryConfig2(op);
	}

}
