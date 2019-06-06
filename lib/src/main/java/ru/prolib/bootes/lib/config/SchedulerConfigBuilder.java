package ru.prolib.bootes.lib.config;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.config.ConfigException;

public class SchedulerConfigBuilder {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SchedulerConfigBuilder.class);
	}
	
	private boolean probeAutoStart, probeAutoShutdown;
	private Instant probeInitialTime, probeStopTime;
	
	public SchedulerConfig build(BasicConfig basicConfig) throws ConfigException {
		if ( probeAutoShutdown && probeStopTime == null ) {
			throw new ConfigException("PROBE stop time is required for PROBE auto shutdown");
		}
		if ( basicConfig.isHeadless() && ! probeAutoShutdown ) {
			throw new ConfigException("PROBE auto shutdown must be enabled in headless mode");
		}
		if ( probeStopTime != null ) {
			if ( probeInitialTime != null && ! probeStopTime.isAfter(probeInitialTime) ) {
				throw new ConfigException("PROBE stop time must be greater than initial time");
			} else if ( probeInitialTime == null && ! probeStopTime.isAfter(Instant.EPOCH) ) {
				throw new ConfigException("PROBE stop time must be greater than epoch start time");
			}
			
		}
		return new SchedulerConfig(
				probeAutoStart,
				probeAutoShutdown,
				probeInitialTime,
				probeStopTime
			);
	}
	
	public SchedulerConfigBuilder withProbeAutoStart(boolean autoStart) {
		logger.debug("withProbeAutoStart={}", autoStart);
		this.probeAutoStart = autoStart;
		return this;
	}
	
	public SchedulerConfigBuilder withProbeAutoShutdown(boolean autoShutdown) {
		logger.debug("withProbeAutoShutdown={}", autoShutdown);
		this.probeAutoShutdown = autoShutdown;
		return this;
	}
	
	public SchedulerConfigBuilder withProbeInitialTime(Instant initialTime) {
		logger.debug("withProbeInitialTime={}", initialTime);
		this.probeInitialTime = initialTime;
		return this;
	}
	
	public SchedulerConfigBuilder withProbeStopTime(Instant stopTime) {
		logger.debug("withProbeStopTime={}", stopTime);
		this.probeStopTime = stopTime;
		return this;
	}

}
