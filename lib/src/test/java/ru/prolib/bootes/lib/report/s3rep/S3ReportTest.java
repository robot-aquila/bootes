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

import ru.prolib.bootes.lib.data.ts.filter.IFilter;

public class S3ReportTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private IS3ReportListener listenerMock1, listenerMock2, listenerMock3;
	private IFilter<S3RRecord> filterMock;
	private List<S3RRecord> records;
	private Set<IS3ReportListener> listeners;
	private S3Report serviceWoFilter, serviceWithFilter;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		listenerMock1 = control.createMock(IS3ReportListener.class);
		listenerMock2 = control.createMock(IS3ReportListener.class);
		listenerMock3 = control.createMock(IS3ReportListener.class);
		filterMock = control.createMock(IFilter.class);
		records = new ArrayList<>();
		listeners = new LinkedHashSet<>();
		serviceWoFilter = new S3Report(records, listeners);
		serviceWithFilter = new S3Report(filterMock, records, listeners);
	}
	
	@Test
	public void testCtor3() {
		assertSame(filterMock, serviceWithFilter.getFilter());
	}
	
	@Test
	public void testCtor2() {
		serviceWoFilter = new S3Report(records, listeners);
		assertNull(serviceWoFilter.getFilter());
	}
	
	@Test
	public void testCtor1() {
		serviceWithFilter = new S3Report(filterMock);
		assertSame(filterMock, serviceWithFilter.getFilter());
	}
	
	@Test
	public void testCtor0() {
		serviceWoFilter = new S3Report();
		assertNull(serviceWoFilter.getFilter());
	}
	
	@Test
	public void testGetRecordCount() {
		assertEquals(0, serviceWoFilter.getRecordCount());
		
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		
		assertEquals(3, serviceWoFilter.getRecordCount());
		
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		
		assertEquals(6, serviceWoFilter.getRecordCount());
	}
	
	@Test
	public void testGetRecord() {
		S3RRecord recMock1, recMock2, recMock3;
		records.add(recMock1 = control.createMock(S3RRecord.class));
		records.add(recMock2 = control.createMock(S3RRecord.class));
		records.add(recMock3 = control.createMock(S3RRecord.class));
		
		assertSame(recMock1, serviceWoFilter.getRecord(0));
		assertSame(recMock2, serviceWoFilter.getRecord(1));
		assertSame(recMock3, serviceWoFilter.getRecord(2));
	}
	
	@Test (expected=IndexOutOfBoundsException.class)
	public void testGetRecord_ThrowsIfOutOfBounds() {
		serviceWoFilter.getRecord(0);
	}
	
	@Test
	public void testUpdate_Last_WoFilter() {
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		serviceWoFilter.create(new S3RRecordCreate(
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00")
			));
		S3RRecord expected = new S3RRecord(
				2,
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
		serviceWoFilter.addListener(listenerMock1);
		serviceWoFilter.addListener(listenerMock2);
		
		S3RRecord actual = serviceWoFilter.update(new S3RRecordUpdateLast(
				T("2019-02-12T12:45:00Z"),
				of("199.98"),
				ofRUB5("-28.34")
			));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testUpdate_Last_ThrowsIfNotExists() {
		serviceWoFilter.update(new S3RRecordUpdateLast(
				T("2019-02-12T12:45:00Z"),
				of("199.98"),
				ofRUB5("-28.34")
			));
	}
	
	@Test
	public void testUpdate_Last_WithFilter_Approved_LastApproved() {
		S3RRecord recordMock1, recordMock2;
		records.add(recordMock1 = control.createMock(S3RRecord.class));
		records.add(recordMock2 = control.createMock(S3RRecord.class));
		S3RRecord expected1 = new S3RRecord(
				2,
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00")
			);
		expect(filterMock.approve(expected1)).andReturn(true);
		control.replay();
		serviceWithFilter.create(new S3RRecordCreate(
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00")
			));
		control.resetToStrict();
		assertEquals(expected1, serviceWithFilter.getRecord(2));
		S3RRecord expected2 = new S3RRecord(
				2,
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
		expect(filterMock.approve(expected2)).andReturn(true);
		listenerMock1.recordUpdated(expected2);
		control.replay();
		serviceWithFilter.addListener(listenerMock1);
		
		S3RRecord actual = serviceWithFilter.update(new S3RRecordUpdateLast(
				T("2019-02-12T12:45:00Z"),
				of("199.98"),
				ofRUB5("-28.34")
			));

		control.verify();
		assertEquals(expected2, actual);
		List<S3RRecord> expected_records = new ArrayList<>();
		expected_records.add(recordMock1);
		expected_records.add(recordMock2);
		expected_records.add(expected2);
		assertEquals(expected_records, records);
	}
	
	@Test
	public void testUpdate_Last_WithFilter_Declined_LastApproved() {
		S3RRecord recordMock1, recordMock2;
		records.add(recordMock1 = control.createMock(S3RRecord.class));
		records.add(recordMock2 = control.createMock(S3RRecord.class));
		S3RRecord expected1 = new S3RRecord(
				2,
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00")
			);
		expect(filterMock.approve(expected1)).andReturn(true);
		control.replay();
		serviceWithFilter.create(new S3RRecordCreate(
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00")
			));
		control.resetToStrict();
		assertEquals(expected1, serviceWithFilter.getRecord(2));
		S3RRecord expected2 = new S3RRecord(
				2,
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
		expect(filterMock.approve(expected2)).andReturn(false);
		listenerMock1.recordDeleted(expected2);
		control.replay();
		serviceWithFilter.addListener(listenerMock1);
		
		S3RRecord actual = serviceWithFilter.update(new S3RRecordUpdateLast(
				T("2019-02-12T12:45:00Z"),
				of("199.98"),
				ofRUB5("-28.34")
			));

		control.verify();
		assertNull(actual);
		List<S3RRecord> expected_records = new ArrayList<>();
		expected_records.add(recordMock1);
		expected_records.add(recordMock2);
		assertEquals(expected_records, records);
	}
	
	@Test
	public void testUpdate_Last_WithFilter_Skipped_LastDeclined() {
		S3RRecord recordMock1, recordMock2;
		records.add(recordMock1 = control.createMock(S3RRecord.class));
		records.add(recordMock2 = control.createMock(S3RRecord.class));
		S3RRecord expected1 = new S3RRecord(
				2,
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00")
			);
		expect(filterMock.approve(expected1)).andReturn(false);
		control.replay();
		assertNull(serviceWithFilter.create(new S3RRecordCreate(
				S3RType.SHORT,
				T("2019-02-12T12:26:00Z"),
				of("192.13"),
				of("1000"),
				of("150.23"),
				of("199.96"),
				of("190.00")
			)));
		control.resetToStrict();
		control.replay();
		serviceWithFilter.addListener(listenerMock1);
		
		S3RRecord actual = serviceWithFilter.update(new S3RRecordUpdateLast(
				T("2019-02-12T12:45:00Z"),
				of("199.98"),
				ofRUB5("-28.34")
			));
		
		control.verify();
		assertNull(actual);
		List<S3RRecord> expected_records = new ArrayList<>();
		expected_records.add(recordMock1);
		expected_records.add(recordMock2);
		assertEquals(expected_records, records);
	}
	
	@Test
	public void testCreate_WoFilter() {
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		S3RRecord expected = new S3RRecord(
				2,
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
		serviceWoFilter.addListener(listenerMock3);
		serviceWoFilter.addListener(listenerMock1);
		
		S3RRecord actual = serviceWoFilter.create(new S3RRecordCreate(
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
	public void testCreate_WithFilter_Approved() {
		records.add(control.createMock(S3RRecord.class));
		records.add(control.createMock(S3RRecord.class));
		S3RRecord expected = new S3RRecord(
				2,
				S3RType.LONG,
				T("2019-02-12T15:13:00Z"),
				of("527.12"),
				of("100"),
				of("635.00"),
				of("520.00"),
				of("531.19")
			);
		expect(filterMock.approve(expected)).andReturn(true);
		listenerMock3.recordCreated(expected);
		listenerMock1.recordCreated(expected);
		control.replay();
		serviceWithFilter.addListener(listenerMock3);
		serviceWithFilter.addListener(listenerMock1);
		
		S3RRecord actual = serviceWithFilter.create(new S3RRecordCreate(
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
	public void testCreate_WithFilter_Declined() {
		S3RRecord recordMock1, recordMock2;
		records.add(recordMock1 = control.createMock(S3RRecord.class));
		records.add(recordMock2 = control.createMock(S3RRecord.class));
		S3RRecord expected = new S3RRecord(
				2,
				S3RType.LONG,
				T("2019-02-12T15:13:00Z"),
				of("527.12"),
				of("100"),
				of("635.00"),
				of("520.00"),
				of("531.19")
			);
		expect(filterMock.approve(expected)).andReturn(false);
		control.replay();
		serviceWithFilter.addListener(listenerMock3);
		serviceWithFilter.addListener(listenerMock1);
		
		S3RRecord actual = serviceWithFilter.create(new S3RRecordCreate(
				S3RType.LONG,
				T("2019-02-12T15:13:00Z"),
				of("527.12"),
				of("100"),
				of("635.00"),
				of("520.00"),
				of("531.19")
			));
		
		control.verify();
		assertNull(actual);
		assertEquals(2, records.size());
		List<S3RRecord> expected_records = new ArrayList<>();
		expected_records.add(recordMock1);
		expected_records.add(recordMock2);
		assertEquals(expected_records, records);
	}
	
	@Test
	public void testAddListener() {
		listeners.add(listenerMock1);
		
		serviceWoFilter.addListener(listenerMock2);
		serviceWoFilter.addListener(listenerMock3);
		
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
		
		serviceWoFilter.removeListener(listenerMock2);
		
		Set<IS3ReportListener> expected = new LinkedHashSet<>();
		expected.add(listenerMock1);
		expected.add(listenerMock3);
		assertEquals(expected, listeners);
	}

}
