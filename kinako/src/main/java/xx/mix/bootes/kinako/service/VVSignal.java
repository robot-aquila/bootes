package xx.mix.bootes.kinako.service;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class VVSignal {
	private final VVSignalType type;
	private final CDecimal volume;
	private final String symbol;
	
	public VVSignal(VVSignalType type, CDecimal volume, String symbol) {
		this.type = type;
		this.volume = volume;
		this.symbol = symbol;
	}
	
	public VVSignalType getType() {
		return type;
	}
	
	public CDecimal getVolume() {
		return volume;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(getClass().getSimpleName())
				.append("[")
				.append(type)
				.append(" ")
				.append(volume)
				.append(" of ")
				.append(symbol)
				.append("]")
				.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(716721, 8990115)
				.append(type)
				.append(volume)
				.append(symbol)
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
				.append(o.type, type)
				.append(o.volume, volume)
				.append(o.symbol, symbol)
				.build();
	}

}
