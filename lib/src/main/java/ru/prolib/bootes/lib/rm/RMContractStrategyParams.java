package ru.prolib.bootes.lib.rm;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class RMContractStrategyParams {
	private final CDecimal tradeGoalCapPer, tradeLossCapPer, expDailyPriceMovePer,
		expLocalPriceMovePer, strategyCapSharePer;
	private final int slippageStp;
	
	public RMContractStrategyParams(CDecimal tradeGoalCapPer,
			CDecimal tradeLossCapPer,
			CDecimal expDailyPriceMovePer,
			CDecimal expLocalPriceMovePer,
			int slippageStp,
			CDecimal strategy_cap_share_per)
	{
		this.tradeGoalCapPer = tradeGoalCapPer;
		this.tradeLossCapPer = tradeLossCapPer;
		this.expDailyPriceMovePer = expDailyPriceMovePer;
		this.expLocalPriceMovePer = expLocalPriceMovePer;
		this.slippageStp = slippageStp;
		this.strategyCapSharePer = strategy_cap_share_per;
	}

	/**
	 * Get minimum trade goal in percentages relative to capital.
	 * <p> 
	 * @return percentage in range 0-1
	 */
	public CDecimal getTradeGoalCapPer() {
		return tradeGoalCapPer;
	}
	
	/**
	 * Get maximum trade loss in percentages relative to capital.
	 * <p>
	 * @return percentage in range 0-1
	 */
	public CDecimal getTradeLossCapPer() {
		return tradeLossCapPer;
	}
	
	/**
	 * Get expected daily price move in percentages relative to average daily move.
	 * <p>
	 * @return percentage in range 0-1
	 */
	public CDecimal getExpDailyPriceMovePer() {
		return expDailyPriceMovePer;
	}
	
	/**
	 * Get expected local price move in percentages relative to average local move.
	 * <p>
	 * @return percentage in range 0-1
	 */
	public CDecimal getExpLocalPriceMovePer() {
		return expLocalPriceMovePer;
	}
	
	/**
	 * Get slippage in price steps.
	 * <p>
	 * @return slippage
	 */
	public int getSlippageStp() {
		return slippageStp;
	}
	
	/**
	 * Get strategy capital share in percentages relative to total capital.
	 * For example if total capital is 1M and strategy share is 30% then strategy capital is 300K.
	 * <p>
	 * @return strategy capital share percentage 0-1 
	 */
	public CDecimal getStrategyCapSharePer() {
		return strategyCapSharePer;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(113003, 805)
				.append(tradeGoalCapPer)
				.append(tradeLossCapPer)
				.append(expDailyPriceMovePer)
				.append(expLocalPriceMovePer)
				.append(slippageStp)
				.append(strategyCapSharePer)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != RMContractStrategyParams.class ) {
			return false;
		}
		RMContractStrategyParams o = (RMContractStrategyParams) other;
		return new EqualsBuilder()
				.append(o.tradeGoalCapPer, tradeGoalCapPer)
				.append(o.tradeLossCapPer, tradeLossCapPer)
				.append(o.expDailyPriceMovePer, expDailyPriceMovePer)
				.append(o.expLocalPriceMovePer, expLocalPriceMovePer)
				.append(o.slippageStp, slippageStp)
				.append(o.strategyCapSharePer, strategyCapSharePer)
				.build();
	}
	
}
