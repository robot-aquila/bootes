package ru.prolib.bootes.protos.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.bootes.lib.config.ConfigSection;

public class ProtosConfigSection implements ConfigSection {
	public static final String LOPT_USE_OHLC_PROVIDER = "protos-use-ohlc-provider";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		defaults.add(LOPT_USE_OHLC_PROVIDER, "0");
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt(LOPT_USE_OHLC_PROVIDER)
				.desc(new StringBuilder()
					.append("Use OHLC provider as OHLC producer for data handler. ")
					.append("Will listen each trade to build OHLC data by default.")
					.toString())
				.build());
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new ProtosConfig(op);
	}

}
