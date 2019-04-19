package ru.prolib.bootes.lib.data.ts;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.bootes.lib.data.ts.CMASignalTrigger.ObjectLocatorStub;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;

public class CMASignalTriggerTest {
	private IMocksControl control;
	private TAMath mathMock;
	private TSeries<CDecimal> fastMock, slowMock;
	private CMASignalTrigger service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		mathMock = control.createMock(TAMath.class);
		fastMock = control.createMock(TSeries.class);
		slowMock = control.createMock(TSeries.class);
		service = new CMASignalTrigger(mathMock, new ObjectLocatorStub(fastMock, slowMock));
	}
	
	@Test
	public void testGetSignal() {
		Instant t = Instant.EPOCH;
		expect(mathMock.cross(fastMock, slowMock, t))
			.andReturn( 0)
			.andReturn(-1)
			.andReturn( 1);
		control.replay();
		
		assertEquals(SignalType.NONE, service.getSignal(t));
		assertEquals(SignalType.SELL, service.getSignal(t));
		assertEquals(SignalType.BUY,  service.getSignal(t));
		
		control.verify();
	}

}
