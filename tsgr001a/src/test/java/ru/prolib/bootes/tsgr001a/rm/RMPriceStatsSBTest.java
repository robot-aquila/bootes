package ru.prolib.bootes.tsgr001a.rm;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class RMPriceStatsSBTest {
	private static ZoneId ZONE = ZoneId.of("Europe/Moscow");
	
	static Instant ZT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(ZONE).toInstant();
	}
	
	private TSeriesImpl<CDecimal> daily, local;
	private RMPriceStatsSB service;

	@Before
	public void setUp() throws Exception {
		daily = new TSeriesImpl<>(ZTFrame.D1MSK);
		local = new TSeriesImpl<>(ZTFrame.M5MSK);
		service = new RMPriceStatsSB();
		service.setDailyMoveSeries(daily);
		service.setLocalMoveSeries(local);
	}
	
	@Test
	public void testSettersAndGetters() {
		assertSame(daily, service.getDailyMoveSeries());
		assertSame(local, service.getLocalMoveSeries());
	}
	
	@Test
	public void testGetDailyPriceMove() {
		assertNull(service.getDailyPriceMove(ZT("2018-12-15T15:35:20")));
		
		daily.set(ZT("2018-12-15T00:00:00"), of("534.902"));

		assertNull(service.getDailyPriceMove(ZT("2018-12-15T15:35:20")));
		assertNull(service.getDailyPriceMove(ZT("2018-12-14T15:35:20")));
		assertEquals(of("534.902"), service.getDailyPriceMove(ZT("2018-12-16T15:35:20")));
		
		daily.set(ZT("2018-12-16T00:00:00"), of("603.015"));
		daily.set(ZT("2018-12-17T00:00:00"), of("505.212"));
		
		assertNull(service.getDailyPriceMove(ZT("2018-12-14T02:15:47")));
		assertNull(service.getDailyPriceMove(ZT("2018-12-15T03:20:45")));
		assertEquals(of("534.902"), service.getDailyPriceMove(ZT("2018-12-16T08:50:45")));
		assertEquals(of("603.015"), service.getDailyPriceMove(ZT("2018-12-17T15:47:19")));
		assertEquals(of("505.212"), service.getDailyPriceMove(ZT("2018-12-18T00:00:00")));
		assertEquals(of("505.212"), service.getDailyPriceMove(ZT("2047-01-01T00:00:00")));
	}

	@Test
	public void testGetLocalPriceMove() {
		assertNull(service.getLocalPriceMove(ZT("2018-12-15T22:43:36")));
		
		local.set(ZT("2018-12-15T22:43:36"), of("42.924"));
		
		assertNull(service.getLocalPriceMove(ZT("2018-12-15T22:00:00")));
		assertNull(service.getLocalPriceMove(ZT("2018-12-15T22:40:00")));
		assertNull(service.getLocalPriceMove(ZT("2018-12-15T22:43:36")));
		assertEquals(of("42.924"), service.getLocalPriceMove(ZT("2018-12-15T22:45:00")));
		assertEquals(of("42.924"), service.getLocalPriceMove(ZT("2018-12-15T23:12:27")));
		
		local.set(ZT("2018-12-15T22:45:00"), of("49.360"));
		local.set(ZT("2018-12-15T23:00:00"), of("51.014"));
		
		assertNull(service.getLocalPriceMove(ZT("2018-01-01T00:00:00")));
		assertNull(service.getLocalPriceMove(ZT("2018-12-15T21:14:46")));
		assertNull(service.getLocalPriceMove(ZT("2018-12-15T22:40:00")));
		assertEquals(of("42.924"), service.getLocalPriceMove(ZT("2018-12-15T22:45:00")));
		assertEquals(of("42.924"), service.getLocalPriceMove(ZT("2018-12-15T22:47:41")));
		assertEquals(of("49.360"), service.getLocalPriceMove(ZT("2018-12-15T22:51:04")));
		assertEquals(of("49.360"), service.getLocalPriceMove(ZT("2018-12-15T23:00:00")));
		assertEquals(of("49.360"), service.getLocalPriceMove(ZT("2018-12-15T23:00:19")));
		assertEquals(of("51.014"), service.getLocalPriceMove(ZT("2018-12-15T23:07:38")));
		assertEquals(of("51.014"), service.getLocalPriceMove(ZT("2018-12-15T23:59:59")));
		assertEquals(of("51.014"), service.getLocalPriceMove(ZT("2050-01-01T00:00:00")));
	}

}
