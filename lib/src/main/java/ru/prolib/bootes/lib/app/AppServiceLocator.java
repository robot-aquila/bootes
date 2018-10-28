package ru.prolib.bootes.lib.app;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.utils.PriceScaleDB;

public class AppServiceLocator {
	private final AppRuntimeService ars;
	private PriceScaleDB scaleDB;
	private EventQueue eventQueue;
	private Scheduler scheduler;
	private Terminal terminal;
	
	public AppServiceLocator(AppRuntimeService ars) {
		this.ars = ars;
	}
	
	public synchronized void setPriceScaleDB(PriceScaleDB scaleDB) {
		this.scaleDB = scaleDB;
	}
	
	public synchronized PriceScaleDB getPriceScaleDB() {
		if ( scaleDB == null ) {
			throw new NullPointerException();
		}
		return scaleDB;
	}
	
	public synchronized void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}
	
	public synchronized EventQueue getEventQueue() {
		if ( eventQueue == null ) {
			throw new NullPointerException();
		}
		return eventQueue;
	}
	
	public synchronized AppRuntimeService getRuntimeService() {
		return ars;
	}
	
	public synchronized void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public synchronized Scheduler getScheduler() {
		if ( scheduler == null ) {
			throw new NullPointerException();
		}
		return scheduler;
	}
	
	public synchronized void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}
	
	public synchronized Terminal getTerminal() {
		if ( terminal == null ) {
			throw new NullPointerException();
		}
		return terminal;
	}

}
