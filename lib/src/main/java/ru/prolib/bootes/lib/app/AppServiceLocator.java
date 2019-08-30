package ru.prolib.bootes.lib.app;

import java.sql.Connection;
import java.time.ZoneId;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.utils.PriceScaleDB;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.service.UIService;

public class AppServiceLocator {
	private final AppRuntimeService ars;
	private AppConfig2 config;
	private PriceScaleDB scaleDB;
	private EventQueue eventQueue;
	private Scheduler scheduler;
	private Terminal terminal;
	private UIService uis;
	private MDStorage<TFSymbol, Candle> ohlcHistoryStorage;
	private IMessages messages;
	private ZoneId zoneID;
	private Connection sqlDBConn;
	
	public AppServiceLocator(AppRuntimeService ars) {
		this.ars = ars;
		zoneID = ZoneId.systemDefault();
	}
	
	public synchronized void setConfig(AppConfig2 config) {
		this.config = config;
	}
	
	public synchronized AppConfig2 getConfig() {
		if ( config == null ) {
			throw new NullPointerException();
		}
		return config;
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
	
	public synchronized void setUIService(UIService service) {
		this.uis = service;
	}
	
	public synchronized UIService getUIService() {
		if ( uis == null ) {
			throw new NullPointerException();
		}
		return uis;
	}
	
	public synchronized void setOHLCHistoryStorage(MDStorage<TFSymbol, Candle> storage) {
		this.ohlcHistoryStorage = storage;
	}
	
	public synchronized MDStorage<TFSymbol, Candle> getOHLCHistoryStorage() {
		if ( ohlcHistoryStorage == null ) {
			throw new NullPointerException();
		}
		return ohlcHistoryStorage;
	}
	
	public synchronized IMessages getMessages() {
		if ( messages == null ) {
			throw new NullPointerException();
		}
		return messages;
	}
	
	public synchronized void setMessages(IMessages messages) {
		this.messages = messages;
	}
	
	public synchronized ZoneId getZoneID() {
		return zoneID;
	}
	
	public synchronized void setZoneID(ZoneId zoneID) {
		this.zoneID = zoneID;
	}
	
	public synchronized Connection getSqlDBConn() {
		if ( sqlDBConn == null ) {
			throw new NullPointerException();
		}
		return sqlDBConn;
	}
	
	public synchronized void setSqlDBConn(Connection connection) {
		this.sqlDBConn = connection;
	}

}
