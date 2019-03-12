package ru.prolib.bootes.lib.data.ts;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.TStamped;

/**
 * Trade signal of short-term speculative strategy (S3).
 * Signal is a directive to open or change position.
 */
public class S3TradeSignal implements TStamped {
	private final SignalType type;
	private Instant time;
	private final CDecimal expectedPrice, expectedQty, takeProfitPts,  stopLossPts, slippagePts,
		baseCap, goalCap, lossCap;
	
	public S3TradeSignal(SignalType type,
			Instant time,
			CDecimal expectedPrice,
			CDecimal expectedQty,
			CDecimal takeProfitPts,
			CDecimal stopLossPts,
			CDecimal slippagePts,
			CDecimal baseCap,
			CDecimal goalCap,
			CDecimal lossCap)
	{
		this.type = type;
		this.time = time;
		this.expectedPrice = expectedPrice;
		this.expectedQty = expectedQty;
		this.takeProfitPts = takeProfitPts;
		this.stopLossPts = stopLossPts;
		this.slippagePts = slippagePts;
		this.baseCap = baseCap;
		this.goalCap = goalCap;
		this.lossCap = lossCap;
	}
	
	/**
	 * Get type of trade.
	 * <p>
	 * @return type
	 */
	public SignalType getType() {
		return type;
	}
	
	/**
	 * Get time of signal detected.
	 * <p>
	 * @return time
	 */
	@Override
	public Instant getTime() {
		return time;
	}
	
	/**
	 * Get expected price of trade.
	 * <p>
	 * @return price
	 */
	public CDecimal getExpectedPrice() {
		return expectedPrice;
	}
	
	/**
	 * Get expected quantity of trade.
	 * <p>
	 * @return quantity
	 */
	public CDecimal getExpectedQty() {
		return expectedQty;
	}
	
	/**
	 * Get take-profit points of the trade.
	 * <p>
	 * @return take-profit
	 */
	public CDecimal getTakeProfitPts() {
		return takeProfitPts;
	}
	
	/**
	 * Get stop-loss points of the trade.
	 * <p>
	 * @return stop-loss
	 */
	public CDecimal getStopLossPts() {
		return stopLossPts;
	}
	
	public CDecimal getSlippagePts() {
		return slippagePts;
	}
	
	/**
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
	
	/**
	 * Get goal of capital increment.
	 * <p> 
	 * @return goal in money
	 */
	public CDecimal getGoalCap() {
		return goalCap;
	}
	
	/**
	 * Get maximum of capital loss.
	 * <p>
	 * @return possible loss in money
	 */
	public CDecimal getLossCap() {
		return lossCap;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(9007861, 419)
				.append(type)
				.append(time)
				.append(expectedPrice)
				.append(expectedQty)
				.append(takeProfitPts)
				.append(stopLossPts)
				.append(slippagePts)
				.append(baseCap)
				.append(goalCap)
				.append(lossCap)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != S3TradeSignal.class ) {
			return false;
		}
		S3TradeSignal o = (S3TradeSignal) other;
		return new EqualsBuilder()
				.append(o.type, type)
				.append(o.time, time)
				.append(o.expectedPrice, expectedPrice)
				.append(o.expectedQty, expectedQty)
				.append(o.takeProfitPts, takeProfitPts)
				.append(o.stopLossPts, stopLossPts)
				.append(o.slippagePts, slippagePts)
				.build();
	}

}
