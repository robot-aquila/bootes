package ru.prolib.bootes.tsgr001a.robot;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class RobotStateListenerCompTest {
	private IMocksControl control;
	private RobotStateListener listenerMock1, listenerMock2, listenerMock3;
	private Set<RobotStateListener> listeners;
	private RobotStateListenerComp service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		listenerMock1 = control.createMock(RobotStateListener.class);
		listenerMock2 = control.createMock(RobotStateListener.class);
		listenerMock3 = control.createMock(RobotStateListener.class);
		listeners = new LinkedHashSet<>();
		service = new RobotStateListenerComp(listeners);
	}
	
	@Test
	public void testAddListener() {
		assertSame(service, service.addListener(listenerMock1));
		assertSame(service, service.addListener(listenerMock2));
		
		Set<RobotStateListener> expected = new LinkedHashSet<>();
		expected.add(listenerMock1);
		expected.add(listenerMock2);
		assertEquals(expected, listeners);
	}
	
	@Test
	public void testRemoveListener() {
		listeners.add(listenerMock1);
		listeners.add(listenerMock2);
		listeners.add(listenerMock3);
		
		assertSame(service, service.removeListener(listenerMock2));
		
		Set<RobotStateListener> expected = new LinkedHashSet<>();
		expected.add(listenerMock1);
		expected.add(listenerMock3);
		assertEquals(expected, listeners);
		
		assertSame(service, service.removeListener(listenerMock1));
		
		expected.clear();
		expected.add(listenerMock3);
		assertEquals(expected, listeners);
	}
	
	@Test
	public void testRobotStarted() {
		listeners.add(listenerMock1);
		listeners.add(listenerMock2);
		listenerMock1.robotStarted();
		listenerMock2.robotStarted();
		control.replay();
		
		service.robotStarted();
		
		control.verify();
	}
	
	@Test
	public void testAccountSelected() {
		listeners.add(listenerMock2);
		listeners.add(listenerMock3);
		listenerMock2.accountSelected();
		listenerMock3.accountSelected();
		control.replay();
		
		service.accountSelected();
		
		control.verify();
	}
	
	@Test
	public void testContractSelected() {
		listeners.add(listenerMock3);
		listeners.add(listenerMock1);
		listenerMock3.contractSelected();
		listenerMock1.contractSelected();
		control.replay();
		
		service.contractSelected();
		
		control.verify();
	}
	
	@Test
	public void testSessionDataAvailable() {
		listeners.add(listenerMock2);
		listeners.add(listenerMock1);
		listenerMock2.sessionDataAvailable();
		listenerMock1.sessionDataAvailable();
		control.replay();
		
		service.sessionDataAvailable();
		
		control.verify();
	}
	
	@Test
	public void testRiskManagementUpdate() {
		listeners.add(listenerMock3);
		listeners.add(listenerMock2);
		listeners.add(listenerMock1);
		listenerMock3.riskManagementUpdate();
		listenerMock2.riskManagementUpdate();
		listenerMock1.riskManagementUpdate();
		control.replay();
		
		service.riskManagementUpdate();
		
		control.verify();
	}
	
	@Test
	public void testSpeculationOpened() {
		listeners.add(listenerMock2);
		listeners.add(listenerMock1);
		listenerMock2.speculationOpened();
		listenerMock1.speculationOpened();
		control.replay();
		
		service.speculationOpened();
		
		control.verify();
	}
	
	@Test
	public void testSpeculationClosed() {
		listeners.add(listenerMock1);
		listeners.add(listenerMock2);
		listenerMock1.speculationClosed();
		listenerMock2.speculationClosed();
		control.replay();
		
		service.speculationClosed();
		
		control.verify();
	}
	
	@Test
	public void testSessionDataCleanup() {
		listeners.add(listenerMock3);
		listeners.add(listenerMock2);
		listenerMock3.sessionDataCleanup();
		listenerMock2.sessionDataCleanup();
		control.replay();
		
		service.sessionDataCleanup();
		
		control.verify();
	}

	@Test
	public void testRobotStopped() {
		listeners.add(listenerMock2);
		listeners.add(listenerMock3);
		listenerMock2.robotStopped();
		listenerMock3.robotStopped();
		control.replay();
		
		service.robotStopped();
		
		control.verify();
	}

}
