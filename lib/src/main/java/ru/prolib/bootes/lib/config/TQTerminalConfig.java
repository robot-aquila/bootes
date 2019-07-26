package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class TQTerminalConfig {
	private final OptionProvider options;
	
	public TQTerminalConfig(OptionProvider options) {
		this.options = options;
	}
	
	public File getLogPath() throws ConfigException {
		return options.getFileNotNull(TQTerminalConfigSection.LOPT_LOG_PATH);
	}
	
	public int getLogLevel() throws ConfigException {
		return options.getIntegerPositiveNotNull(TQTerminalConfigSection.LOPT_LOG_LEVEL);
	}
	
	public String getLogin() throws ConfigException {
		return options.getStringNotNull(TQTerminalConfigSection.LOPT_LOGIN, null);
	}
	
	public String getPassword() throws ConfigException {
		return options.getStringNotNull(TQTerminalConfigSection.LOPT_PASSWORD, null);
	}
	
	public String getHost() throws ConfigException {
		return options.getStringNotNull(TQTerminalConfigSection.LOPT_HOST, null);
	}
	
	public int getPort() throws ConfigException {
		return options.getIntegerPositiveNonZeroNotNull(TQTerminalConfigSection.LOPT_PORT);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQTerminalConfig.class ) {
			return false;
		}
		TQTerminalConfig o = (TQTerminalConfig) other;
		return new EqualsBuilder()
				.append(o.options, options)
				.build();
	}

}
