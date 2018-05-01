package ru.prolib.bootes.lib.config;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SchedulerConfig {
	private final boolean probeAutoStart, probeAutoShutdown;
	private final Instant probeInitialTime, probeStopTime;
	
	public SchedulerConfig(boolean probeAutoStart,
						   boolean probeAutoShutdown,
						   Instant probeInitialTime,
						   Instant probeStopTime)
	{
		this.probeAutoStart = probeAutoStart;
		this.probeAutoShutdown = probeAutoShutdown;
		this.probeInitialTime = probeInitialTime;
		this.probeStopTime = probeStopTime;
	}
	
	public boolean isProbeAutoStart() {
		return probeAutoStart;
	}
	
	public boolean isProbeAutoShutdown() {
		return probeAutoShutdown;
	}
	
	public Instant getProbeInitialTime() {
		return probeInitialTime;
	}
	
	public Instant getProbeStopTime() {
		return probeStopTime;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("probeAutoStart", probeAutoStart)
			.append("probeAutoShutdown", probeAutoShutdown)
			.append("probeInitialTime", probeInitialTime)
			.append("probeStopTime", probeStopTime)
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(3491905, 85)
			.append(probeAutoStart)
			.append(probeAutoShutdown)
			.append(probeInitialTime)
			.append(probeStopTime)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SchedulerConfig.class ) {
			return false;
		}
		SchedulerConfig o = (SchedulerConfig) other;
		return new EqualsBuilder()
			.append(o.probeAutoStart, probeAutoStart)
			.append(o.probeAutoShutdown, probeAutoShutdown)
			.append(o.probeInitialTime, probeInitialTime)
			.append(o.probeStopTime, probeStopTime)
			.isEquals();
	}

}
