package ru.prolib.bootes.tsgr001a.robot;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class ContractParams {
	private final Symbol symbol;
	private final Interval tradingPeriod;
	
	public ContractParams(Symbol symbol, Interval tradingPeriod) {
		this.symbol = symbol;
		this.tradingPeriod = tradingPeriod;
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
	 * Get closest trading period.
	 * <p>
	 * @return period in which contract can be traded potentially.
	 * Outside that period contract considered as unavailable for trading.
	 */
	public Interval getTradingPeriod() {
		return tradingPeriod;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1097221, 505)
				.append(symbol)
				.append(tradingPeriod)
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
				.append(o.tradingPeriod, tradingPeriod)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
