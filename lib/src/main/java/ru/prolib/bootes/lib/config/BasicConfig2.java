package ru.prolib.bootes.lib.config;

import static ru.prolib.bootes.lib.config.BasicConfig2Section.*;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class BasicConfig2 {
	private final OptionProvider options;
	
	public BasicConfig2(OptionProvider options) {
		this.options = options;
	}
	
	public boolean isShowHelp() throws ConfigException {
		return options.getBoolean(LOPT_HELP);
	}
	
	public boolean isHeadless() throws ConfigException {
		return options.getBoolean(LOPT_HEADLESS);
	}
	
	public boolean isNoOrders() throws ConfigException {
		return options.getBoolean(LOPT_NO_ORDERS);
	}
	
	public File getDataDirectory() throws ConfigException {
		return options.getFile(LOPT_DATA_DIR);
	}
	
	public File getConfigFile() {
		return options.getFile(LOPT_CONFIG_FILE);
	}
	
	public File getReportDirectory() {
		return options.getFile(LOPT_REPORT_DIR);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != BasicConfig2.class ) {
			return false;
		}
		BasicConfig2 o = (BasicConfig2) other;
		return new EqualsBuilder()
				.append(o.options, options)
				.build();
	}

}
