package ru.prolib.bootes.tsgr001a.robot;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class ContractParams {
	private final Symbol symbol;
	private final Interval dataTrackingPeriod;
	
	public ContractParams(Symbol symbol, Interval dataTrackingPeriod) {
		this.symbol = symbol;
		this.dataTrackingPeriod = dataTrackingPeriod;
	}
	
	/**
	 * Get contract symbol.
	 * <p>
	 * @return symbol of contract to trade
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * Get data tracking period.
	 * <p>
	 * @return period in which contract data should be initialized and properly
	 * tracked.
	 */
	public Interval getDataTrackingPeriod() {
		return dataTrackingPeriod;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1097221, 505)
				.append(symbol)
				.append(dataTrackingPeriod)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ContractParams.class ) {
			return false;
		}
		ContractParams o = (ContractParams) other;
		return new EqualsBuilder()
				.append(o.symbol, symbol)
				.append(o.dataTrackingPeriod, dataTrackingPeriod)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
