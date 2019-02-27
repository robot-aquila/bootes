package ru.prolib.bootes.lib.report.s3rep.filter;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.report.s3rep.S3RRecord;

public class S3RShortDurationRecordsTest {
	private IMocksControl control;
	private S3RRecord recordMock;
	private S3RShortDurationRecords service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		recordMock = control.createMock(S3RRecord.class);
		service = new S3RShortDurationRecords("foo", 15);
	}
	
	@Test
	public void testCtor2() {
		assertEquals("foo", service.getID());
		assertEquals(15L, service.getDuration());
	}
	
	@Test
	public void testCtor1() {
		service = new S3RShortDurationRecords(10L);
		assertEquals("SHORT_DURATION", service.getID());
		assertEquals(10L, service.getDuration());
	}
	
	@Test
	public void testApprove_IfNotEnded() {
		expect(recordMock.getDurationMinutes()).andReturn(null);
		control.replay();
		
		assertTrue(service.approve(recordMock));
		
		control.verify();
	}
	
	@Test
	public void testApprove_IfGtDuration() {
		expect(recordMock.getDurationMinutes()).andReturn(16L);
		control.replay();
		
		assertFalse(service.approve(recordMock));
		
		control.verify();
	}
	
	@Test
	public void testApprove_IfEqDuration() {
		expect(recordMock.getDurationMinutes()).andReturn(15L);
		control.replay();
		
		assertTrue(service.approve(recordMock));
		
		control.verify();
	}

	@Test
	public void testApprove_IfLtDuration() {
		expect(recordMock.getDurationMinutes()).andReturn(3L);
		control.replay();
		
		assertTrue(service.approve(recordMock));
		
		control.verify();
	}

}
