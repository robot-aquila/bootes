package ru.prolib.bootes.lib.config;

import java.io.File;
import java.time.LocalDateTime;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class BasicConfigLoader {
	
	private File defaultReportsDir() {
		LocalDateTime ldt = LocalDateTime.now();
		return new File("reports", String.format("%4d%02d%02d%02d%02d%02d",
				ldt.getYear(),
				ldt.getMonthValue(),
				ldt.getDayOfMonth(),
				ldt.getHour(),
				ldt.getMinute(),
				ldt.getSecond()
			));
	}
	
	public void load(BasicConfigBuilder builder, OptionProvider optionProvider) throws ConfigException {
		builder.withShowHelp(optionProvider.getBoolean("help", false))
			.withHeadless(optionProvider.getBoolean("headless", false))
			.withNoOrders(optionProvider.getBoolean("no-orders", false))
			.withDataDirectory(optionProvider.getFile("data-dir"))
			.withConfigFile(optionProvider.getFile("config-file"))
			.withReportsDirectory(optionProvider.getFile("reports-dir", defaultReportsDir()));
	}
	
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
			.longOpt("help")
			.desc("Show help.")
			.build());
		options.addOption(Option.builder()
			.longOpt("headless")
			.desc("Enable headless mode.")
			.build());
		options.addOption(Option.builder()
			.longOpt("no-orders")
			.desc("Avoid using orders where possible. Use simulated results instead.")
			.build());
		options.addOption(Option.builder()
			.longOpt("data-dir")
			.hasArg()
			.desc("Data directory by default. This may be used as an alternative for some other options.")
			.build());
		options.addOption(Option.builder()
			.longOpt("config-file")
			.hasArg()
			.desc("Path to configuration file. All settings from this file will be loaded prior to applying "
				+ "other command line options. Options passed via command line have higher priority and will "
				+ "override options of configuration file.")
			.build());
		options.addOption(Option.builder()
			.longOpt("reports-dir")
			.hasArg()
			.desc("Directory to store reports. If omitted then default path "
				+ "will be used. Following template is used to build default path: "
				+ "reports/YYYTMMDDhhmmSS")
			.build());
	}

}
