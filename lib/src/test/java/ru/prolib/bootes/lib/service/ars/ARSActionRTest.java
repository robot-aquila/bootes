package ru.prolib.bootes.lib.service.ars;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class ARSActionRTest {
	private IMocksControl control;
	private Runnable rMock1, rMock2;
	private ARSActionR service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rMock1 = control.createMock(Runnable.class);
		rMock2 = control.createMock(Runnable.class);
		service = new ARSActionR(rMock1);
	}
	
	@Test
	public void testCtor() {
		assertSame(rMock1, service.getRunnable());
	}
	
	@Test
	public void testRun() throws Throwable {
		rMock1.run();
		control.replay();
		
		service.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new ARSActionR(rMock1)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new ARSActionR(rMock2)));
	}

}
