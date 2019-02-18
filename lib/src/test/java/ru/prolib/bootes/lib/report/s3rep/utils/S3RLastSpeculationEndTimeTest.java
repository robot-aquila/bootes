package ru.prolib.bootes.lib.report.s3rep.utils;

import static org.junit.Assert.*;

import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.S3RRecord;

public class S3RLastSpeculationEndTimeTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private IS3Report reportMock;
	private S3RRecord recordMock;
	private S3RLastSpeculationEndTime service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		reportMock = control.createMock(IS3Report.class);
		recordMock = control.createMock(S3RRecord.class);
		service = new S3RLastSpeculationEndTime(reportMock);
	}
	
	@Test
	public void testGetTime_IfReportIsEmpty() {
		expect(reportMock.getRecordCount()).andReturn(0);
		control.replay();
		
		assertNull(service.getTime());
		
		control.verify();
	}
	
	@Test
	public void testGetTime_IfLastRecordIsNotClosed() {
		expect(reportMock.getRecordCount()).andReturn(5);
		expect(reportMock.getRecord(4)).andReturn(recordMock);
		expect(recordMock.getExitTime()).andReturn(null);
		control.replay();
		
		assertNull(service.getTime());
		
		control.verify();
	}
	
	@Test
	public void testGetTime_OK() {
		expect(reportMock.getRecordCount()).andReturn(5);
		expect(reportMock.getRecord(4)).andReturn(recordMock);
		expect(recordMock.getExitTime()).andReturn(T("2019-02-18T15:58:00Z"));
		control.replay();
		
		assertEquals(T("2019-02-18T15:58:00Z"), service.getTime());
		
		control.verify();
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new S3RLastSpeculationEndTime(reportMock)));
		assertFalse(service.equals(new S3RLastSpeculationEndTime(control.createMock(IS3Report.class))));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(622215, 9629)
				.append(reportMock)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

}
