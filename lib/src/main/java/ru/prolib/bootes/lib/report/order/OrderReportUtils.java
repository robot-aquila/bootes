package ru.prolib.bootes.lib.report.order;

import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;

public class OrderReportUtils {
	private static final OrderReportUtils instance = new OrderReportUtils();
	
	public static OrderReportUtils getInstance() {
		return instance;
	}
	
	public OrderInfo fromOrder(Order order) {
		List<OrderExecInfo> execs = new ArrayList<>();
		int num = 1;
		for ( OrderExecution exec : order.getExecutions() ) {
			execs.add(new OrderExecInfo(num, exec.getTime(), exec.getPricePerUnit(), exec.getVolume(), exec.getValue()));
			num ++;
		}
		return new OrderInfo(
				order.getAction(),
				order.getSymbol(),
				execs.size(),
				order.getTime(),
				order.getPrice(),
				order.getInitialVolume(),
				order.getExecutedValue(),
				execs
			);
	}

}
