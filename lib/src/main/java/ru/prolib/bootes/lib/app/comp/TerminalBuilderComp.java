package ru.prolib.bootes.lib.app.comp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.app.DriverRegistry;

public class TerminalBuilderComp implements AppComponent, DriverRegistry {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TerminalBuilderComp.class);
	}
	
	private final AppServiceLocator serviceLocator;
	private final Map<String, AppComponent> drivers;
	private AppComponent selectedDriver;
	
	TerminalBuilderComp(AppServiceLocator service_locator, Map<String, AppComponent> drivers) {
		this.serviceLocator = service_locator;
		this.drivers = drivers;
	}
	
	public TerminalBuilderComp(AppServiceLocator service_locator) {
		this(service_locator, new HashMap<>());
	}

	@Override
	public void init() throws Throwable {
		String driver_id = serviceLocator.getConfig().getBasicConfig().getDriver();
		selectedDriver = drivers.get(driver_id);
		if ( selectedDriver == null ) {
			throw new IllegalArgumentException("Driver not found: " + driver_id);
		}
		logger.info("Selected driver: {}", driver_id);
		selectedDriver.init();
	}

	@Override
	public void startup() throws Throwable {
		selectedDriver.startup();
	}

	@Override
	public void shutdown() throws Throwable {
		selectedDriver.shutdown();
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		for ( AppComponent comp : drivers.values() ) {
			comp.registerConfig(config_service);
		}
		// TODO: configure --driver option here
	}

	@Override
	public void registerDriver(String driver_id, AppComponent component) {
		drivers.put(driver_id, component);
	}

}
