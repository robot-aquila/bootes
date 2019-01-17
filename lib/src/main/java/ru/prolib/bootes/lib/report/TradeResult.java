package ru.prolib.bootes.lib.report;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class TradeResult {
	private final Instant start, end;
	private final boolean isLong;
	private final CDecimal pnl, qty;
	
	public TradeResult(Instant start,
			Instant end,
			boolean isLong,
			CDecimal pnl,
			CDecimal qty)
	{
		this.start = start;
		this.end = end;
		this.isLong = isLong;
		this.pnl = pnl;
		this.qty = qty;
	}
	
	public Instant getStartTime() {
		return start;
	}
	
	public Instant getEndTime() {
		return end;
	}
	
	public boolean isLong() {
		return isLong;
	}
	
	public boolean isShort() {
		return ! isLong;
	}
	
	public CDecimal getPnL() {
		return pnl;
	}
	
	public CDecimal getQty() {
		return qty;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(8163921, 6671)
				.append(start)
				.append(end)
				.append(isLong)
				.append(pnl)
				.append(qty)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TradeResult.class ) {
			return false;
		}
		TradeResult o = (TradeResult) other;
		return new EqualsBuilder()
				.append(o.start, start)
				.append(o.end, end)
				.append(o.isLong, isLong)
				.append(o.pnl, pnl)
				.append(o.qty, qty)
				.build();
	}

}
