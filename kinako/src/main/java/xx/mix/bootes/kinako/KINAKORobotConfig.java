package xx.mix.bootes.kinako;

import static xx.mix.bootes.kinako.KINAKORobotConfigSection.*;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class KINAKORobotConfig {
	private final OptionProvider options;
	
	public KINAKORobotConfig(OptionProvider options) {
		this.options = options;
	}
	
	public boolean isUseLimitOrders() {
		try {
			return options.getBoolean(LOPT_USE_LIMIT_ORDERS);
		} catch ( ConfigException e ) {
			return false;
		}
	}

}
