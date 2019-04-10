package ru.prolib.bootes.lib.rm;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class RMContractStrategyPositionParams {
	private final int numContracts;
	private final CDecimal takeProfit, stopLoss, slippage,
		tradeGoalCap, tradeLossCap,
		avgDailyPriceMove, avgLocalPriceMove,
		baseCap;
	
	public RMContractStrategyPositionParams(int numContracts,
			CDecimal takeProfit,
			CDecimal stopLoss,
			CDecimal slippage,
			CDecimal tradeGoalCap,
			CDecimal tradeLossCap,
			CDecimal avgDailyPriceMove,
			CDecimal avgLocalPriceMove,
			CDecimal baseCap)
	{
		this.numContracts = numContracts;
		this.takeProfit = takeProfit;
		this.stopLoss = stopLoss;
		this.slippage = slippage;
		this.tradeGoalCap = tradeGoalCap;
		this.tradeLossCap = tradeLossCap;
		this.avgDailyPriceMove = avgDailyPriceMove;
		this.avgLocalPriceMove = avgLocalPriceMove;
		this.baseCap = baseCap;
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
	
	/**
	 * Get slippage in points.
	 * <p>
	 * @return price points
	 */
	public CDecimal getSlippagePts() {
		return slippage;
	}
	
	public CDecimal getTradeGoalCap() {
		return tradeGoalCap;
	}
	
	public CDecimal getTradeLossCap() {
		return tradeLossCap;
	}
	
	public CDecimal getAvgDailyPriceMove() {
		return avgDailyPriceMove;
	}
	
	public CDecimal getAvgLocalPriceMove() {
		return avgLocalPriceMove;
	}
	
	/**
	 * /**
	 * Get base capital.
	 * <p>
	 * Base capital is a value at the moment of signal detection used to
	 * determine all derived values. It's not an account value. It's a
	 * share in absolute value dedicated for the strategy at the moment
	 * of signal detection
	 * <p>
	 * @return base capital in money
	 */
	public CDecimal getBaseCap() {
		return baseCap;
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
				.append(slippage)
				.append(tradeGoalCap)
				.append(tradeLossCap)
				.append(avgDailyPriceMove)
				.append(avgLocalPriceMove)
				.append(baseCap)
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
				.append(o.slippage, slippage)
				.append(o.tradeGoalCap, tradeGoalCap)
				.append(o.tradeLossCap, tradeLossCap)
				.append(o.avgDailyPriceMove, avgDailyPriceMove)
				.append(o.avgLocalPriceMove, avgLocalPriceMove)
				.append(o.baseCap, baseCap)
				.build();
	}

}
