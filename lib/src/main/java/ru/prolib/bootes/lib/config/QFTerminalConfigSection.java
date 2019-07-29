package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;

public class QFTerminalConfigSection implements ConfigSection {
	public static final String LOPT_TEST_ACCOUNT = "qforts-test-account";
	public static final String LOPT_TEST_BALANCE = "qforts-test-balance";
	public static final String LOPT_DATA_DIR = "qforts-data-dir";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		defaults.add(LOPT_TEST_ACCOUNT, "QFORTS-TEST");
		defaults.add(LOPT_TEST_BALANCE, "1000000.00");
		defaults.add(LOPT_DATA_DIR, op.getString(BasicConfig2Section.LOPT_DATA_DIR));
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt(LOPT_DATA_DIR)
				.hasArg()
				.argName("path")
				.desc("Root directory of combined storage of L1 and symbol data.")
				.build());
		
		options.addOption(Option.builder()
				.longOpt(LOPT_TEST_ACCOUNT)
				.hasArg()
				.argName("code")
				.desc("Code of test account.")
				.build());
		
		options.addOption(Option.builder()
				.longOpt(LOPT_TEST_BALANCE)
				.hasArg()
				.argName("amount")
				.desc("Initial balance of test account in RUB.")
				.build());
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new QFTerminalConfig(op);
	}

}