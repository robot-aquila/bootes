package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;

public class EventQueueComp extends CommonComp {
	private static final String DEFAULT_ID = "BOOTES-QUEUE";

	public EventQueueComp(AppConfig appConfig, AppServiceLocator serviceLocator, String serviceID) {
		super(appConfig, serviceLocator, serviceID);
	}
	
	public EventQueueComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		serviceLocator.setEventQueue(new EventQueueImpl(serviceID));
	}

	@Override
	public void startup() throws Throwable {

	}

	@Override
	public void shutdown() throws Throwable {

	}

}
