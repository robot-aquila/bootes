package ru.prolib.bootes.lib.config;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;

public class SchedulerConfig2 {
	private final OptionProvider options;

	public SchedulerConfig2(OptionProvider options) {
		this.options = options;
	}
	
	public boolean isAutoStart() throws ConfigException {
		return options.getBoolean(SchedulerConfig2Section.LOPT_AUTO_START);
	}
	
	public boolean isAutoShutdown() throws ConfigException {
		return options.getBoolean(SchedulerConfig2Section.LOPT_AUTO_SHUTDOWN);
	}
	
	public Instant getInitialTime() throws ConfigException {
		return options.getInstant(SchedulerConfig2Section.LOPT_INITIAL_TIME);
	}
	
	public Instant getStopTime() throws ConfigException {
		return options.getInstant(SchedulerConfig2Section.LOPT_STOP_TIME);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if  ( other == null || other.getClass() != SchedulerConfig2.class ) {
			return false;
		}
		SchedulerConfig2 o = (SchedulerConfig2) other;
		return new EqualsBuilder()
				.append(o.options, options)
				.build();
	}

}
