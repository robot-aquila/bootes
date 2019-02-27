package ru.prolib.bootes.lib.report.s3rep.filter;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.LocalTimePeriod;
import ru.prolib.bootes.lib.report.s3rep.S3RRecord;

public class S3RCrossingIntradayPeriodTest {
	static ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static Instant TZ(String timeString) {
		return LocalDateTime.parse(timeString).atZone(ZONE_ID).toInstant();
	}
	
	private IMocksControl control;
	private S3RRecord recordMock;
	private LocalTimePeriod period;
	private S3RCrossingIntradayPeriod service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		recordMock = control.createMock(S3RRecord.class);
		period = new LocalTimePeriod(LocalTime.of(10,  0), LocalTime.of(11,  0), ZONE_ID);
		service = new S3RCrossingIntradayPeriod("bar", period);
	}
	
	@Test
	public void testCtor2() {
		assertEquals("bar", service.getID());
		assertEquals(period, service.getPeriod());
	}
	
	@Test
	public void testCtor1() {
		service = new S3RCrossingIntradayPeriod(period);
		assertEquals("CROSS_INTRADAY_PERIOD", service.getID());
		assertEquals(period, service.getPeriod());
	}
	
	@Test
	public void testApprove_IfNotEnded() {
		expect(recordMock.getEntryTime()).andReturn(TZ("2019-02-27T10:15:00"));
		expect(recordMock.getExitTime()).andReturn(null);
		control.replay();
		
		assertTrue(service.approve(recordMock));
		
		control.verify();
	}
	
	@Test
	public void testApprove_IfOverlappedWithStart() {
		expect(recordMock.getEntryTime()).andReturn(TZ("2019-02-27T09:30:00"));
		expect(recordMock.getExitTime()).andReturn(TZ("2019-02-27T10:30:00"));
		control.replay();
		
		assertTrue(service.approve(recordMock));
		
		control.verify();
	}
	
	@Test
	public void testApprove_IfOverlappedWithEnd() {
		expect(recordMock.getEntryTime()).andReturn(TZ("2019-02-27T10:45:00"));
		expect(recordMock.getExitTime()).andReturn(TZ("2019-02-28T00:15:26"));
		control.replay();
		
		assertTrue(service.approve(recordMock));
		
		control.verify();
	}
	
	@Test
	public void testApprove_IfWholeOverlapped() {
		expect(recordMock.getEntryTime()).andReturn(TZ("2019-02-15T05:15:45"));
		expect(recordMock.getExitTime()).andReturn(TZ("2019-03-01T12:48:19"));
		control.replay();
		
		assertTrue(service.approve(recordMock));
		
		control.verify();
	}

	@Test
	public void testApprove_IfNotOverlapped() {
		expect(recordMock.getEntryTime()).andReturn(TZ("2019-02-27T11:15:00"));
		expect(recordMock.getExitTime()).andReturn(TZ("2019-02-28T08:13:46"));
		control.replay();
		
		assertFalse(service.approve(recordMock));
		
		control.verify();
	}

}
