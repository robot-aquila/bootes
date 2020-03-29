package ru.prolib.bootes.lib.report.summarep;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Trade sequence summary information.
 */
public class SRTradeSSI {
	private final CDecimal pnl;
	private final int count;
	
	public SRTradeSSI(CDecimal pnl, int count) {
		this.pnl = pnl;
		this.count = count;
	}
	
	public CDecimal getPnL() {
		return pnl;
	}
	
	public int getCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1955721, 307)
				.append(pnl)
				.append(count)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SRTradeSSI.class ) {
			return false;
		}
		SRTradeSSI o = (SRTradeSSI) other;
		return new EqualsBuilder()
				.append(o.pnl, pnl)
				.append(o.count, count)
				.build();
	}

}
