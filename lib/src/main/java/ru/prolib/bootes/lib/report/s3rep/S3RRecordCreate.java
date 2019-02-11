package ru.prolib.bootes.lib.report.s3rep;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class S3RRecordCreate {
	protected final S3RType type;
	protected final Instant entryTime;
	protected final CDecimal entryPrice, qty, tp, sl, be;
	
	public S3RRecordCreate(S3RType type,
			Instant entryTime,
			CDecimal entryPrice,
			CDecimal qty,
			CDecimal tp,
			CDecimal sl,
			CDecimal be)
	{
		this.type = type;
		this.entryTime = entryTime;
		this.entryPrice = entryPrice;
		this.qty = qty;
		this.tp = tp;
		this.sl = sl;
		this.be = be;
	}
	
	public S3RType getType() {
		return type;
	}
	
	public Instant getEntryTime() {
		return entryTime;
	}
	
	public CDecimal getEntryPrice() {
		return entryPrice;
	}
	
	public CDecimal getQty() {
		return qty;
	}
	
	public CDecimal getTakeProfit() {
		return tp;
	}
	
	public CDecimal getStopLoss() {
		return sl;
	}
	
	public CDecimal getBreakEven() {
		return be;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(7009715, 19)
				.append(type)
				.append(entryTime)
				.append(entryPrice)
				.append(qty)
				.append(tp)
				.append(sl)
				.append(be)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != S3RRecordCreate.class ) {
			return false;
		}
		S3RRecordCreate o = (S3RRecordCreate) other;
		return new EqualsBuilder()
				.append(o.type, type)
				.append(o.entryTime, entryTime)
				.append(o.entryPrice, entryPrice)
				.append(o.qty, qty)
				.append(o.tp, tp)
				.append(o.sl, sl)
				.append(o.be, be)
				.build();
	}

}
