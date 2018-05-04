package ru.prolib.bootes.lib.service.task;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.SchedulerImpl;

public class ProbeRunTest {
	private IMocksControl control;
	private SchedulerImpl probeMock1, probeMock2;
	private ProbeRun service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		probeMock1 = control.createMock(SchedulerImpl.class);
		probeMock2 = control.createMock(SchedulerImpl.class);
		service = new ProbeRun(probeMock1);
	}
	
	@Test
	public void testRun() {
		probeMock1.setExecutionSpeed(0);
		probeMock1.setModeRun();
		control.replay();
		
		service.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new ProbeRun(probeMock1)));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
		assertFalse(service.equals(new ProbeRun(probeMock2)));
	}

}
