package ru.prolib.bootes.lib.report.s3rep;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class S3ReportTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private IS3ReportListener listenerMock1, listenerMock2, listenerMock3;
	private List<S3RRecord> records;
	private Set<IS3ReportListener> listeners;
	private S3Report service;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		listenerMock1 = control.createMock(IS3ReportListener.class);
		listenerMock2 = control.createMock(IS3ReportListener.class);
		listenerMock3 = control.createMock(IS3ReportListener.class);
		records = new ArrayList<>();
		listeners = new LinkedHashSet<>();
		service = new S3Report(records, listeners);
	}
	
	@Test
	public void testGetRecordCount() {
		assertEquals(0, service.getRecordCount());
		
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		
		assertEquals(3, service.getRecordCount());
		
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		
		assertEquals(6, service.getRecordCount());
	}
	
	@Test
	public void testGetRecord() {
		S3RRecord recMock1, recMock2, recMock3;
		records.add(recMock1 = control.createMock(S3RRecord.class));
		records.add(recMock2 = control.createMock(S3RRecord.class));
		records.add(recMock3 = control.createMock(S3RRecord.class));
		
		assertSame(recMock1, service.getRecord(0));
		assertSame(recMock2, service.getRecord(1));
		assertSame(recMock3, service.getRecord(2));
	}
	
	@Test (expected=IndexOutOfBoundsException.class)
	public void testGetRecord_ThrowsIfOutOfBounds() {
		service.getRecord(0);
	}
	
	@Test
	public void testUpdate_Last() {
		records.add(control.createMock(S3RRecord.class));
		records.add(new S3RRecord(
				2L,
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00")
			));
		S3RRecord expected = new S3RRecord(
				2L,
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00"),
				T("2019-02-12T12:45:00Z"),
				of("199.98"),
				ofRUB5("-28.34")
			);
		listenerMock1.recordUpdated(expected);
		listenerMock2.recordUpdated(expected);
		control.replay();
		service.addListener(listenerMock1);
		service.addListener(listenerMock2);
		
		S3RRecord actual = service.update(new S3RRecordUpdateLast(
				T("2019-02-12T12:45:00Z"),
				of("199.98"),
				ofRUB5("-28.34")
			));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IndexOutOfBoundsException.class)
	public void testUpdate_Last_ThrowsIfNotExists() {
		service.update(new S3RRecordUpdateLast(
				T("2019-02-12T12:45:00Z"),
				of("199.98"),
				ofRUB5("-28.34")
			));
	}
	
	@Test
	public void testCreate() {
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		S3RRecord expected = new S3RRecord(
				2L,
				S3RType.LONG,
				T("2019-02-12T15:13:00Z"),
				of("527.12"),
				of("100"),
				of("635.00"),
				of("520.00"),
				of("531.19")
			);
		listenerMock3.recordCreated(expected);
		listenerMock1.recordCreated(expected);
		control.replay();
		service.addListener(listenerMock3);
		service.addListener(listenerMock1);
		
		S3RRecord actual = service.create(new S3RRecordCreate(
				S3RType.LONG,
				T("2019-02-12T15:13:00Z"),
				of("527.12"),
				of("100"),
				of("635.00"),
				of("520.00"),
				of("531.19")
			));
		
		control.verify();
		assertEquals(expected, actual);
		assertEquals(3, records.size());
		assertSame(actual, records.get(2));
	}
	
	@Test
	public void testAddListener() {
		listeners.add(listenerMock1);
		
		service.addListener(listenerMock2);
		service.addListener(listenerMock3);
		
		Set<IS3ReportListener> expected = new LinkedHashSet<>();
		expected.add(listenerMock1);
		expected.add(listenerMock2);
		expected.add(listenerMock3);
		assertEquals(expected, listeners);
	}
	
	@Test
	public void testRemoveListener() {
		listeners.add(listenerMock1);
		listeners.add(listenerMock2);
		listeners.add(listenerMock3);
		
		service.removeListener(listenerMock2);
		
		Set<IS3ReportListener> expected = new LinkedHashSet<>();
		expected.add(listenerMock1);
		expected.add(listenerMock3);
		assertEquals(expected, listeners);
	}

}
