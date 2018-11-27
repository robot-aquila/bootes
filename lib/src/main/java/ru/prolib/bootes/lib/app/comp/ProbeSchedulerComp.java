package ru.prolib.bootes.lib.app.comp;

import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.probe.SchedulerBuilder;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerControlToolbar;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerTaskFilter;
import ru.prolib.aquila.probe.scheduler.ui.SymbolUpdateTaskFilter;
import ru.prolib.aquila.probe.scheduler.utils.EventQueueSynchronizer;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.SchedulerConfig;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.AppShutdown;
import ru.prolib.bootes.lib.service.task.ProbeRun;
import ru.prolib.bootes.lib.service.task.ProbeStop;

public class ProbeSchedulerComp extends CommonComp {
	private static final String DEFAULT_ID = "PROBE-SCHEDULER";
	private ARSHandler handler;

	public ProbeSchedulerComp(AppConfig appConfig, AppServiceLocator serviceLocator, String serviceID) {
		super(appConfig, serviceLocator, serviceID);
	}
	
	public ProbeSchedulerComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		SchedulerConfig c = appConfig.getSchedulerConfig();
		SchedulerBuilder builder = new SchedulerBuilder()
			.setName(serviceID)
			.setExecutionSpeed(0);
		if ( c.getProbeInitialTime() != null ) {
			builder.setInitialTime(c.getProbeInitialTime());
		}
		
		SchedulerImpl scheduler = builder.buildScheduler();
		serviceLocator.setScheduler(scheduler);
		scheduler.addSynchronizer(new EventQueueSynchronizer(serviceLocator.getEventQueue()));

		ARSHandlerBuilder hb = new ARSHandlerBuilder()
				.withID(serviceID)
				.addShutdownAction(new ProbeStop(scheduler));
		if ( c.isProbeAutoStart() ) {
			hb.addStartupAction(new ProbeRun(scheduler));
		}
		if ( c.getProbeStopTime() != null ) {
			if ( c.isProbeAutoShutdown() ) {
				scheduler.schedule(new AppShutdown(serviceLocator.getRuntimeService()), c.getProbeStopTime());
			} else {
				scheduler.schedule(new ProbeStop(scheduler), c.getProbeStopTime());
			}
		}
		if ( ! appConfig.getBasicConfig().isHeadless() ) {
			UIService uis = serviceLocator.getUIService();
			List<SchedulerTaskFilter> filters = new ArrayList<>();
			filters.add(new SymbolUpdateTaskFilter(uis.getMessages()));
			uis.getTopPanel().add(new SchedulerControlToolbar(uis.getMessages(), scheduler, uis.getZoneID(), filters));
		}
		handler = hb.build();
	}

	@Override
	public void startup() throws Throwable {
		handler.startup();
	}

	@Override
	public void shutdown() throws Throwable {
		handler.shutdown();
	}

}
