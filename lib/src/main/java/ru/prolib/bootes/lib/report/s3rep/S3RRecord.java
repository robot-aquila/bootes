package ru.prolib.bootes.lib.report.s3rep;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class S3RRecord extends S3RRecordCreate {
	protected final int id;
	protected final Instant exitTime;
	protected final CDecimal exitPrice, pl;
	
	public S3RRecord(int id,
			S3RType type,
			Instant entryTime,
			CDecimal entryPrice,
			CDecimal qty,
			CDecimal tp,
			CDecimal sl,
			CDecimal be,
			Instant exitTime,
			CDecimal exitPrice,
			CDecimal pl)
	{
		super(type, entryTime, entryPrice, qty, tp, sl, be);
		this.id = id;
		this.exitTime = exitTime;
		this.exitPrice = exitPrice;
		this.pl = pl;
	}
	
	public S3RRecord(int id,
			S3RType type,
			Instant entryTime,
			CDecimal entryPrice,
			CDecimal qty,
			CDecimal tp,
			CDecimal sl,
			CDecimal be)
	{
		this(id, type, entryTime, entryPrice, qty, tp, sl, be, null, null, null);
	}
	
	public int getID() {
		return id;
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
	
	public Instant getExitTime() {
		return exitTime;
	}
	
	public CDecimal getExitPrice() {
		return exitPrice;
	}
	
	public CDecimal getProfitAndLoss() {
		return pl;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(112591, 403)
				.append(id)
				.append(type)
				.append(entryTime)
				.append(entryPrice)
				.append(qty)
				.append(tp)
				.append(sl)
				.append(be)
				.append(exitTime)
				.append(exitPrice)
				.append(pl)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != S3RRecord.class ) {
			return false;
		}
		S3RRecord o = (S3RRecord) other;
		return new EqualsBuilder()
				.append(o.id, id)
				.append(o.type, type)
				.append(o.entryTime, entryTime)
				.append(o.entryPrice, entryPrice)
				.append(o.qty, qty)
				.append(o.tp, tp)
				.append(o.sl, sl)
				.append(o.be, be)
				.append(o.exitTime, exitTime)
				.append(o.exitPrice, exitPrice)
				.append(o.pl, pl)
				.build();
	}
	
}
