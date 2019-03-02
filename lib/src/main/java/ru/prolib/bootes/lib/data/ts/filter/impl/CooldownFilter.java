package ru.prolib.bootes.lib.data.ts.filter.impl;

import java.time.Duration;
import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;

public class CooldownFilter extends AbstractFilter<S3TradeSignal> {
	private static final String DEFAULT_ID = "COOLDOWN";
	private final TStamped getter;
	private final Duration duration;
	
	public CooldownFilter(String id, TStamped getter, Duration duration) {
		super(id);
		this.getter = getter;
		this.duration = duration;
	}
	
	public CooldownFilter(TStamped getter, Duration duration) {
		this(DEFAULT_ID, getter, duration);
	}
	
	public TStamped getTimeGetter() {
		return getter;
	}
	
	public Duration getDuration() {
		return duration;
	}

	@Override
	public boolean approve(S3TradeSignal signal) {
		Instant gt = getter.getTime();
		if ( gt == null ) {
			return true;
		}
		Duration actual = Duration.between(gt, signal.getTime());
		return actual.compareTo(duration) > 0;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CooldownFilter.class ) {
			return false;
		}
		CooldownFilter o = (CooldownFilter) other;
		return new EqualsBuilder()
				.append(o.id, id)
				.append(o.getter, getter)
				.append(o.duration, duration)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
