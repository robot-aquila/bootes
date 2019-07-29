package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;

public interface ConfigSection {
	void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException;
	void configureOptions(Options options);
	Object configure(OptionProvider op) throws ConfigException;
}
