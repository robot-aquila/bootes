package ru.prolib.bootes.lib.config;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class SqlDBConfig {
	private final OptionProvider options;
	
	public SqlDBConfig(OptionProvider options) {
		this.options = options;
	}
	
	public String getURL() throws ConfigException {
		return options.getStringNotNull(SqlDBConfigSection.LOPT_URL, null);
	}
	
	public String getUser() throws ConfigException {
		return options.getStringNotNull(SqlDBConfigSection.LOPT_USER, "");
	}
	
	public String getPass() throws ConfigException {
		return options.getStringNotNull(SqlDBConfigSection.LOPT_PASS, "");
	}
	
}
