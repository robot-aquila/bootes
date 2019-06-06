package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.ConfigException;

public class SchedulerConfigBuilderTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private BasicConfig basicConfig;
	private SchedulerConfigBuilder service;

	@Before
	public void setUp() throws Exception {
		service = new SchedulerConfigBuilder();
		basicConfig = new BasicConfigBuilder().build();
	}
	
	@Test
	public void testWithProbeAutoStart() throws Exception {
		SchedulerConfig actual = service.build(basicConfig);
		assertFalse(actual.isProbeAutoStart());
		
		assertSame(service, service.withProbeAutoStart(true));
		actual = service.build(basicConfig);
		assertTrue(actual.isProbeAutoStart());
		
		assertSame(service, service.withProbeAutoStart(false));
		actual = service.build(basicConfig);
		assertFalse(actual.isProbeAutoStart());
	}
	
	@Test
	public void testWithProbeInitialTime() throws Exception {
		SchedulerConfig actual = service.build(basicConfig);
		assertNull(actual.getProbeInitialTime());
		
		assertSame(service, service.withProbeInitialTime(T("2018-04-22T23:12:00Z")));
		actual = service.build(basicConfig);
		assertEquals(T("2018-04-22T23:12:00Z"), actual.getProbeInitialTime());
		
		assertSame(service, service.withProbeInitialTime(null));
		actual = service.build(basicConfig);
		assertNull(actual.getProbeInitialTime());
	}
	
	@Test
	public void testWithProbeStopTime() throws Exception {
		SchedulerConfig actual = service.build(basicConfig);
		assertNull(actual.getProbeStopTime());
		
		assertSame(service, service.withProbeStopTime(T("1996-01-13T15:45:10Z")));
		actual = service.build(basicConfig);
		assertEquals(T("1996-01-13T15:45:10Z"), actual.getProbeStopTime());
		
		assertSame(service, service.withProbeStopTime(null));
		actual = service.build(basicConfig);
		assertNull(actual.getProbeStopTime());
	}
	
	@Test
	public void testWithProbeAutoShutdown() throws Exception {
		SchedulerConfig actual = service.build(basicConfig);
		assertFalse(actual.isProbeAutoShutdown());
		
		service.withProbeStopTime(T("1998-08-12T00:00:00Z"));
		
		assertSame(service, service.withProbeAutoShutdown(true));
		actual = service.build(basicConfig);
		assertTrue(actual.isProbeAutoShutdown());
		
		assertSame(service, service.withProbeAutoShutdown(false));
		actual = service.build(basicConfig);
		assertFalse(actual.isProbeAutoShutdown());
	}
	
	@Test
	public void testBuild_ThrowsIfAutoShutdownButStopTimeIsUndefined() throws Exception {
		try {
			service.withProbeAutoShutdown(true)
				.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("PROBE stop time is required for PROBE auto shutdown", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfHeadlessButAutoShutdownDisabled() throws Exception {
		basicConfig = new BasicConfigBuilder()
			.withHeadless(true)
			.build();
		try {
			service.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("PROBE auto shutdown must be enabled in headless mode", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfStopTimeIsLessThanInitialTime() throws Exception {
		try {
			service.withProbeInitialTime(T("2018-04-22T00:00:00Z"))
				.withProbeStopTime(T("2018-01-01T00:00:00Z"))
				.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("PROBE stop time must be greater than initial time", e.getMessage());
		}
	}

	@Test
	public void testBuild_ThrowsIfStopTimeIsEqualToInitialTime() throws Exception {
		try {
			service.withProbeInitialTime(T("2018-04-22T00:00:00Z"))
				.withProbeStopTime(T("2018-04-22T00:00:00Z"))
				.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("PROBE stop time must be greater than initial time", e.getMessage());
		}
	}

	@Test
	public void testBuild_ThrowsIfInitialTimeIsNotSpecifiedAndStopTimeIsLessThanEpochTime() {
		try {
			service.withProbeStopTime(Instant.EPOCH.minusMillis(1))
				.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("PROBE stop time must be greater than epoch start time", e.getMessage());
		}
	}
	
	@Test
	public void testBuild_ThrowsIfInitialTimeIsNotSpecifiedAndStopTimeIsEqualToEpochTime() {
		try {
			service.withProbeStopTime(Instant.EPOCH)
				.build(basicConfig);
			fail("Expected exception");
		} catch ( ConfigException e ) {
			assertEquals("PROBE stop time must be greater than epoch start time", e.getMessage());
		}
	}

}
