package ru.prolib.bootes.tsgr001a.rm;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class RMContractStrategyPositionParams {
	private final int numContracts;
	private final CDecimal takeProfit, stopLoss, tradeGoalCap, tradeLossCap;
	
	public RMContractStrategyPositionParams(int numContracts,
			CDecimal takeProfit,
			CDecimal stopLoss,
			CDecimal tradeGoalCap,
			CDecimal tradeLossCap)
	{
		this.numContracts = numContracts;
		this.takeProfit = takeProfit;
		this.stopLoss = stopLoss;
		this.tradeGoalCap = tradeGoalCap;
		this.tradeLossCap = tradeLossCap;
	}
	
	/**
	 * Get number of contracts per trade.
	 * <p>
	 * @return number of contracts
	 */
	public int getNumberOfContracts() {
		return numContracts;
	}
	
	/**
	 * Get take profit in points relative to position open price.
	 * <p>
	 * @return price points
	 */
	public CDecimal getTakeProfitPts() {
		return takeProfit;
	}
	
	/**
	 * Get stop loss in points relative to position open price.
	 * <p>
	 * @return price points
	 */
	public CDecimal getStopLossPts() {
		return stopLoss;
	}
	
	public CDecimal getTradeGoalCap() {
		return tradeGoalCap;
	}
	
	public CDecimal getTradeLossCap() {
		return tradeLossCap;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(900127, 91)
				.append(numContracts)
				.append(takeProfit)
				.append(stopLoss)
				.append(tradeGoalCap)
				.append(tradeLossCap)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != RMContractStrategyPositionParams.class ) {
			return false;
		}
		RMContractStrategyPositionParams o = (RMContractStrategyPositionParams) other;
		return new EqualsBuilder()
				.append(o.numContracts, numContracts)
				.append(o.takeProfit, takeProfit)
				.append(o.stopLoss, stopLoss)
				.append(o.tradeGoalCap, tradeGoalCap)
				.append(o.tradeLossCap, tradeLossCap)
				.build();
	}

}
