package ru.prolib.bootes.lib.report.order;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;

import org.junit.Before;
import org.junit.Test;

public class OrderReportTest {
	IMocksControl control;
	List<OrderInfo> orders;
	OrderInfo orderMock1, orderMock2, orderMock3;
	OrderReport service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		orderMock1 = control.createMock(OrderInfo.class);
		orderMock2 = control.createMock(OrderInfo.class);
		orderMock3 = control.createMock(OrderInfo.class);
		orders = new ArrayList<>();
		service = new OrderReport(orders);
	}
	
	@Test
	public void testAddOrder() {
		service.addOrder(orderMock1);
		service.addOrder(orderMock2);
		service.addOrder(orderMock3);
		
		Collection<OrderInfo> expected = Arrays.asList(orderMock1, orderMock2, orderMock3);
		assertEquals(expected, orders);
	}

	@Test
	public void testGetOrders() {
		orders.add(orderMock1);
		orders.add(orderMock2);
		orders.add(orderMock3);
		
		Collection<OrderInfo> actual = service.getOrders();
		
		assertNotSame(orders, actual);
		assertEquals(orders, actual);
	}

}
