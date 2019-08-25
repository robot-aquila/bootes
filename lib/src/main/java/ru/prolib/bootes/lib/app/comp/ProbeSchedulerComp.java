package ru.prolib.bootes.lib.app.comp;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.probe.SchedulerBuilder;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerControlToolbar;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerTaskFilter;
import ru.prolib.aquila.probe.scheduler.ui.SymbolUpdateTaskFilter;
import ru.prolib.aquila.probe.scheduler.utils.EventQueueSynchronizerV2;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.config.SchedulerConfig2;
import ru.prolib.bootes.lib.config.SchedulerConfig2Section;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.AppShutdown;
import ru.prolib.bootes.lib.service.task.ProbeRun;
import ru.prolib.bootes.lib.service.task.ProbeStop;

public class ProbeSchedulerComp extends CommonComp {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ProbeSchedulerComp.class);
	}
	
	private static final String CONFIG_SECTION_ID = "probe-scheduler";
	private ARSHandler handler;

	public ProbeSchedulerComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}

	@Override
	public void init() throws Throwable {
		AppConfig2 app_conf = serviceLocator.getConfig();
		SchedulerConfig2 conf = app_conf.getSection(CONFIG_SECTION_ID);
		SchedulerBuilder builder = new SchedulerBuilder()
			.setName(serviceID)
			.setExecutionSpeed(0);
		if ( conf.getInitialTime() != null ) {
			builder.setInitialTime(conf.getInitialTime());
		}
		
		SchedulerImpl scheduler = builder.buildScheduler();
		serviceLocator.setScheduler(scheduler);
		scheduler.addSynchronizer(new EventQueueSynchronizerV2(serviceLocator.getEventQueue()));

		ARSHandlerBuilder hb = new ARSHandlerBuilder()
				.withID(serviceID)
				.addShutdownAction(new ProbeStop(scheduler));
		if ( conf.isAutoStart() ) {
			hb.addStartupAction(new ProbeRun(scheduler));
		}
		if ( conf.getStopTime() != null ) {
			if ( conf.isAutoShutdown() ) {
				scheduler.schedule(new AppShutdown(serviceLocator.getRuntimeService()), conf.getStopTime());
			} else {
				scheduler.schedule(new ProbeStop(scheduler), conf.getStopTime());
			}
		}
		if ( ! app_conf.getBasicConfig().isHeadless() ) {
			UIService uis = serviceLocator.getUIService();
			List<SchedulerTaskFilter> filters = new ArrayList<>();
			filters.add(new SymbolUpdateTaskFilter(uis.getMessages()));
			uis.getTopPanel().add(new SchedulerControlToolbar(uis.getMessages(), scheduler, uis.getZoneID(), filters));
		}
		handler = hb.build();
	}

	@Override
	public void startup() throws Throwable {
		logger.debug("startup at simulated time: {}", serviceLocator.getScheduler().getCurrentTime());
		handler.startup();
	}

	@Override
	public void shutdown() throws Throwable {
		logger.debug("shutdown at simulated time: {}", serviceLocator.getScheduler().getCurrentTime());
		handler.shutdown();
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		config_service.addSection(CONFIG_SECTION_ID, new SchedulerConfig2Section());
	}

}
