package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class TerminalConfigLoader {

	public void load(TerminalConfigBuilder builder, OptionProvider optionProvider, BasicConfig basicConfig)
		throws ConfigException
	{
		builder.withQFortsL1Directory(optionProvider.getFileNotNull("qforts-l1-dir", basicConfig.getDataDirectory()))
			.withQFortsSymbolDirectory(optionProvider.getFileNotNull("qforts-symbol-dir", basicConfig.getDataDirectory()))
			.withQFortsTestAccount(new Account(optionProvider.getStringNotNull("qforts-test-account", "QFORTS-TEST")))
			.withQFortsTestBalance(CDecimalBD.ofRUB2(optionProvider.getStringNotNull("qforts-test-balance", "1000000")));
	}
	
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt("qforts-l1-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of L1 data in FINAM export format.")
				.build());
		options.addOption(Option.builder()
				.longOpt("qforts-symbol-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of symbol data in MOEX export format.")
				.build());
		options.addOption(Option.builder()
				.longOpt("qforts-test-account")
				.hasArg()
				.argName("code")
				.desc("Code of test account.")
				.build());
		options.addOption(Option.builder()
				.longOpt("qforts-test-balance")
				.hasArg()
				.argName("amount")
				.desc("Initial balance of test account in RUB.")
				.build());
	}
	
}
