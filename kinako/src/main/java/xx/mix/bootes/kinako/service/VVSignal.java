package xx.mix.bootes.kinako.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VVSignal {
	private final Instant time;
	private final List<VVOrderRecom> recommendations;
	
	public VVSignal(Instant time, List<VVOrderRecom> recommendations) {
		this.time = time;
		this.recommendations = recommendations;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public List<VVOrderRecom> getRecommendations() {
		return Collections.unmodifiableList(recommendations);
	}
	
	public VVOrderRecom getRecommendation(String symbol) {
		for ( VVOrderRecom signal : recommendations ) {
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
				.append(recommendations)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != VVSignal.class ) {
			return false;
		}
		VVSignal o = (VVSignal) other;
		return new EqualsBuilder()
				.append(o.time, time)
				.append(o.recommendations, recommendations)
				.build();
	}

}
