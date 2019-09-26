package xx.mix.bootes.kinako;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.bootes.lib.config.ConfigSection;

public class KINAKORobotConfigSection implements ConfigSection {
	public static final String LOPT_USE_LIMIT_ORDERS = "kinako-use-limit-orders";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		defaults.add(LOPT_USE_LIMIT_ORDERS, "0");
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt(LOPT_USE_LIMIT_ORDERS)
				.desc(new StringBuilder()
					.append("Use limit orders instead of market. Use upper and lower ")
					.append("price limits to determine price of an order.")
					.toString())
				.build());
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new KINAKORobotConfig(op);
	}

}
