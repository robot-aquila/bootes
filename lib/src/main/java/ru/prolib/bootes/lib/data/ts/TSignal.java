package ru.prolib.bootes.lib.data.ts;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Trading signal.
 */
public class TSignal {
	private final Instant time;
	private final int index;
	private final SignalType type;
	private final CDecimal price;
	
	public TSignal(Instant time, int index, SignalType type, CDecimal price) {
		this.time = time;
		this.index = index;
		this.type = type;
		this.price = price;
	}
	
	/**
	 * Get time of signal.
	 * <p>
	 * @return signal time
	 */
	public Instant getTime() {
		return time;
	}
	
	/**
	 * Get basis index of signal occurrence.
	 * <p>
	 * @return index of element where signal appeared. Most cases negative value means that index not defined.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Get signal type.
	 * <p>
	 * @return signal type
	 */
	public SignalType getType() {
		return type;
	}
	
	/**
	 * Get price of signal.
	 * <p>
	 * @return price or null if price not defined
	 */
	public CDecimal getPrice() {
		return price;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(getClass().getSimpleName())
				.append("[")
				.append(time)
				.append(" i=").append(index)
				.append(" ").append(type)
				.append("@").append(price)
				.append("]")
				.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(8865123, 19)
				.append(time)
				.append(index)
				.append(type)
				.append(price)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TSignal.class ) {
			return false;
		}
		TSignal o = (TSignal) other;
		return new EqualsBuilder()
				.append(o.time, time)
				.append(o.index, index)
				.append(o.type, type)
				.append(o.price, price)
				.build();
	}
	
}
