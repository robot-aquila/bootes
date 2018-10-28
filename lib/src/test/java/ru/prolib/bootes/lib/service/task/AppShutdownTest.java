package ru.prolib.bootes.lib.service.task;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.app.AppRuntimeService;

public class AppShutdownTest {
	private IMocksControl control;
	private AppRuntimeService rtsMock1, rtsMock2;
	private AppShutdown service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rtsMock1 = control.createMock(AppRuntimeService.class);
		rtsMock2 = control.createMock(AppRuntimeService.class);
		service = new AppShutdown(rtsMock1);
	}
	
	@Test
	public void testRun() {
		rtsMock1.shutdown();
		control.replay();
		
		service.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new AppShutdown(rtsMock1)));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
		assertFalse(service.equals(new AppShutdown(rtsMock2)));
	}

}
