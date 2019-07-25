package ru.prolib.bootes.lib.config;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class TerminalConfigLoader {

	public void load(TerminalConfigBuilder builder, OptionProvider op, BasicConfig basicConfig)
		throws ConfigException
	{
		builder.withQFortsDataDirectory(op.getFile("qforts-data-dir", basicConfig.getDataDirectory()))
			.withQFortsTestAccount(new Account(op.getStringNotNull("qforts-test-account", "QFORTS-TEST")))
			.withQFortsTestBalance(ofRUB2(op.getStringNotNull("qforts-test-balance", "1000000")))
			.withDriverID(op.getStringNotNull("driver", "default"))
			.withTransaqLogPath(op.getFile("transaq-log-path"))
			.withTransaqLogLevel(op.getIntegerPositiveNotNull("transaq-log-level", 0))
			.withTransaqLogin(op.getString("transaq-login"))
			.withTransaqPassword(op.getString("transaq-password"))
			.withTransaqHost(op.getString("transaq-host"))
			.withTransaqPort(op.getIntegerPositiveNonZeroNotNull("transaq-port", 3900));
	}
	
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt("qforts-data-dir")
				.hasArg()
				.argName("path")
				.desc("Root directory of combined storage of L1 and symbol data.")
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
		
		options.addOption(Option.builder()
				.longOpt("driver")
				.hasArg()
				.argName("driverID")
				.desc("Terminal driver ID.")
				.build());
		options.addOption(Option.builder()
				.longOpt("transaq-log-path")
				.hasArg()
				.argName("path")
				.desc("Path to transaq log files.")
				.build());
		options.addOption(Option.builder()
				.longOpt("transaq-log-level")
				.hasArg()
				.argName("level")
				.desc("Transaq log level. Possible values: 0, 1 or 2.")
				.build());
		options.addOption(Option.builder()
				.longOpt("transaq-login")
				.hasArg()
				.argName("login")
				.desc("Transaq login.")
				.build());
		options.addOption(Option.builder()
				.longOpt("transaq-password")
				.hasArg()
				.argName("pwd")
				.desc("Transaq password.")
				.build());
		options.addOption(Option.builder()
				.longOpt("transaq-host")
				.hasArg()
				.argName("hostname")
				.desc("Transaq host.")
				.build());
		options.addOption(Option.builder()
				.longOpt("transaq-port")
				.hasArg()
				.argName("port")
				.desc("Transaq port.")
				.build());
	}
	
}
