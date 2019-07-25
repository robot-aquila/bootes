package ru.prolib.bootes.lib.config;

import java.io.File;
import java.time.LocalDateTime;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;

public class BasicConfig2Section implements ConfigSection {
	public static final String LOPT_HELP = "help";
	public static final String LOPT_HEADLESS = "headless";
	public static final String LOPT_NO_ORDERS = "no-orders";
	public static final String LOPT_DATA_DIR = "data-dir";
	public static final String LOPT_CONFIG_FILE = "config-file";
	public static final String LOPT_REPORT_DIR = "report-dir";

	private File defaultReportDir() {
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
	
	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		defaults.add(LOPT_HELP, "0");
		defaults.add(LOPT_HEADLESS, "0");
		defaults.add(LOPT_NO_ORDERS, "0");
		defaults.add(LOPT_REPORT_DIR, defaultReportDir().getAbsolutePath());
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
			.longOpt(LOPT_HELP)
			.desc("Show help.")
			.build());
		options.addOption(Option.builder()
			.longOpt(LOPT_HEADLESS)
			.desc("Enable headless mode.")
			.build());
		options.addOption(Option.builder()
			.longOpt(LOPT_NO_ORDERS)
			.desc("Avoid using orders where possible. Use simulated results instead.")
			.build());
		options.addOption(Option.builder()
			.longOpt(LOPT_DATA_DIR)
			.hasArg()
			.desc("Data directory by default. This may be used as an alternative for some other options.")
			.build());
		options.addOption(Option.builder()
			.longOpt(LOPT_CONFIG_FILE)
			.hasArg()
			.desc("Path to configuration file. All settings from this file will be loaded prior to applying "
				+ "other command line options. Options passed via command line have higher priority and will "
				+ "override options of configuration file.")
			.build());
		options.addOption(Option.builder()
			.longOpt(LOPT_REPORT_DIR)
			.hasArg()
			.desc("Directory to store reports. If omitted then default path "
				+ "will be used. Following template is used to build default path: "
				+ "reports/YYYTMMDDhhmmSS")
			.build());
	}

	@Override
	public Object configure(OptionProvider op) {
		return new BasicConfig2(op);
	}

}
