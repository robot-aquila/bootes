package ru.prolib.bootes.lib.config;

import java.time.Instant;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVWritableStore;
import ru.prolib.aquila.core.config.OptionProvider;

public class SchedulerConfig2Section implements ConfigSection {
	public static final String LOPT_AUTO_START = "probe-auto-start";
	public static final String LOPT_AUTO_SHUTDOWN = "probe-auto-shutdown";
	public static final String LOPT_INITIAL_TIME = "probe-initial-time";
	public static final String LOPT_STOP_TIME = "probe-stop-time";

	@Override
	public void configureDefaults(KVWritableStore defaults, OptionProvider op) throws ConfigException {
		
	}

	@Override
	public void configureOptions(Options options) {
		options.addOption(Option.builder()
				.longOpt(LOPT_AUTO_START)
				.desc("If specified then PROBE scheduler will be started immediately after program initialization.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_AUTO_SHUTDOWN)
				.desc("Shutdown program automatically when PROBE scheduler reach the stop time. "
					+ "This option requires --probe-stop-time option specified.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_INITIAL_TIME)
				.hasArg()
				.argName("time")
				.desc("Specify start time of PROBE scheduler. If omitted then default PROBE time will be used.")
				.build());
		options.addOption(Option.builder()
				.longOpt(LOPT_STOP_TIME)
				.hasArg()
				.desc("Specify time to stop simulation by stopping PROBE scheduler.")
				.build());
	}

	@Override
	public Object configure(OptionProvider op) throws ConfigException {
		SchedulerConfig2 config = new SchedulerConfig2(op);
		boolean auto_shutdown = config.isAutoShutdown();
		if ( auto_shutdown && config.getStopTime() == null ) {
			throw new ConfigException("PROBE stop time is required for PROBE auto shutdown");
		}
		if ( op.getBoolean(BasicConfig2Section.LOPT_HEADLESS) && ! auto_shutdown ) {
			throw new ConfigException("PROBE auto shutdown must be enabled in headless mode");
		}
		Instant stop_time = config.getStopTime(), init_time = config.getInitialTime();
		if ( stop_time != null ) {
			if ( init_time != null && ! stop_time.isAfter(init_time) ) {
				throw new ConfigException("PROBE stop time must be greater than initial time");
			} else if ( init_time == null && ! stop_time.isAfter(Instant.EPOCH) ) {
				throw new ConfigException("PROBE stop time must be greater than epoch start time");
			}
		}
		return config;
	}

}
