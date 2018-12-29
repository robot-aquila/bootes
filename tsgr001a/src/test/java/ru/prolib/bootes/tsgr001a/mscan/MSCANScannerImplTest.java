package ru.prolib.bootes.tsgr001a.mscan;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class MSCANScannerImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private MSCANListener listenerMock1, listenerMock2;
	private MSCANSensor sensorMock1, sensorMock2, sensorMock3;
	private Set<MSCANSensor> sensors;
	private Set<MSCANListener> listeners;
	private Set<MSCANScannerImpl.EventNode> events;
	private MSCANScannerImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		listenerMock1 = control.createMock(MSCANListener.class);
		listenerMock2 = control.createMock(MSCANListener.class);
		sensorMock1 = control.createMock(MSCANSensor.class);
		sensorMock2 = control.createMock(MSCANSensor.class);
		sensorMock3 = control.createMock(MSCANSensor.class);
		sensors = new LinkedHashSet<>();
		listeners = new LinkedHashSet<>();
		events = new LinkedHashSet<>();
		service = new MSCANScannerImpl(sensors, listeners, events);
	}
	
	@Test
	public void testAddSensor() {
		service.addSensor(sensorMock2);
		service.addSensor(sensorMock1);
		
		Set<MSCANSensor> expected = new LinkedHashSet<>();
		expected.add(sensorMock2);
		expected.add(sensorMock1);
		assertEquals(expected, sensors);
	}
	
	@Test
	public void testRemoveSensor() {
		sensors.add(sensorMock1);
		sensors.add(sensorMock2);
		sensors.add(sensorMock3);
		
		service.removeSensor(sensorMock2);
		
		Set<MSCANSensor> expected = new LinkedHashSet<>();
		expected.add(sensorMock1);
		expected.add(sensorMock3);
		assertEquals(expected, sensors);
	}
	
	@Test
	public void testAddListener() {
		service.addListener(listenerMock2);
		service.addListener(listenerMock1);
		
		Set<MSCANListener> expected = new LinkedHashSet<>();
		expected.add(listenerMock2);
		expected.add(listenerMock1);
		assertEquals(expected, listeners);
	}
	
	@Test
	public void testRemoveListener() {
		listeners.add(listenerMock2);
		listeners.add(listenerMock1);
		
		service.removeListener(listenerMock1);
		
		Set<MSCANListener> expected = new LinkedHashSet<>();
		expected.add(listenerMock2);
		assertEquals(expected, listeners);
	}
	
	private void addAllListenersAndSensors() {
		service.addSensor(sensorMock1);
		service.addSensor(sensorMock2);
		service.addListener(listenerMock1);
		service.addListener(listenerMock2);
	}
	
	@Test
	public void testAnalyze_Skipped() {
		addAllListenersAndSensors();
		Instant time = T("1990-10-01T15:40:32Z");
		MSCANLogEntry entry1 = new MSCANLogEntryImpl(
				true,
				"BUY",
				T("1990-10-01T15:40:32.005Z"),
				of("290.114"),
				"Test"
			);
		MSCANEvent event = new MSCANEventImpl(entry1);
		expect(sensorMock1.analyze(time)).andReturn(null);
		expect(sensorMock2.analyze(time)).andReturn(event);
		listenerMock1.onEventSkipped(event);
		listenerMock2.onEventSkipped(event);
		control.replay();
		
		service.analyze(time);
		
		control.verify();
		assertEquals(new LinkedHashSet<>(), events);
	}
	
	@Test
	public void testAnalyze_Started() {
		addAllListenersAndSensors();
		Instant time = T("2015-06-12T20:56:10Z");
		MSCANLogEntry entry1 = new MSCANLogEntryImpl(
				true,
				"BUY",
				T("2015-06-12T20:56:10.003Z"),
				of("117.503"),
				"Test 1"
			);
		MSCANEvent event1 = new MSCANEventImpl(entry1);
		MSCANLogEntry entry2 = new MSCANLogEntryImpl(
				false,
				"SELL",
				T("2015-06-12T20:56:10.006Z"),
				of("105.003"),
				"Test 2"
			);
		MSCANEvent event2 = new MSCANEventImpl(entry2);
		expect(sensorMock1.analyze(time)).andReturn(event1);
		listenerMock1.onEventSkipped(event1);
		listenerMock2.onEventSkipped(event1);
		expect(sensorMock2.analyze(time)).andReturn(event2);
		listenerMock1.onEventStarted(event2);
		listenerMock2.onEventStarted(event2);
		control.replay();
		
		service.analyze(time);
		
		control.verify();
		Set<MSCANScannerImpl.EventNode> expected = new LinkedHashSet<>();
		expected.add(new MSCANScannerImpl.EventNode(event2, sensorMock2));
		assertEquals(expected, events);
	}
	
	@Test
	public void testAnalyze_Changed() {
		service.addListener(listenerMock1);
		service.addListener(listenerMock2);
		Instant time = T("1991-08-12T23:19:48Z");
		MSCANLogEntry entry1 = new MSCANLogEntryImpl(
				false,
				"BUY",
				T("1991-08-12T20:15:51Z"),
				of("904.557"),
				"Test 1"
			);
		MSCANEvent event = new MSCANEventImpl(entry1);
		MSCANLogEntry entry2 = new MSCANLogEntryImpl(
				false,
				"CHECK",
				T("1991-08-12T23:19:45Z"),
				of("835.904"),
				"Test 2"
			);
		events.add(new MSCANScannerImpl.EventNode(event, sensorMock1));
		expect(sensorMock1.analyze(time, event)).andReturn(entry2);
		listenerMock1.onEventChanged(event, entry2);
		listenerMock2.onEventChanged(event, entry2);
		control.replay();
		
		service.analyze(time);
		
		control.verify();
		Set<MSCANScannerImpl.EventNode> expected = new LinkedHashSet<>();
		expected.add(new MSCANScannerImpl.EventNode(event, sensorMock1));
		assertEquals(expected, events);
	}

	@Test
	public void testAnalyze_Closed() {
		service.addListener(listenerMock1);
		service.addListener(listenerMock2);
		Instant time = T("2018-12-27T22:14:00Z");
		MSCANLogEntry entry1 = new MSCANLogEntryImpl(
				false,
				"BUY",
				T("2018-12-27T22:00:00Z"),
				of("113.276"),
				"open long"
			);
		MSCANEvent event = new MSCANEventImpl(entry1);
		MSCANLogEntry entry2 = new MSCANLogEntryImpl(
				true,
				"CLOSE",
				T("2018-12-27T22:14:00.023Z"),
				of("112.540"),
				"close long"
			);
		events.add(new MSCANScannerImpl.EventNode(event, sensorMock2));
		expect(sensorMock2.analyze(time, event)).andReturn(entry2);
		listenerMock1.onEventClosed(event);
		listenerMock2.onEventClosed(event);
		control.replay();
		
		service.analyze(time);
		
		control.verify();
		assertEquals(new LinkedHashSet<>(), events);
	}

}
