package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.config.kvstore.KVStoreHash;

public class SchedulerConfigLoaderTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private Map<String, String> data;
	private OptionProvider op, opMock;
	private BasicConfig basicConfig;
	private SchedulerConfigBuilder builder;
	private SchedulerConfigLoader service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		data = new HashMap<>();
		op = new OptionProviderKvs(new KVStoreHash(data));
		opMock = control.createMock(OptionProvider.class);
		basicConfig = new BasicConfigBuilder().build();
		builder = new SchedulerConfigBuilder();
		service = new SchedulerConfigLoader();
	}
	
	@Test
	public void testLoad_OnRealData() throws Exception {
		data.put("probe-auto-start", "1");
		data.put("probe-auto-shutdown", "1");
		data.put("probe-initial-time", "2018-04-25T00:00:00Z");
		data.put("probe-stop-time", "2018-04-25T10:00:00Z");
		
		service.load(builder, op, basicConfig);
		
		SchedulerConfig actual = builder.build(basicConfig);
		SchedulerConfig expected = new SchedulerConfigBuilder()
			.withProbeAutoStart(true)
			.withProbeAutoShutdown(true)
			.withProbeInitialTime(T("2018-04-25T00:00:00Z"))
			.withProbeStopTime(T("2018-04-25T10:00:00Z"))
			.build(basicConfig);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoad_OnMocks() throws Exception {
		expect(opMock.getBoolean("probe-auto-start", false)).andReturn(true);
		expect(opMock.getBoolean("probe-auto-shutdown", false)).andReturn(false);
		expect(opMock.getInstant("probe-initial-time")).andReturn(T("2018-04-15T00:00:00Z"));
		expect(opMock.getInstant("probe-stop-time")).andReturn(null);
		control.replay();

		service.load(builder, opMock, basicConfig);
		
		control.verify();
		SchedulerConfig actual = builder.build(basicConfig);
		SchedulerConfig expected = new SchedulerConfigBuilder()
			.withProbeAutoStart(true)
			.withProbeAutoShutdown(false)
			.withProbeInitialTime(T("2018-04-15T00:00:00Z"))
			.withProbeStopTime(null)
			.build(basicConfig);
		assertEquals(expected, actual);
	}

	@Test
	public void testConfigureOptions() {
		Options options = new Options();
		
		service.configureOptions(options);

		assertEquals(4, options.getOptions().size());
		
		Option actual = options.getOption("probe-auto-start");
		assertEquals(Option.builder()
				.longOpt("probe-auto-start")
				.desc("If specified then PROBE scheduler will be started immediately after program initialization.")
				.build(), actual);
		assertFalse(actual.hasArg());
		
		actual = options.getOption("probe-auto-shutdown");
		assertEquals(Option.builder()
				.longOpt("probe-auto-shutdown")
				.desc("Shutdown program automatically when PROBE scheduler reach the stop time. "
					+ "This option requires --probe-stop-time option specified.")
				.build(), actual);
		assertFalse(actual.hasArg());
		
		actual = options.getOption("probe-initial-time");
		assertEquals(Option.builder()
				.longOpt("probe-initial-time")
				.hasArg()
				.argName("time")
				.desc("Specify start time of PROBE scheduler. If omitted then default PROBE time will be used.")
				.build(), actual);
		assertTrue(actual.hasArg());
		
		actual = options.getOption("probe-stop-time");
		assertEquals(Option.builder()
				.longOpt("probe-stop-time")
				.hasArg()
				.desc("Specify time to stop simulation by stopping PROBE scheduler.")
				.build(), actual);
		assertTrue(actual.hasArg());
	}

}
