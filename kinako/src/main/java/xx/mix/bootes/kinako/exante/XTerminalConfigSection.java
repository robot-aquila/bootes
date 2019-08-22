package xx.mix.bootes.kinako.exante;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.bootes.lib.config.ConfigSection;

public class XTerminalConfigSection implements ConfigSection {
	public static final String LOPT_CONFIG = "exante-config";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt(LOPT_CONFIG)
				.hasArg()
				.argName("filename")
				.desc("Path to Exante config file.")
				.build());		
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new XTerminalConfig(op);
	}

}
