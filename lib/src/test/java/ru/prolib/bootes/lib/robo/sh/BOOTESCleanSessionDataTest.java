package ru.prolib.bootes.lib.robo.sh;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.sm.OnInterruptAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.bootes.lib.robo.ISessionDataHandler;
import ru.prolib.bootes.lib.robo.RobotStateListener;
import ru.prolib.bootes.lib.robo.sh.statereq.ISessionDataTrackable;

public class BOOTESCleanSessionDataTest {
	private IMocksControl control;
	private ISessionDataTrackable stateMock;
	private RobotStateListener rslMock;
	private ISessionDataHandler dhMock;
	private BOOTESCleanSessionData service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		stateMock = control.createMock(ISessionDataTrackable.class);
		rslMock = control.createMock(RobotStateListener.class);
		dhMock = control.createMock(ISessionDataHandler.class);
		service = new BOOTESCleanSessionData(stateMock);
	}
	
	@Test
	public void testCtor() {
		assertSame(service, service.getEnterAction());
		
		assertEquals(3, service.getExits().size());
		assertNotNull(service.getExit("OK"));
		assertNotNull(service.getExit("ERROR"));
		assertNotNull(service.getExit("INTERRUPT"));
		
		List<SMInput> actual_inputs = service.getInputs();
		assertEquals(1, actual_inputs.size());
		assertTrue(actual_inputs.contains(new SMInput(service, new OnInterruptAction(service))));
	}
	
	@Test
	public void testGetInterrupt() {
		assertEquals(new SMInput(service, new OnInterruptAction(service)), service.getInterrupt());
	}
	
	@Test
	public void testOnInterrupt() {
		control.replay();
		
		assertEquals(service.getExit("INTERRUPT"), service.onInterrupt(null));
		
		control.verify();
	}

	@Test
	public void testEnter() {
		expect(stateMock.getSessionDataHandler()).andStubReturn(dhMock);
		expect(stateMock.getStateListener()).andStubReturn(rslMock);
		dhMock.cleanSession();
		rslMock.sessionDataCleanup();
		control.replay();
		
		service.enter(null);
		
		control.verify();
	}

}
