package xx.mix.bootes.kinako.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VVSignalSet {
	private final Instant time;
	private final List<VVSignal> signals;
	
	public VVSignalSet(Instant time, List<VVSignal> signals) {
		this.time = time;
		this.signals = signals;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public List<VVSignal> getSignals() {
		return Collections.unmodifiableList(signals);
	}
	
	public VVSignal getSignal(String symbol) {
		for ( VVSignal signal : signals ) {
			if ( symbol.equals(signal.getSymbol()) ) {
				return signal;
			}
		}
		throw new IllegalArgumentException("Symbol not found: " + symbol);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(917265413, 99127)
				.append(time)
				.append(signals)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != VVSignalSet.class ) {
			return false;
		}
		VVSignalSet o = (VVSignalSet) other;
		return new EqualsBuilder()
				.append(o.time, time)
				.append(o.signals, signals)
				.build();
	}

}
