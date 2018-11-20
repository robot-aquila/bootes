package ru.prolib.bootes.lib.app.comp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.eque.EventQueueStats;
import ru.prolib.aquila.ui.form.EventQueueStatePanel;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.service.UIService;

public class EventQueueComp extends CommonComp {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EventQueueComp.class);
	}
	
	private static final String DEFAULT_ID = "BOOTES-QUEUE";
	private EventQueueImpl queue;

	public EventQueueComp(AppConfig appConfig, AppServiceLocator serviceLocator, String serviceID) {
		super(appConfig, serviceLocator, serviceID);
	}
	
	public EventQueueComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		serviceLocator.setEventQueue(queue = new EventQueueImpl(serviceID));
		if ( ! appConfig.getBasicConfig().isHeadless() ) {
			UIService uis = serviceLocator.getUIService();
			uis.getTopPanel().add(new EventQueueStatePanel(uis.getMessages(), queue));
		}
	}

	@Override
	public void startup() throws Throwable {

	}

	@Override
	public void shutdown() throws Throwable {
		EventQueueStats stats = queue.getStats();
		if ( stats != null ) {
			logger.debug("            Events sent: {}", stats.getTotalEventsSent());
			logger.debug("      Events dispatched: {}", stats.getTotalEventsDispatched());
			logger.debug("Building task list time: {} ns", stats.getBuildingTaskListTime());
			logger.debug("       Dispatching time: {} ns", stats.getDispatchingTime());
			logger.debug("        Delivering time: {} ns", stats.getDeliveryTime());
			
		}
	}

}