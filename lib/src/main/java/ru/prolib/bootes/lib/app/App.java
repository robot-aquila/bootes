package ru.prolib.bootes.lib.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import ru.prolib.bootes.lib.app.comp.UIComp;
import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.bootes.lib.app.comp.EventQueueComp;
import ru.prolib.bootes.lib.app.comp.MessagesComp;
import ru.prolib.bootes.lib.app.comp.OHLCHistoryStorageComp;
import ru.prolib.bootes.lib.app.comp.PriceScaleDBComp;
import ru.prolib.bootes.lib.app.comp.QFTerminalComp;
import ru.prolib.bootes.lib.app.comp.TerminalBuilderComp;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.service.ars.AppRuntimeServiceImpl;

public abstract class App {
	private static final Logger logger;
	
	static {
		logger = Logger.getLogger(App.class);
	}
	
	public static final int STATUS_OK = 0;
	public static final int STATUS_ERR_INIT_FAILED = 1;

	private AppServiceLocator serviceLocator = null;
	
	protected AppServiceLocator getServiceLocator() {
		return serviceLocator;
	}
	
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
		logger.debug("APP: Is starting...");
		List<AppComponent> srv_comps = new ArrayList<>(), app_comps = new ArrayList<>();
		try {
			serviceLocator = createServiceLocator();
			AppConfigService2 acs = createConfigService();
			registerServices(srv_comps);
			registerApplications(app_comps);
			for ( AppComponent comp : srv_comps ) {
				comp.registerConfig(acs);
			}
			for ( AppComponent comp : app_comps ) {
				comp.registerConfig(acs);
			}
			
			AppConfig2 app_conf = acs.loadConfig(args);
			if ( app_conf.getBasicConfig().isShowHelp() ) {
				acs.showHelp(80, "todo", "", "");
				return 0;
			}
			serviceLocator.setConfig(app_conf);
			
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
			for ( AppComponent comp : srv_comps ) {
				ars.addService(comp);
			}
			for ( AppComponent comp : app_comps ) {
				ars.addApplication(comp);
			}
			
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
	protected AppConfigService2 createConfigService() {
		return new AppConfigService2();
	}
	
	/**
	 * Create service locator.
	 * <p>
	 * Override this method to produce specific instance of service locator.
	 * <p>
	 * @return service locator
	 */
	protected AppServiceLocator createServiceLocator() {
		return new AppServiceLocator(new AppRuntimeServiceImpl("BOOTES-ARS"));
	}
	
	protected void registerServices(List<AppComponent> list) {
		list.add(new MessagesComp(getServiceLocator()));
		list.add(new PriceScaleDBComp(getServiceLocator()));
		list.add(new UIComp(getServiceLocator()));
		list.add(new EventQueueComp(getServiceLocator()));
		TerminalBuilderComp registry = new TerminalBuilderComp(getServiceLocator());
		registerTerminalServices(registry);
		list.add(registry);
		list.add(new OHLCHistoryStorageComp(getServiceLocator()));
	}
	
	protected void registerTerminalServices(DriverRegistry registry) {
		QFTerminalComp default_driver = new QFTerminalComp(serviceLocator);
		registry.registerDriver("default", default_driver);
		registry.registerDriver("qforts", default_driver);
	}
	
	abstract protected void registerApplications(List<AppComponent> list);

}
