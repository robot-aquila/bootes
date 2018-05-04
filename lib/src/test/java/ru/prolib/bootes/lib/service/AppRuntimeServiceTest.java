package ru.prolib.bootes.lib.service;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AppRuntimeServiceTest {
	private IMocksControl control;
	private Runnable rMock1, rMock2;
	private List<Runnable> startActionsStub, shutdownActionsStub;
	private AppRuntimeServiceImpl service;
	
	@Rule
	public ExpectedException eex = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rMock1 = control.createMock(Runnable.class);
		rMock2 = control.createMock(Runnable.class);
		startActionsStub = new ArrayList<>();
		shutdownActionsStub = new ArrayList<>();
		service = new AppRuntimeServiceImpl(startActionsStub, shutdownActionsStub);
	}
	
	@Test
	public void testCtor() {
		assertFalse(service.isStarted());
		assertFalse(service.isShutdown());
	}
	
	@Test
	public void testAddStartAction() {
		control.replay();
		
		service.addStartAction(rMock1);
		service.addStartAction(rMock2);

		control.verify();
		List<Runnable> expected = new ArrayList<>();
		expected.add(rMock1);
		expected.add(rMock2);
		assertEquals(expected, startActionsStub);
		assertEquals(new ArrayList<>(), shutdownActionsStub);
	}
	
	@Test
	public void testAddStartAction_ThrowsIfStarted() {
		service.triggerStart();
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("App already started");
		
		service.addStartAction(rMock1);
	}
	
	@Test
	public void testAddShutdownAction() {
		control.replay();
		
		service.addShutdownAction(rMock2);
		service.addShutdownAction(rMock1);
		
		control.verify();
		List<Runnable> expected = new ArrayList<>();
		expected.add(rMock2);
		expected.add(rMock1);
		assertEquals(expected, shutdownActionsStub);
		assertEquals(new ArrayList<>(), startActionsStub);
	}
	
	@Test
	public void testAddShutdownAction_OkIfStarted() {
		service.addShutdownAction(rMock1);
		service.triggerStart();
		control.replay();
		
		service.addShutdownAction(rMock2);
		
		control.verify();
		List<Runnable> expected = new ArrayList<>();
		expected.add(rMock1);
		expected.add(rMock2);
		assertEquals(expected, shutdownActionsStub);
	}
	
	@Test
	public void testAddShutdownAction_ThrowsIfShutdown() {
		service.triggerStart();
		service.triggerShutdown();
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("App already shutdown");
		
		service.addShutdownAction(rMock1);
	}
	
	@Test
	public void testTriggerStart() {
		service.addStartAction(rMock1);
		service.addStartAction(rMock2);
		rMock1.run();
		rMock2.run();
		control.replay();
		
		service.triggerStart();
		
		control.verify();
		assertTrue(service.isStarted());
		assertFalse(service.isShutdown());
	}
	
	@Test
	public void testTriggerStart_ThrowsIfStarted() {
		service.triggerStart();
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("App already started");
		
		service.triggerStart();
	}
	
	@Test
	public void testTriggerShutdown() {
		service.triggerStart();
		service.addShutdownAction(rMock2);
		service.addShutdownAction(rMock1);
		rMock2.run();
		rMock1.run();
		control.replay();
		
		service.triggerShutdown();
		
		control.verify();
		assertTrue(service.isStarted());
		assertTrue(service.isShutdown());
	}
	
	@Test
	public void testTriggerShutdown_ThrowsIfNotStarted() {
		service.addShutdownAction(rMock1);
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("App not started yet");
		
		service.triggerShutdown();
	}
	
	@Test
	public void testTriggerShutdown_ThrowsIfShutdown() {
		service.triggerStart();
		service.triggerShutdown();
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("App already shutdown");
		
		service.triggerShutdown();
	}

}
