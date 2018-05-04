package ru.prolib.bootes.lib.service;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.utils.PriceScaleDBLazy;
import ru.prolib.aquila.core.utils.PriceScaleDBTB;
import ru.prolib.aquila.probe.SchedulerBuilder;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.probe.scheduler.utils.EventQueueSynchronizer;
import ru.prolib.aquila.qforts.impl.QFBuilder;
import ru.prolib.aquila.qforts.impl.QFTransactionException;
import ru.prolib.aquila.web.utils.WUDataFactory;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.SchedulerConfig;
import ru.prolib.bootes.lib.config.TerminalConfig;
import ru.prolib.bootes.lib.service.task.AppShutdown;
import ru.prolib.bootes.lib.service.task.ProbeRun;
import ru.prolib.bootes.lib.service.task.ProbeStop;

public class AppServiceLocator {
	
	public static class Factory {
		
		public SchedulerBuilder createProbeBuilder() {
			return new SchedulerBuilder();
		}
		
		public AppRuntimeService createRuntimeService() {
			return new AppRuntimeServiceImpl();
		}
		
		public EventQueue createEventQueue(String queueID) {
			return new EventQueueImpl(queueID);
		}
		
		public QFBuilder createQFBuilder() {
			return new QFBuilder();
		}
		
		public BasicTerminalBuilder createTerminalBuilder() {
			return new BasicTerminalBuilder();
		}
		
		public WUDataFactory createWUDataFactory() {
			return new WUDataFactory();
		}
		
		public PriceScaleDBLazy createPriceScaleDBLazy() {
			return new PriceScaleDBLazy();
		}
		
		public PriceScaleDBTB createPriceScaleDBTB(Terminal terminal) {
			return new PriceScaleDBTB(terminal);
		}
		
	}
	
	private final AppConfig appConfig;
	private final Factory factory;
	private PriceScaleDBLazy scaleDB;
	private AppRuntimeService runtime;
	private EventQueue eventQueue;
	private Scheduler scheduler;
	private Terminal terminal;
	
	AppServiceLocator(AppConfig appConfig, Factory factory) {
		this.appConfig = appConfig;
		this.factory = factory;
	}
	
	public AppServiceLocator(AppConfig appConfig) {
		this(appConfig, new Factory());
	}
	
	public synchronized void setPriceScaleDB(PriceScaleDBLazy scaleDB) {
		this.scaleDB = scaleDB;
	}
	
	public synchronized PriceScaleDBLazy getPriceScaleDB() {
		if ( scaleDB == null ) {
			scaleDB = factory.createPriceScaleDBLazy();
		}
		return scaleDB;
	}
	
	public synchronized void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}
	
	public synchronized EventQueue getEventQueue() {
		if ( eventQueue == null ) {
			eventQueue = factory.createEventQueue("BOOTES-QUEUE");
		}
		return eventQueue;
	}
	
	public synchronized void setRuntimeService(AppRuntimeService rts) {
		this.runtime = rts;
	}
	
	public synchronized AppRuntimeService getRuntimeService() {
		if ( runtime == null ) {
			runtime = factory.createRuntimeService();
		}
		return runtime;
	}
	
	public synchronized void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public synchronized Scheduler getScheduler() {
		if ( scheduler == null ) {
			SchedulerConfig c = appConfig.getSchedulerConfig();
			SchedulerBuilder builder = factory.createProbeBuilder()
				.setName("PROBE-SCHEDULER")
				.setExecutionSpeed(1);
			if ( c.getProbeInitialTime() != null ) {
				builder.setInitialTime(c.getProbeInitialTime());
			}
			
			SchedulerImpl s = builder.buildScheduler(); scheduler = s;
			s.addSynchronizer(new EventQueueSynchronizer(getEventQueue()));
			
			AppRuntimeService rts = getRuntimeService();
			rts.addShutdownAction(new ProbeStop(s));
			if ( c.isProbeAutoStart() ) {
				rts.addStartAction(new ProbeRun(s));
			}
			if ( c.getProbeStopTime() != null ) {
				if ( c.isProbeAutoShutdown() ) {
					scheduler.schedule(new AppShutdown(rts), c.getProbeStopTime());
				} else {
					scheduler.schedule(new ProbeStop(s), c.getProbeStopTime());
				}
			}
		}
		return scheduler;
	}
	
	public synchronized void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}
	
	public synchronized Terminal getTerminal() {
		if ( terminal == null ) {
			TerminalConfig conf = appConfig.getTerminalConfig();
			QFBuilder qf = factory.createQFBuilder();
			terminal = factory.createTerminalBuilder()
				.withEventQueue(getEventQueue())
				.withScheduler(getScheduler())
				.withTerminalID("BOOTES-TERMINAL")
				.withDataProvider(factory.createWUDataFactory()
						.decorateForSymbolAndL1DataReplayFM(qf.buildDataProvider(),
								getScheduler(),
								conf.getQFortsDataDirectory(),
								getPriceScaleDB()))
				.buildTerminal();
			scaleDB.setParentDB(factory.createPriceScaleDBTB(terminal));
			try {
				qf.buildEnvironment((EditableTerminal) terminal)
					.createPortfolio(conf.getQForstTestAccount(), conf.getQForstTestBalance());
			} catch ( QFTransactionException e ) {
				throw new IllegalStateException("Error creating test account", e);
			}
		}
		return terminal;
	}

}
