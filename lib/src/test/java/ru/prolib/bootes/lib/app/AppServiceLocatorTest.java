package ru.prolib.bootes.lib.app;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.utils.PriceScaleDB;

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

}
