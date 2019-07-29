package ru.prolib.bootes.lib.service.ars;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppConfigService2;

public class AppRuntimeServiceImplTest {
	
	static class TestHandler implements AppComponent {
		private final String id;
		private final List<String> log;
		private final boolean throwAtStartup, throwAtShutdown;
		
		TestHandler(String id, List<String> log, boolean throwAtStartup, boolean throwAtShutdown) {
			this.id = id;
			this.log = log;
			this.throwAtStartup = throwAtStartup;
			this.throwAtShutdown = throwAtShutdown;
		}
		
		TestHandler(String id, List<String> log) {
			this(id, log, false, false);
		}

		@Override
		public void startup() throws Throwable {
			log.add("+" + id);
			if ( throwAtStartup ) {
				throw new Exception("Test startup error: " + id);
			}
		}

		@Override
		public void shutdown() throws Throwable {
			log.add("-" + id);
			if ( throwAtShutdown ) {
				throw new Exception("Test shutdown error: " + id);
			}
		}

		@Override
		public void init() throws Throwable {
			log.add("!" + id);
		}

		@Override
		public void registerConfig(AppConfigService2 config_service) {
			
		}
		
	}

	private AppRuntimeServiceImpl service;
	private List<String> log;
	private TestHandler h1, h2, h3, h4, h5, h6;
	
	@Before
	public void setUp() throws Exception {
		service = new AppRuntimeServiceImpl("ARS");
		log = new ArrayList<>();
		h1 = new TestHandler("h1", log);
		h2 = new TestHandler("h2", log);
		h3 = new TestHandler("h3", log);
		h4 = new TestHandler("h4", log);
		h5 = new TestHandler("h5", log);
		h6 = new TestHandler("h6", log);
	}
	
	@After
	public void tearDown() throws Exception {
		if ( service != null ) {
			service.shutdown();
		}
	}
	
	@Test (expected=IllegalStateException.class)
	public void testAddService_ThrowsIfStarted() throws Exception {
		service.startup();
		
		service.addService(h1);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testAddApplication_ThrowsIfStarted() throws Exception {
		service.startup();
		
		service.addApplication(h1);
	}
	
	@Test
	public void testShutdown_DoNotRunHandlersIfCalledPriorToStartup() throws Exception {
		service.addService(h1);
		service.addApplication(h2);
		
		service.shutdown();
		
		assertTrue(service.waitForShutdown(1000));
		assertEquals(new ArrayList<>(), log);
	}
	
	@Test
	public void testShutdown_MarksAsStartedAndUnlocksWaiters_NoHandlers() throws Exception {
		CountDownLatch finished = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				try {
					if ( service.waitForStartup(1000L) ) {
						finished.countDown();
					}
				} catch ( InterruptedException e ) { }
			}
		}.start();
		
		service.shutdown();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertTrue(service.waitForShutdown(1000));
		assertEquals(new ArrayList<>(), log);
	}
	
	@Test
	public void testShutdown_MarksAsStartedAndUnlockWaiters_WithHandlers() throws Exception {
		service.addService(h1);
		service.addApplication(h2);
		CountDownLatch finished = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				try {
					if ( service.waitForStartup(1000L) ) {
						finished.countDown();
					}
				} catch ( InterruptedException e ) { }
			}
		}.start();
		
		service.shutdown();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertTrue(service.waitForShutdown(1000));
		assertEquals(new ArrayList<>(), log);
	}
	
	@Test
	public void testWaitForStartup0() throws Throwable {
		service.addService(h1);
		service.addApplication(h4);
		CountDownLatch finished = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				try {
					service.waitForStartup();
					finished.countDown();
				} catch ( InterruptedException e ) { }
			}
		}.start();
		
		service.init();
		service.startup();
		service.shutdown();
		assertTrue(service.waitForShutdown(1000));
	}
	
	@Test
	public void testWaitForShutdown0() throws Throwable {
		service.addService(h1);
		service.addApplication(h4);
		CountDownLatch finished = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				try {
					service.waitForShutdown();
					finished.countDown();
				} catch ( InterruptedException e ) { }
			}
		}.start();
		
		service.init();
		service.startup();
		service.shutdown();
		assertTrue(service.waitForShutdown(1000));
	}
	
	@Test
	public void testWorkerInterruptionUnlocksAllWaiters() throws Exception {
		service.addService(h1);
		service.addApplication(h2);
		CountDownLatch finished = new CountDownLatch(2);
		new Thread() {
			@Override
			public void run() {
				try {
					if ( service.waitForShutdown(1000) ) {
						finished.countDown();
					}
				} catch ( InterruptedException e) { }
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					if ( service.waitForStartup(1000) ) {
						finished.countDown();
					}
				} catch ( InterruptedException e ) { }
			}
		}.start();
		
		service.getWorkerThread().interrupt();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertTrue(service.waitForShutdown(1000));
		assertEquals(new ArrayList<>(), log);
	}

	@Test
	public void testRun_AllOK() throws Throwable {
		service.addService(h1);
		service.addService(h2);
		service.addService(h3);
		service.addApplication(h4);
		service.addApplication(h5);
		service.addApplication(h6);
		service.init();
		service.startup();
		assertTrue(service.waitForStartup(1000));
		service.shutdown();
		
		List<String> expected = new ArrayList<>();
		expected.add("!h1");
		expected.add("!h2");
		expected.add("!h3");
		expected.add("!h4");
		expected.add("!h5");
		expected.add("!h6");
		expected.add("+h1");
		expected.add("+h2");
		expected.add("+h3");
		expected.add("+h4");
		expected.add("+h5");
		expected.add("+h6");
		expected.add("-h6");
		expected.add("-h5");
		expected.add("-h4");
		expected.add("-h3");
		expected.add("-h2");
		expected.add("-h1");
		assertTrue(service.waitForShutdown(1000));
		assertEquals(expected, log);
	}
	
	@Test
	public void testRun_ServiceStartupFailure() throws Throwable {
		h2 = new TestHandler("h2", log, true, false);
		service.addService(h1);
		service.addService(h2);
		service.addService(h3);
		service.addApplication(h4);
		service.addApplication(h5);
		service.addApplication(h6);
		service.init();
		service.startup();
		
		assertTrue(service.waitForShutdown(1000));
		List<String> expected = new ArrayList<>();
		expected.add("!h1");
		expected.add("!h2");
		expected.add("!h3");
		expected.add("!h4");
		expected.add("!h5");
		expected.add("!h6");
		expected.add("+h1");
		expected.add("+h2");
		expected.add("-h1");
		assertEquals(expected, log);
	}
	
	@Test
	public void testRun_ServiceShutdownFailure() throws Throwable {
		h2 = new TestHandler("h2", log, false, true);
		service.addService(h1);
		service.addService(h2);
		service.addService(h3);
		service.addApplication(h4);
		service.addApplication(h5);
		service.addApplication(h6);
		service.init();
		service.startup();
		
		service.shutdown();
		assertTrue(service.waitForShutdown(1000));
		List<String> expected = new ArrayList<>();
		expected.add("!h1");
		expected.add("!h2");
		expected.add("!h3");
		expected.add("!h4");
		expected.add("!h5");
		expected.add("!h6");
		expected.add("+h1");
		expected.add("+h2");
		expected.add("+h3");
		expected.add("+h4");
		expected.add("+h5");
		expected.add("+h6");
		expected.add("-h6");
		expected.add("-h5");
		expected.add("-h4");
		expected.add("-h3");
		expected.add("-h2");
		expected.add("-h1");
		assertEquals(expected, log);
	}
	
	@Test
	public void testRun_AppStartupFailure() throws Throwable {
		h5 = new TestHandler("h5", log, true, false);
		service.addService(h1);
		service.addService(h2);
		service.addService(h3);
		service.addApplication(h4);
		service.addApplication(h5);
		service.addApplication(h6);
		service.init();
		service.startup();
		
		service.shutdown();
		assertTrue(service.waitForShutdown(1000));
		List<String> expected = new ArrayList<>();
		expected.add("!h1");
		expected.add("!h2");
		expected.add("!h3");
		expected.add("!h4");
		expected.add("!h5");
		expected.add("!h6");
		expected.add("+h1");
		expected.add("+h2");
		expected.add("+h3");
		expected.add("+h4");
		expected.add("+h5");
		expected.add("+h6");
		expected.add("-h6");
		expected.add("-h4");
		expected.add("-h3");
		expected.add("-h2");
		expected.add("-h1");
		assertEquals(expected, log);
	}
	
	@Test
	public void testRun_AppShutdownFailure() throws Throwable {
		h5 = new TestHandler("h5", log, false, true);
		service.addService(h1);
		service.addService(h2);
		service.addService(h3);
		service.addApplication(h4);
		service.addApplication(h5);
		service.addApplication(h6);
		service.init();
		service.startup();
		
		service.shutdown();
		assertTrue(service.waitForShutdown(1000));
		List<String> expected = new ArrayList<>();
		expected.add("!h1");
		expected.add("!h2");
		expected.add("!h3");
		expected.add("!h4");
		expected.add("!h5");
		expected.add("!h6");
		expected.add("+h1");
		expected.add("+h2");
		expected.add("+h3");
		expected.add("+h4");
		expected.add("+h5");
		expected.add("+h6");
		expected.add("-h6");
		expected.add("-h5");
		expected.add("-h4");
		expected.add("-h3");
		expected.add("-h2");
		expected.add("-h1");
		assertEquals(expected, log);
	}
	
	@Test
	public void testInit_ThrowsIfAlreadyInitialized() throws Throwable {
		service.init();
		try {
			service.init();
			fail("Expected: " + IllegalStateException.class.getSimpleName());
		} catch ( IllegalStateException e ) {
			assertEquals("Already initialized", e.getMessage());
		}
	}
	
	@Test
	public void testStartup_ThrowsIfNotInitialized() throws Exception {
		try {
			service.startup();
			fail("Expected: " + IllegalStateException.class.getSimpleName());
		} catch ( IllegalStateException e ) {
			assertEquals("Not initialized", e.getMessage());
		}
	}
	
}
