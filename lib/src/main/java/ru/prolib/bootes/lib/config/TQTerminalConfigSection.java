package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;

public class TQTerminalConfigSection implements ConfigSection {
	public static final String LOPT_LOG_PATH = "transaq-log-path";
	public static final String LOPT_LOG_LEVEL = "transaq-log-level";
	public static final String LOPT_LOGIN = "transaq-login";
	public static final String LOPT_PASSWORD = "transaq-password";
	public static final String LOPT_HOST = "transaq-host";
	public static final String LOPT_PORT = "transaq-port";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		defaults.add(LOPT_LOG_LEVEL, "0");
		defaults.add(LOPT_PORT, "3900");
	}

	@Override
	public void configureOptions(Options options) throws ConfigException {
		options.addOption(Option.builder()
				.longOpt(LOPT_LOG_PATH)
				.hasArg()
				.argName("path")
				.desc("Path to transaq log files.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_LOG_LEVEL)
				.hasArg()
				.argName("level")
				.desc("Transaq log level. Possible values: 0, 1 or 2.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_LOGIN)
				.hasArg()
				.argName("login")
				.desc("Transaq login.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_PASSWORD)
				.hasArg()
				.argName("pwd")
				.desc("Transaq password.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_HOST)
				.hasArg()
				.argName("hostname")
				.desc("Transaq host.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_PORT)
				.hasArg()
				.argName("port")
				.desc("Transaq port.")
				.build());
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new TQTerminalConfig(op);
	}

}
