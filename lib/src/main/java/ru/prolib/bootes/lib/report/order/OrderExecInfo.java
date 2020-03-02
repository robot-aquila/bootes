package ru.prolib.bootes.lib.report.order;

import java.time.Instant;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class OrderExecInfo {
	protected final long num;
	protected final Instant time;
	protected final CDecimal price, qty, value;
	
	public OrderExecInfo(long num, Instant time, CDecimal price, CDecimal qty, CDecimal value) {
		this.num = num;
		this.time = time;
		this.price = price;
		this.qty = qty;
		this.value = value;
	}
	
	/**
	 * Get number of execution.
	 * <p>
	 * @return number of execution
	 */
	public long getNum() {
		return num;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public CDecimal getPrice() {
		return price;
	}
	
	public CDecimal getQty() {
		return qty;
	}
	
	public CDecimal getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderExecInfo.class ) {
			return false;
		}
		OrderExecInfo o = (OrderExecInfo) other;
		return new EqualsBuilder()
				.append(o.num, num)
				.append(o.time, time)
				.append(o.price, price)
				.append(o.qty, qty)
				.append(o.value, value)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(115243911, 37)
				.append(num)
				.append(time)
				.append(price)
				.append(qty)
				.append(value)
				.build();
	}

}
