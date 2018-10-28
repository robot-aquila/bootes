package ru.prolib.bootes.lib.app;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ru.prolib.bootes.lib.app.comp.EventQueueComp;
import ru.prolib.bootes.lib.app.comp.PriceScaleDBComp;
import ru.prolib.bootes.lib.app.comp.ProbeSchedulerComp;
import ru.prolib.bootes.lib.app.comp.QFortsTerminalComp;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.ConfigException;
import ru.prolib.bootes.lib.service.ars.AppRuntimeServiceImpl;

public abstract class App {
	private static final Logger logger;
	
	static {
		logger = Logger.getLogger(App.class);
	}
	
	public static final int STATUS_OK = 0;
	public static final int STATUS_ERR_INIT_FAILED = 1;

	protected AppServiceLocator serviceLocator = null;
	protected AppConfig appConfig = null;
	
	/**
	 * Run application.
	 * <p>
	 * Call this method to run application.
	 * <p>
	 * @param args - command line arguments
	 * @return exit code
	 * @throws Throwable unhandled exception
	 */
	public int run(String[] args) throws Throwable {
		Logger.getRootLogger().setLevel(Level.DEBUG);
		logger.debug("APP: Is starting...");
		try {
			AppConfigService acs = createConfigService();
			appConfig = acs.loadConfig(args);
			if ( appConfig.getBasicConfig().isShowHelp() ) {
				acs.showHelp(80, "todo", "", "");
				return 0;
			}
			serviceLocator = createServiceLocator();
		} catch ( ConfigException e ) {
			logger.error("Configuration error: ", e);
			return STATUS_ERR_INIT_FAILED;
		}
		AppRuntimeService ars = serviceLocator.getRuntimeService();
		CountDownLatch barrier = new CountDownLatch(1);
		Runtime.getRuntime().addShutdownHook(new Thread("APP_SHUTDOWN") {
			@Override
			public void run() {
				logger.debug("shutdown hook");
				ars.shutdown();
				try {
					barrier.await();
					//ars.waitForShutdown();
				} catch ( InterruptedException e ) { }
			}
		});
		try {
			registerServices(ars);
			registerApplications(ars);
			ars.init();
			ars.startup();
			logger.debug("APP: Is waiting for shutdown");
			ars.waitForShutdown();
			logger.debug("APP: Is exiting...");
		} catch ( Throwable e ) {
			logger.error("Startup error: ", e);
		} finally {
			ars.shutdown();
			barrier.countDown();
		}
		return STATUS_OK;
	}
	
	/**
	 * Create configuration service.
	 * <p>
	 * Override this method to produce specific instance of configuration service.
	 * <p>
	 * @return configuration service
	 */
	protected AppConfigService createConfigService() {
		return new AppConfigService();
	}
	
	/**
	 * Create service locator.
	 * <p>
	 * Override this method to produce specific instance of service locator.
	 * Use {@link #appConfig} property to get an access to configuration.
	 * <p>
	 * @return service locator
	 */
	protected AppServiceLocator createServiceLocator() {
		return new AppServiceLocator(new AppRuntimeServiceImpl("BOOTES-ARS"));
	}
	
	protected void registerServices(AppRuntimeService ars) {
		ars.addService(new PriceScaleDBComp(appConfig, serviceLocator));
		ars.addService(new EventQueueComp(appConfig, serviceLocator));
		ars.addService(new ProbeSchedulerComp(appConfig, serviceLocator));
		ars.addService(new QFortsTerminalComp(appConfig, serviceLocator));
	}
	
	abstract protected void registerApplications(AppRuntimeService ars);

}
