package ru.prolib.bootes.lib.config;

import java.io.File;

import ru.prolib.aquila.core.config.ConfigException;

@Deprecated
public class OHLCHistoryConfigBuilder {
	private File dataDir, cacheDir;
	
	public OHLCHistoryConfig build(BasicConfig basicConfig) throws ConfigException {
		if ( dataDir == null ) {
			throw new ConfigException("OHLC history data directory must be specified");
		}
		if ( cacheDir == null ) {
			throw new ConfigException("OHLC history cache directory must be specified");
		}
		return new OHLCHistoryConfig(dataDir, cacheDir);
	}
	
	public OHLCHistoryConfigBuilder withDataDirectory(File dir) {
		this.dataDir = dir;
		return this;
	}
	
	public OHLCHistoryConfigBuilder withCacheDirectory(File dir) {
		this.cacheDir = dir;
		return this;
	}

}
