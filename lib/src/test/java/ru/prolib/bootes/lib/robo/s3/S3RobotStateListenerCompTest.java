package ru.prolib.bootes.lib.robo.s3;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerComp;

public class S3RobotStateListenerCompTest {
	private IMocksControl control;
	private S3RobotStateListener listenerMock1, listenerMock2, listenerMock3;
	private Set<S3RobotStateListener> listeners;
	private S3RobotStateListenerComp service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		listenerMock1 = control.createMock(S3RobotStateListener.class);
		listenerMock2 = control.createMock(S3RobotStateListener.class);
		listenerMock3 = control.createMock(S3RobotStateListener.class);
		listeners = new LinkedHashSet<>();
		service = new S3RobotStateListenerComp(listeners);
	}
	
	@Test
	public void testAddListener() {
		assertSame(service, service.addListener(listenerMock1));
		assertSame(service, service.addListener(listenerMock2));
		
		Set<S3RobotStateListener> expected = new LinkedHashSet<>();
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
		
		Set<S3RobotStateListener> expected = new LinkedHashSet<>();
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
	public void testCpeculationUpdate() {
		listeners.add(listenerMock3);
		listeners.add(listenerMock2);
		listenerMock3.speculationUpdate();
		listenerMock2.speculationUpdate();
		control.replay();
		
		service.speculationUpdate();
		
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
	
	@Test
	public void testOrderFinished() {
		Order orderMock = control.createMock(Order.class);
		listeners.add(listenerMock1);
		listeners.add(listenerMock3);
		listenerMock1.orderFinished(orderMock);
		listenerMock3.orderFinished(orderMock);
		control.replay();
		
		service.orderFinished(orderMock);
		
		control.verify();
	}

}
