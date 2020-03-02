package ru.prolib.bootes.lib.robo.s3.rh;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.bootes.lib.report.order.OrderInfo;
import ru.prolib.bootes.lib.report.order.OrderReport;
import ru.prolib.bootes.lib.report.order.OrderReportUtils;

public class OrderReportHandlerTest {
	IMocksControl control;
	Order orderMock;
	OrderReport reportMock;
	OrderReportUtils utilsMock;
	OrderInfo orderInfoMock;
	OrderReportHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		orderMock = control.createMock(Order.class);
		reportMock = control.createMock(OrderReport.class);
		utilsMock = control.createMock(OrderReportUtils.class);
		orderInfoMock = control.createMock(OrderInfo.class);
		service = new OrderReportHandler(reportMock, utilsMock);
	}

	@Test
	public void testOrderFinished() {
		expect(utilsMock.fromOrder(orderMock)).andReturn(orderInfoMock);
		reportMock.addOrder(orderInfoMock);
		control.replay();
		
		service.orderFinished(orderMock);
		
		control.verify();
	}

}
