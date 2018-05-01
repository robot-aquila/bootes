package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.bootes.lib.config.SchedulerConfig;

public class SchedulerConfigTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private SchedulerConfig service;

	@Before
	public void setUp() throws Exception {
		service = new SchedulerConfig(true, false, T("2018-04-22T00:00:00Z"), T("2018-04-22T00:00:10Z"));
	}
	
	@Test
	public void testCtor() {
		assertTrue(service.isProbeAutoStart());
		assertFalse(service.isProbeAutoShutdown());
		assertEquals(T("2018-04-22T00:00:00Z"), service.getProbeInitialTime());
		assertEquals(T("2018-04-22T00:00:10Z"), service.getProbeStopTime());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Boolean> vAutoStart = new Variant<Boolean>(true, false);
		Variant<Boolean> vAutoShtdn = new Variant<Boolean>(vAutoStart, false, true);
		Variant<Instant> vInitTime = new Variant<Instant>(vAutoShtdn, T("2018-04-22T00:00:00Z"), T("2010-01-01T00:00:00Z"));
		Variant<Instant> vStopTime = new Variant<Instant>(vInitTime, T("2018-04-22T00:00:10Z"), T("2010-05-01T00:00:00Z"));
		Variant<?> iterator = vStopTime;
		int foundCnt = 0;
		SchedulerConfig x, found = null;
		do {
			x = new SchedulerConfig(vAutoStart.get(), vAutoShtdn.get(), vInitTime.get(), vStopTime.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertTrue(found.isProbeAutoStart());
		assertFalse(found.isProbeAutoShutdown());
		assertEquals(T("2018-04-22T00:00:00Z"), found.getProbeInitialTime());
		assertEquals(T("2018-04-22T00:00:10Z"), found.getProbeStopTime());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(3491905, 85)
			.append(true)
			.append(false)
			.append(T("2018-04-22T00:00:00Z"))
			.append(T("2018-04-22T00:00:10Z"))
			.toHashCode();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String expected = "SchedulerConfig[probeAutoStart=true,probeAutoShutdown=false,"
				+ "probeInitialTime=2018-04-22T00:00:00Z,probeStopTime=2018-04-22T00:00:10Z]";
		
		assertEquals(expected, service.toString());
	}

}
