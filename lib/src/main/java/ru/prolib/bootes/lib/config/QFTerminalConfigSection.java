package ru.prolib.bootes.lib.config;

import static ru.prolib.aquila.qforts.impl.QFOrderExecutionTriggerMode.*;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;

public class QFTerminalConfigSection implements ConfigSection {
	public static final String LOPT_TEST_ACCOUNT = "qforts-test-account";
	public static final String LOPT_TEST_BALANCE = "qforts-test-balance";
	public static final String LOPT_DATA_DIR = "qforts-data-dir";
	public static final String LOPT_LIQUIDITY_MODE = "qforts-liquidity-mode";
	public static final String LOPT_LEGACY_SDS = "qforts-legacy-sds";
	public static final String LOPT_ORDER_EXEC_TRIGGER_MODE = "qforts-order-exec-trigger-mode";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		defaults.add(LOPT_TEST_ACCOUNT, "QFORTS-TEST");
		defaults.add(LOPT_TEST_BALANCE, "1000000.00");
		defaults.add(LOPT_DATA_DIR, op.getString(BasicConfig2Section.LOPT_DATA_DIR));
		defaults.add(LOPT_LIQUIDITY_MODE, "0");
		defaults.add(LOPT_LEGACY_SDS, "false");
		defaults.add(LOPT_ORDER_EXEC_TRIGGER_MODE, "0");
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
		
		options.addOption(Option.builder()
				.longOpt(LOPT_LIQUIDITY_MODE)
				.hasArg()
				.argName("mode")
				.desc("Order execution liquidity mode. Available modes are: 0 - LIMITED,"
					+ " 1 - APPLY_TO_ORDER, 2 - UNLIMITED. Default is LIMITED.")
				.build());
		
		options.addOption(Option.builder()
				.longOpt(LOPT_LEGACY_SDS)
				.desc("Enable legacy symbol data service.")
				.build());
		
		options.addOption(Option.builder()
				.longOpt(LOPT_ORDER_EXEC_TRIGGER_MODE)
				.hasArg()
				.argName("mode")
				.desc(new StringBuilder()
						.append("Order execution trigger mode. Available modes are: ")
						.append("0 -  ").append(USE_LAST_TRADE_EVENT_OF_SECURITY).append(" (default), ")
						.append("1 - ").append(USE_L1UPDATES_WHEN_ORDER_APPEARS)
						.toString())
				.build());
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		return new QFTerminalConfig(op);
	}

}
