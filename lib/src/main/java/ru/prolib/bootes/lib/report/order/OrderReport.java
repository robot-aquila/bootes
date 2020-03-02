package ru.prolib.bootes.lib.report.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderReport {
	private final List<OrderInfo> orders;
	
	public OrderReport(List<OrderInfo> orders) {
		this.orders = orders;
	}
	
	public OrderReport() {
		this(new ArrayList<>());
	}
	
	public void addOrder(OrderInfo order) {
		orders.add(order);
	}
	
	public Collection<OrderInfo> getOrders() {
		return new ArrayList<>(orders);
	}
	
}
