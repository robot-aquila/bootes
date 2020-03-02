package ru.prolib.bootes.lib.report.order;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class OrderInfoTest {
	static final Symbol symbol1 = new Symbol("MSFT"), symbol2 = new Symbol("AAPL");
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	IMocksControl control;
	OrderExecInfo execMock1, execMock2, execMock3, execMock4, execMock5, execMock6;
	List<OrderExecInfo> execs1, execs2;
	OrderInfo service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		execs1 = new ArrayList<>();
		execs1.add(execMock1 = control.createMock(OrderExecInfo.class));
		execs1.add(execMock2 = control.createMock(OrderExecInfo.class));
		execs1.add(execMock3 = control.createMock(OrderExecInfo.class));
		execs2 = new ArrayList<>();
		execs2.add(execMock4 = control.createMock(OrderExecInfo.class));
		execs2.add(execMock5 = control.createMock(OrderExecInfo.class));
		execs2.add(execMock6 = control.createMock(OrderExecInfo.class));
		service = new OrderInfo(
				OrderAction.BUY,
				symbol1,
				20L,
				T("2020-03-01T17:41:00Z"),
				of("12.44"),
				of(10L),
				ofRUB2("24.88"),
				execs1
			);
	}
	
	@Test
	public void testGetters() {
		assertEquals(OrderAction.BUY, service.getAction());
		assertEquals(symbol1, service.getSymbol());
		assertEquals(20L, service.getNum());
		assertEquals(T("2020-03-01T17:41:00Z"), service.getTime());
		assertEquals(of("12.44"), service.getPrice());
		assertEquals(of(10L), service.getQty());
		assertEquals(ofRUB2("24.88"), service.getValue());
		assertEquals(execs1, service.getExecutions());
		assertNotSame(execs1, service.getExecutions());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("OrderInfo[action=BUY,symbol=MSFT,executions=[")
				.append(execMock1).append(", ")
				.append(execMock2).append(", ")
				.append(execMock3)
				.append("],num=20,time=2020-03-01T17:41:00Z,price=12.44,qty=10,value=24.88 RUB]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(1352411751, 41)
				.append(OrderAction.BUY)
				.append(new Symbol("MSFT"))
				.append(20L)
				.append(T("2020-03-01T17:41:00Z"))
				.append(of("12.44"))
				.append(of(10L))
				.append(ofRUB2("24.88"))
				.append(execs1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<OrderAction> vAct = new Variant<>(OrderAction.BUY, OrderAction.SELL);
		Variant<Symbol> vSym = new Variant<>(vAct, symbol1, symbol2);
		Variant<Long> vNum = new Variant<>(vSym, 20L, 159L);
		Variant<Instant> vTm = new Variant<>(vNum, T("2020-03-01T17:41:00Z"), T("2015-07-19T00:00:00Z"));
		Variant<CDecimal> vPr = new Variant<>(vTm, of("12.44"), of("420.97"));
		Variant<CDecimal> vQty = new Variant<>(vPr, of(10L), of(240L));
		Variant<CDecimal> vVal = new Variant<>(vQty, ofRUB2("24.88"), ofUSD5("0.11256"));
		Variant<List<OrderExecInfo>> vExe = new Variant<>(vVal, execs1, execs2);
		Variant<?> iterator = vExe;
		int found_cnt = 0;
		OrderInfo x, found = null;
		do {
			x = new OrderInfo(vAct.get(), vSym.get(), vNum.get(), vTm.get(), vPr.get(),
					vQty.get(), vVal.get(), vExe.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertNotNull(found);
		assertEquals(OrderAction.BUY, found.getAction());
		assertEquals(symbol1, found.getSymbol());
		assertEquals(20L, found.getNum());
		assertEquals(T("2020-03-01T17:41:00Z"), found.getTime());
		assertEquals(of("12.44"), found.getPrice());
		assertEquals(of(10L), found.getQty());
		assertEquals(ofRUB2("24.88"), found.getValue());
		assertEquals(execs1, found.getExecutions());
	}

}
