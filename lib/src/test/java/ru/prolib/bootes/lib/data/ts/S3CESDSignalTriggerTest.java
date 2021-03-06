package ru.prolib.bootes.lib.data.ts;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.bootes.lib.data.ts.S3CESDSignalTrigger.ObjectLocatorStub;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.bootes.lib.data.ts.SignalType;

public class S3CESDSignalTriggerTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TSeriesImpl<CDecimal> source;
	private S3CESDSignalTrigger service;

	@Before
	public void setUp() throws Exception {
		source = new TSeriesImpl<>(ZTFrame.M5);
		service = new S3CESDSignalTrigger(new ObjectLocatorStub(source));
	}
	
	@Test
	public void testGetSignal_SkipIfIndexNotFound() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.23"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.92"));
		source.set(T("2019-01-04T03:10:00Z"), of("15.01"));
		
		assertNull(service.getSignal(T("2019-01-01T00:00:00Z")));
	}
	
	@Test
	public void testGetSignal_SkipIf0Elements() {
		assertNull(service.getSignal(T("2019-01-04T03:15:00Z")));
	}
	
	@Test
	public void testGetSignal_SkipIf1Elements() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		
		assertNull(service.getSignal(T("2019-01-04T02:55:00Z")));
	}
	
	@Test
	public void testGetSignal_SkipIf2Elements() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.23"));
		
		assertNull(service.getSignal(T("2019-01-04T03:00:00Z")));
	}
	
	@Test
	public void testGetSignal_SkipIfNullV1() {
		source.set(T("2019-01-04T02:55:00Z"), null);
		source.set(T("2019-01-04T03:00:00Z"), of("14.23"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.92"));
		
		assertNull(service.getSignal(T("2019-01-04T03:10:00Z")));
	}
	
	@Test
	public void testGetSignal_SkipIfNullV2() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), null);
		source.set(T("2019-01-04T03:05:00Z"), of("14.92"));
		
		assertNull(service.getSignal(T("2019-01-04T03:10:00Z")));
	}
	
	@Test
	public void testGetSignal_SkipIfNullV3() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.23"));
		source.set(T("2019-01-04T03:05:00Z"), null);
		
		assertNull(service.getSignal(T("2019-01-04T03:10:00Z")));
	}
	
	@Test
	public void testGetSignal_Up() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.23"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.92"));
		source.set(T("2019-01-04T03:10:00Z"), of("13.99")); // consider incomplete element
		
		TSignal
		expected = new TSignal(T("2019-01-04T03:10:00Z"), 2, SignalType.BUY, of("14.92"));
		assertEquals(expected, service.getSignal(T("2019-01-04T03:10:00Z")));
		
		expected = new TSignal(T("2019-01-04T03:13:29Z"), 2, SignalType.BUY, of("14.92"));
		assertEquals(expected, service.getSignal(T("2019-01-04T03:13:29Z")));
	}
	
	@Test
	public void testGetSignal_Up_SkipIfV2LtV1() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.07"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.92"));
		source.set(T("2019-01-04T03:10:00Z"), of("13.99"));
		
		assertNull(service.getSignal(T("2019-01-04T03:10:00Z")));
	}

	@Test
	public void testGetSignal_Up_SkipIfV3LtV2() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.23"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.22"));
		source.set(T("2019-01-04T03:10:00Z"), of("13.99"));
		
		assertNull(service.getSignal(T("2019-01-04T03:10:00Z")));
	}

	@Test
	public void testGetSignal_Down() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.09"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.01"));
		source.set(T("2019-01-04T03:10:00Z"), of("15.34"));
		
		TSignal
		expected = new TSignal(T("2019-01-04T03:10:00Z"), 2, SignalType.SELL, of("14.01"));
		assertEquals(expected, service.getSignal(T("2019-01-04T03:10:00Z")));
	}
	
	@Test
	public void testGetSignal_Down_SkipIfV2GtV1() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.13"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.01"));
		source.set(T("2019-01-04T03:10:00Z"), of("15.34"));
		
		assertNull(service.getSignal(T("2019-01-04T03:10:00Z")));
	}
	
	@Test
	public void testGetSignal_Down_SkipIfV3GtV2() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.09"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.54"));
		source.set(T("2019-01-04T03:10:00Z"), of("15.34"));
		
		assertNull(service.getSignal(T("2019-01-04T03:10:00Z")));
	}
	
	@Test
	public void testGetSignal_IfTimeNotFoundButHasEntriesBeforeThatTime() {
		source.set(T("2019-01-04T02:55:00Z"), of("14.12"));
		source.set(T("2019-01-04T03:00:00Z"), of("14.09"));
		source.set(T("2019-01-04T03:05:00Z"), of("14.01"));
		source.set(T("2019-01-04T03:10:00Z"), of("15.34"));

		assertNull(service.getSignal(T("2019-01-05T00:00:00Z")));
	}

}
