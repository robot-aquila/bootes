package ru.prolib.bootes.lib.app;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.utils.PriceScaleDB;
import ru.prolib.aquila.data.replay.CandleReplayService;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.service.UIService;

public class AppServiceLocatorTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private AppRuntimeService arsMock;
	private Scheduler schedulerMock;
	private AppServiceLocator service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		arsMock = control.createMock(AppRuntimeService.class);
		schedulerMock = control.createMock(Scheduler.class);
		service = new AppServiceLocator(arsMock);
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetConfig_ThrowsIfNotDefined() throws Exception {
		service.getConfig();
	}
	
	@Test
	public void testGetConfig() throws Exception {
		AppConfig2 confMock = control.createMock(AppConfig2.class);
		service.setConfig(confMock);
		control.replay();
		
		AppConfig2 actual = service.getConfig();
		
		control.verify();
		assertSame(confMock, actual);
		assertSame(confMock, service.getConfig());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetPriceScaleDB_ThrowsIfNotDefined() {		
		service.getPriceScaleDB();
	}
	
	@Test
	public void testGetPriceScaleDB() {
		PriceScaleDB sdbMock = control.createMock(PriceScaleDB.class);
		service.setPriceScaleDB(sdbMock);
		control.replay();
		
		PriceScaleDB actual = service.getPriceScaleDB();
		
		control.verify();
		assertSame(sdbMock, actual);
		assertSame(sdbMock, service.getPriceScaleDB());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetEventQueue_ThrowsIfNotDefined() {
		service.getEventQueue();
	}
	
	@Test
	public void testGetEventQueue() {
		EventQueue eqMock = control.createMock(EventQueue.class);
		service.setEventQueue(eqMock);
		control.replay();
		
		EventQueue actual = service.getEventQueue();
		
		control.verify();
		assertSame(eqMock, actual);
		assertSame(eqMock, service.getEventQueue());
	}
	
	@Test
	public void testGetRuntimeService() {
		control.replay();
		
		AppRuntimeService actual = service.getRuntimeService();
		
		control.verify();
		assertNotNull(actual);
		assertSame(arsMock, service.getRuntimeService());
		assertSame(arsMock, service.getRuntimeService());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetScheduler_ThrowsIfNotDefined() throws Exception {
		service.getScheduler();
	}
	
	@Test
	public void testGetScheduler() throws Exception {
		service.setScheduler(schedulerMock);
		control.replay();
		
		Scheduler actual = service.getScheduler();
		
		control.verify();
		assertSame(schedulerMock, actual);
		assertSame(schedulerMock, service.getScheduler());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetTerminal_ThrowsIfNotDefined() throws Exception {
		service.getTerminal();
	}
	
	@Test
	public void testGetTerminal() {
		Terminal termMock = control.createMock(Terminal.class);
		service.setTerminal(termMock);
		
		Terminal actual = service.getTerminal();
		
		assertSame(termMock, actual);
		assertSame(termMock, service.getTerminal());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetUIService_ThrowsIfNotDefined() throws Exception {
		service.getUIService();
	}
	
	@Test
	public void testGetUIService() {
		UIService uisMock = control.createMock(UIService.class);
		service.setUIService(uisMock);
		
		UIService actual = service.getUIService();
		
		assertNotNull(actual);
		assertSame(uisMock, actual);
		assertSame(actual, service.getUIService());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetOHLCHistoryStorage_ThrowsIfNotDefined() throws Exception {
		service.getOHLCHistoryStorage();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetOHLCHistoryStorage() {
		MDStorage<TFSymbol, Candle> storageMock = control.createMock(MDStorage.class);
		service.setOHLCHistoryStorage(storageMock);
		
		MDStorage<TFSymbol, Candle> actual = service.getOHLCHistoryStorage();
		
		assertNotNull(actual);
		assertSame(storageMock, actual);
		assertSame(actual, service.getOHLCHistoryStorage());
	}

	@Test (expected=NullPointerException.class)
	public void testGetOHLCReplayService_ThrowsIfNotDefined() {
		service.getOHLCReplayService();
	}
	
	@Test
	public void testGetOHLCReplayService() {
		CandleReplayService crsMock = control.createMock(CandleReplayService.class);
		service.setOHLCReplayService(crsMock);
		
		CandleReplayService actual = service.getOHLCReplayService();
		
		assertNotNull(actual);
		assertSame(crsMock, actual);
		assertSame(actual, service.getOHLCReplayService());
	}
	
	@Test
	public void testGetMessages() {
		IMessages messagesMock = control.createMock(IMessages.class);
		service.setMessages(messagesMock);
		
		IMessages actual = service.getMessages();
		
		assertNotNull(actual);
		assertSame(messagesMock, actual);
		assertSame(actual, service.getMessages());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetMessages_ThrowsIfNotDefined() {
		service.getMessages();
	}
	
	@Test
	public void testGetZoneID() {
		ZoneId x = ZoneId.of("America/Asuncion");
		assertEquals(ZoneId.systemDefault(), service.getZoneID());
		service.setZoneID(x);
		assertEquals(x, service.getZoneID());
	}
	
	@Test
	public void testGetSqlDBConn() {
		Connection dbhMock = control.createMock(Connection.class);
		service.setSqlDBConn(dbhMock);
		
		Connection actual = service.getSqlDBConn();
		
		assertNotNull(actual);
		assertSame(dbhMock, actual);
		assertSame(actual, service.getSqlDBConn());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSqlDBConn_ThrowsIfNotDefined() {
		service.getSqlDBConn();
	}

}
