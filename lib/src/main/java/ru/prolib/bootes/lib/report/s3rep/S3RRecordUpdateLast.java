package ru.prolib.bootes.lib.report.s3rep;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class S3RRecordUpdateLast {
	private final Instant exitTime;
	private final CDecimal exitPrice, pl;
	
	public S3RRecordUpdateLast(Instant exitTime,
			CDecimal exitPrice,
			CDecimal pl)
	{
		this.exitTime = exitTime;
		this.exitPrice = exitPrice;
		this.pl = pl;
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
		return new HashCodeBuilder(3357189, 745)
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
		if ( other == null || other.getClass() != S3RRecordUpdateLast.class ) {
			return false;
		}
		S3RRecordUpdateLast o = (S3RRecordUpdateLast) other;
		return new EqualsBuilder()
				.append(o.exitTime, exitTime)
				.append(o.exitPrice, exitPrice)
				.append(o.pl, pl)
				.build();
	}
	
}
