package ru.prolib.bootes.lib.data.ts;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.bootes.lib.data.ts.CMASignalTrigger.ObjectLocatorStub;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;

public class CMASignalTriggerTest {
	static final Instant t1, t2, t3;
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	static {
		t1 = T("2027-01-15T00:00:00Z");
		t2 = T("1992-12-31T23:59:59Z");
		t3 = T("1723-10-01T00:00:00Z");
	}
	
	private IMocksControl control;
	private TAMath mathMock;
	private TSeries<CDecimal> fastMock, slowMock, priceMock;
	private CMASignalTrigger service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		mathMock = control.createMock(TAMath.class);
		fastMock = control.createMock(TSeries.class);
		slowMock = control.createMock(TSeries.class);
		priceMock = control.createMock(TSeries.class);
		service = new CMASignalTrigger(mathMock, new ObjectLocatorStub(fastMock, slowMock, priceMock));
	}
	
	@Test
	public void testGetSignal_None_NotCrossed() {
		expect(fastMock.getFirstIndexBefore(t1)).andReturn(120);
		expect(priceMock.getFirstBefore(t1)).andReturn(of("25.904"));
		expect(mathMock.cross(fastMock, slowMock, t1)).andReturn(0);
		control.replay();
		
		TSignal actual = service.getSignal(t1);
		
		control.verify();
		TSignal expected = new TSignal(t1, 120, SignalType.NONE, of("25.904"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSignal_None_UnableToDeterminePrice() {
		expect(fastMock.getFirstIndexBefore(t2)).andReturn(240);
		expect(priceMock.getFirstBefore(t2)).andReturn(null);
		control.replay();
		
		TSignal actual = service.getSignal(t2);
		
		control.verify();
		TSignal expected = new TSignal(t2, 240, SignalType.NONE, null);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSignal_Buy() {
		expect(fastMock.getFirstIndexBefore(t3)).andReturn(421);
		expect(priceMock.getFirstBefore(t3)).andReturn(of("900.07"));
		expect(mathMock.cross(fastMock, slowMock, t3)).andReturn(1);
		control.replay();
		
		TSignal actual = service.getSignal(t3);
		
		control.verify();
		TSignal expected = new TSignal(t3, 421, SignalType.BUY, of("900.07"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSignal_Sell() {
		expect(fastMock.getFirstIndexBefore(t2)).andReturn(508);
		expect(priceMock.getFirstBefore(t2)).andReturn(of("215.42"));
		expect(mathMock.cross(fastMock, slowMock, t2)).andReturn(-1);
		control.replay();
		
		TSignal actual = service.getSignal(t2);
		
		control.verify();
		TSignal expected = new TSignal(t2, 508, SignalType.SELL, of("215.42"));
		assertEquals(expected, actual);
	}

}
