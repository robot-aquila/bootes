package ru.prolib.bootes.tsgr001a.robot.filter;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalType;

public class SignalTimetableTest {
	private static ZoneId ZONE_ID;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ZONE_ID = ZoneId.of("Europe/Moscow");
	}
	
	static Instant ZDT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(ZONE_ID).toInstant();
	}
	
	static S3TradeSignal ofTime(String timeString) {
		return new S3TradeSignal(
				SignalType.BUY,
				ZDT(timeString),
				of("112.04"),
				of("1000"),
				of("15.00"),
				of("3.00"),
				of("0.5")
			);
	}
	
	private SignalTimetable service;

	@Before
	public void setUp() throws Exception {
		service = new SignalTimetable(ZONE_ID);
	}

	@Test
	public void testApprove() {
		assertFalse(service.approve(ofTime("2019-03-07T08:15:34")));
		assertFalse(service.approve(ofTime("2019-03-07T09:00:00")));
		assertFalse(service.approve(ofTime("2019-03-07T10:00:00")));
		assertFalse(service.approve(ofTime("2019-03-07T10:29:59.999")));
		assertTrue(service.approve(ofTime("2019-03-07T10:30:00")));
		assertTrue(service.approve(ofTime("2019-03-07T12:15:31")));
	}

}
