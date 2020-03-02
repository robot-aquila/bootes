package ru.prolib.bootes.lib.report.order;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class OrderReportUtilsTest {
	static Symbol symbol = new Symbol("SPY");
	static Account account = new Account("TEST-01");
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	OrderReportUtils service;
	EditableTerminal terminal;

	@Before
	public void setUp() throws Exception {
		service = new OrderReportUtils();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		terminal.getEditableSecurity(symbol);
		terminal.getEditablePortfolio(account);
	}
	
	@Test
	public void testGetInstance() {
		OrderReportUtils actual = OrderReportUtils.getInstance();
		assertNotNull(actual);
		assertSame(OrderReportUtils.getInstance(), actual);
		assertSame(OrderReportUtils.getInstance(), actual);
	}

	@Test
	public void testFromOrder() throws Exception {
		EditableOrder order = (EditableOrder)
				terminal.createOrder(account, symbol, OrderAction.BUY, of(12L), of(110250L));
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.TIME, T("2020-03-01T23:17:45Z"))
				.withToken(OrderField.EXECUTED_VALUE, ofRUB2("102.37"))
				.buildUpdate());
		order.addExecution(101L, "x01", T("2020-03-01T23:17:59.204Z"), of(110240L), of(2L), ofRUB2("16.08"));
		order.addExecution(102L, "x02", T("2020-03-01T23:25:04.003Z"), of(110250L), of(1L), ofRUB2("8.04"));
		order.addExecution(103L, "x03", T("2020-03-01T23:47:19.026Z"), of(110230L), of(9L), ofRUB2("72.12"));
		
		OrderInfo actual = service.fromOrder(order);
		
		OrderInfo expected = new OrderInfo(
				OrderAction.BUY,
				symbol,
				3L,
				T("2020-03-01T23:17:45Z"),
				of(110250L),
				of(12L),
				ofRUB2("102.37"),
				Arrays.asList(
						new OrderExecInfo(1L, T("2020-03-01T23:17:59.204Z"), of(110240L), of(2L), ofRUB2("16.08")),
						new OrderExecInfo(2L, T("2020-03-01T23:25:04.003Z"), of(110250L), of(1L), ofRUB2( "8.04")),
						new OrderExecInfo(3L, T("2020-03-01T23:47:19.026Z"), of(110230L), of(9L), ofRUB2("72.12"))
					)
			);
		assertEquals(expected, actual);
	}

}
