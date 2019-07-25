package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;

public class SchedulerConfig2SectionTest {
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private SchedulerConfig2Section service;

	@Before
	public void setUp() throws Exception {
		service = new SchedulerConfig2Section();
	}
	
	@Test
	public void testConfigureDefaults() throws Exception {
		Map<String, String> defaults_data = new HashMap<>(), options_data = new HashMap<>();
		KVStoreHash defaults = new KVStoreHash(defaults_data);
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));

		service.configureDefaults(defaults, options);
		
		Map<String, String> expected = new HashMap<>();
		assertEquals(expected, defaults_data);
	}
	
	@Test
	public void testConfigureOptions() throws Exception {
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

	@Test
	public void testConfigure() throws Exception {
		Map<String, String> options_data = new HashMap<>();
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));

		Object actual = service.configure(options);

		SchedulerConfig2 expected = new SchedulerConfig2(options);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConfigure_ThrowsIfAutoShutdownButStopTimeIsUndefined() throws Exception {
		Map<String, String> options_data = new HashMap<>();
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		options_data.put("probe-auto-shutdown", "1");
		eex.expect(ConfigException.class);
		eex.expectMessage("PROBE stop time is required for PROBE auto shutdown");
		
		service.configure(options);
	}
	
	@Test
	public void testConfigure_ThrowsIfHeadlessButAutoShutdownDisabled() throws Exception {
		Map<String, String> options_data = new HashMap<>();
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		options_data.put("headless", "1");
		eex.expect(ConfigException.class);
		eex.expectMessage("PROBE auto shutdown must be enabled in headless mode");
		
		service.configure(options);
	}

	@Test
	public void testConfigure_ThrowsIfStopTimeIsLessThanInitialTime() throws Exception {
		Map<String, String> options_data = new HashMap<>();
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		options_data.put("probe-initial-time", "2019-07-25T22:25:00Z");
		options_data.put("probe-stop-time", "2019-07-25T00:00:00Z");
		eex.expect(ConfigException.class);
		eex.expectMessage("PROBE stop time must be greater than initial time");
		
		service.configure(options);
	}

	@Test
	public void testConfigure_ThrowsIfStopTimeIsEqualToInitialTime() throws Exception {
		Map<String, String> options_data = new HashMap<>();
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		options_data.put("probe-initial-time", "2019-07-25T22:25:00Z");
		options_data.put("probe-stop-time", "2019-07-25T22:25:00Z");
		eex.expect(ConfigException.class);
		eex.expectMessage("PROBE stop time must be greater than initial time");
		
		service.configure(options);
	}

	@Test
	public void testConfigure_ThrowsIfInitialTimeIsNotSpecifiedAndStopTimeIsLessThanEpochTime() throws Exception {
		Map<String, String> options_data = new HashMap<>();
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		options_data.put("probe-stop-time", "1968-07-25T22:25:00Z");
		eex.expect(ConfigException.class);
		eex.expectMessage("PROBE stop time must be greater than epoch start time");
		
		service.configure(options);
	}
	
	@Test
	public void testConfigure_ThrowsIfInitialTimeIsNotSpecifiedAndStopTimeIsEqualToEpochTime() throws Exception {
		Map<String, String> options_data = new HashMap<>();
		OptionProvider options = new OptionProviderKvs(new KVStoreHash(options_data));
		options_data.put("probe-stop-time", "1970-01-01T00:00:00Z");
		eex.expect(ConfigException.class);
		eex.expectMessage("PROBE stop time must be greater than epoch start time");
		
		service.configure(options);
	}
	
}
