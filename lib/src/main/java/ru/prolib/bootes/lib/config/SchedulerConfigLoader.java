package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class SchedulerConfigLoader {
	
	public void load(SchedulerConfigBuilder builder, OptionProvider optionProvider, BasicConfig basicConfig)
			throws ConfigException
	{
		builder.withProbeAutoStart(optionProvider.getBoolean("probe-auto-start", false))
			.withProbeAutoShutdown(optionProvider.getBoolean("probe-auto-shutdown", false))
			.withProbeInitialTime(optionProvider.getInstant("probe-initial-time"))
			.withProbeStopTime(optionProvider.getInstant("probe-stop-time"));
	}
	
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt("probe-auto-start")
				.desc("If specified then PROBE scheduler will be started immediately after program initialization.")
				.build());
		options.addOption(Option.builder()
				.longOpt("probe-auto-shutdown")
				.desc("Shutdown program automatically when PROBE scheduler reach the stop time. "
					+ "This option requires --probe-stop-time option specified.")
				.build());
		options.addOption(Option.builder()
				.longOpt("probe-initial-time")
				.hasArg()
				.argName("time")
				.desc("Specify start time of PROBE scheduler. If omitted then default PROBE time will be used.")
				.build());
		options.addOption(Option.builder()
				.longOpt("probe-stop-time")
				.hasArg()
				.desc("Specify time to stop simulation by stopping PROBE scheduler.")
				.build());
	}

}
