package ru.prolib.bootes.lib.robo.s3.rh;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.bootes.lib.report.order.OrderReport;
import ru.prolib.bootes.lib.report.order.OrderReportUtils;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerStub;

public class OrderReportHandler extends S3RobotStateListenerStub {
	protected final OrderReportUtils utils;
	protected final OrderReport report;
	
	public OrderReportHandler(OrderReport report, OrderReportUtils utils) {
		this.report = report;
		this.utils = utils;
	}
	
	public OrderReportHandler(OrderReport report) {
		this(report, OrderReportUtils.getInstance());
	}

	@Override
	public void orderFinished(Order order) {
		report.addOrder(utils.fromOrder(order));
	}
	
}
