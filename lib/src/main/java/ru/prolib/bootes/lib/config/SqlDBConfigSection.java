package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;

public class SqlDBConfigSection implements ConfigSection {
	public static final String LOPT_URL = "sqldb-url";
	public static final String LOPT_USER = "sqldb-user";
	public static final String LOPT_PASS = "sqldb-pass";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt(LOPT_URL)
				.desc("a database url of the form jdbc:subprotocol:subname")
				.hasArg()
				.argName("url")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_USER)
				.desc("the database user on whose behalf the connection is being made")
				.hasArg()
				.argName("user")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_PASS)
				.desc("the user's password")
				.hasArg()
				.argName("password")
				.build());
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new SqlDBConfig(op);
	}
	

}
