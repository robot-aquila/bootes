package ru.prolib.bootes.protos.config;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class ProtosConfig {
	private final OptionProvider options;
	
	public ProtosConfig(OptionProvider options) {
		this.options = options;
	}
	
	public boolean isUseOhlcProvider() {
		try {
			return options.getBoolean(ProtosConfigSection.LOPT_USE_OHLC_PROVIDER);
		} catch ( ConfigException e ) {
			return false;
		}
	}

}
