package ru.prolib.bootes.lib.report.order;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class OrderInfo extends OrderExecInfo {
	protected final OrderAction action;
	protected final Symbol symbol;
	protected final Collection<OrderExecInfo> executions;

	public OrderInfo(OrderAction action,
			Symbol symbol,
			long num,
			Instant time,
			CDecimal price,
			CDecimal qty,
			CDecimal value,
			Collection<OrderExecInfo> executions)
	{
		super(num, time, price, qty, value);
		this.action = action;
		this.symbol = symbol;
		this.executions = executions;
	}
	
	public OrderAction getAction() {
		return action;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public Collection<OrderExecInfo> getExecutions() {
		return new ArrayList<>(executions);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1352411751, 41)
				.append(action)
				.append(symbol)
				.append(num)
				.append(time)
				.append(price)
				.append(qty)
				.append(value)
				.append(executions)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderInfo.class ) {
			return false;
		}
		OrderInfo o = (OrderInfo) other;
		return new EqualsBuilder()
				.append(o.action, action)
				.append(o.symbol, symbol)
				.append(o.num, num)
				.append(o.time, time)
				.append(o.price, price)
				.append(o.qty, qty)
				.append(o.value, value)
				.append(o.executions, executions)
				.build();
	}
	
}
