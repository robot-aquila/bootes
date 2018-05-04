package ru.prolib.bootes.lib.service;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.utils.PriceScaleDBLazy;
import ru.prolib.aquila.core.utils.PriceScaleDBTB;
import ru.prolib.aquila.probe.SchedulerBuilder;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.qforts.impl.QFBuilder;
import ru.prolib.aquila.qforts.impl.QFortsEnv;
import ru.prolib.aquila.web.utils.WUDataFactory;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.BasicConfig;
import ru.prolib.bootes.lib.config.BasicConfigBuilder;
import ru.prolib.bootes.lib.config.SchedulerConfig;
import ru.prolib.bootes.lib.config.SchedulerConfigBuilder;
import ru.prolib.bootes.lib.config.TerminalConfig;
import ru.prolib.bootes.lib.config.TerminalConfigBuilder;
import ru.prolib.bootes.lib.service.task.AppShutdown;
import ru.prolib.bootes.lib.service.task.ProbeRun;
import ru.prolib.bootes.lib.service.task.ProbeStop;

public class AppServiceLocatorTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private AppConfig appConfigMock;
	private AppServiceLocator.Factory factory, factoryMock;
	private SchedulerImpl probeMock;
	private EventQueue eventQueueMock;
	private AppRuntimeService rtsMock;
	private AppServiceLocator service;

	@Before
	public void setUp() throws Exception {
		factory = new AppServiceLocator.Factory();
		control = createStrictControl();
		appConfigMock = control.createMock(AppConfig.class);
		factoryMock = control.createMock(AppServiceLocator.Factory.class);
		probeMock = control.createMock(SchedulerImpl.class);
		eventQueueMock = control.createMock(EventQueue.class);
		rtsMock = control.createMock(AppRuntimeService.class);
		service = new AppServiceLocator(appConfigMock, factoryMock);
	}
	
	@Test
	public void testFactory_CreateProbeBuilder() {
		SchedulerBuilder actual = factory.createProbeBuilder();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreateRuntimeService() {
		AppRuntimeService actual = factory.createRuntimeService();
		
		assertNotNull(actual);
		assertEquals(AppRuntimeServiceImpl.class, actual.getClass());
	}
	
	@Test
	public void testFactory_CreateEventQueue() {
		EventQueue actual = factory.createEventQueue("ZULU-24");
		
		assertNotNull(actual);
		assertEquals(EventQueueImpl.class, actual.getClass());
		assertEquals("ZULU-24", actual.getId());
	}
	
	@Test
	public void testFactory_CreateQFBuilder() {
		QFBuilder actual = factory.createQFBuilder();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreateTerminalBuilder() {
		BasicTerminalBuilder actual = factory.createTerminalBuilder();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreateWUDataFactory() {
		WUDataFactory actual = factory.createWUDataFactory();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreatePriceScaleDBLazy() {
		PriceScaleDBLazy actual = factory.createPriceScaleDBLazy();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreatePriceScaleDBTB() {
		Terminal termMock = control.createMock(Terminal.class);
		
		PriceScaleDBTB actual = factory.createPriceScaleDBTB(termMock);
		
		assertNotNull(actual);
	}
	
	@Test
	public void testGetPriceScaleDB_Create() {
		PriceScaleDBLazy scaleDbMock = control.createMock(PriceScaleDBLazy.class);
		expect(factoryMock.createPriceScaleDBLazy()).andReturn(scaleDbMock);
		control.replay();
		
		PriceScaleDBLazy actual = service.getPriceScaleDB();
		
		control.verify();
		assertSame(scaleDbMock, actual);
		assertSame(scaleDbMock, service.getPriceScaleDB());
	}
	
	@Test
	public void testGetPriceScaleDB_Existing() {
		PriceScaleDBLazy scaleDbMock = control.createMock(PriceScaleDBLazy.class);
		service.setPriceScaleDB(scaleDbMock);
		control.replay();
		
		PriceScaleDBLazy actual = service.getPriceScaleDB();
		
		control.verify();
		assertSame(scaleDbMock, actual);
		assertSame(scaleDbMock, service.getPriceScaleDB());
	}
	
	@Test
	public void testGetEventQueue_Create() {
		EventQueue eqMock = control.createMock(EventQueue.class);
		expect(factoryMock.createEventQueue("BOOTES-QUEUE")).andReturn(eqMock);
		control.replay();
		
		EventQueue actual = service.getEventQueue();
		
		control.verify();
		assertSame(eqMock, actual);
		assertSame(eqMock, service.getEventQueue());
	}
	
	@Test
	public void testGetEventQueue_Existing() {
		EventQueue eqMock = control.createMock(EventQueue.class);
		service.setEventQueue(eqMock);
		control.replay();
		
		EventQueue actual = service.getEventQueue();
		
		control.verify();
		assertSame(eqMock, actual);
		assertSame(eqMock, service.getEventQueue());
	}
	
	@Test
	public void testGetRuntimeService_Create() {
		AppRuntimeService rtsMock = control.createMock(AppRuntimeService.class);
		expect(factoryMock.createRuntimeService()).andReturn(rtsMock);
		control.replay();
		
		AppRuntimeService actual = service.getRuntimeService();
		
		control.verify();
		assertSame(rtsMock, actual);
		assertSame(rtsMock, service.getRuntimeService());
	}
	
	@Test
	public void testGetRuntimeService_Existing() {
		AppRuntimeService rtsMock = control.createMock(AppRuntimeService.class);
		service.setRuntimeService(rtsMock);
		control.replay();
		
		AppRuntimeService actual = service.getRuntimeService();
		
		control.verify();
		assertSame(rtsMock, actual);
		assertSame(rtsMock, service.getRuntimeService());
	}
	
	@Test
	public void testGetScheduler_Create_NoSpecOptions() throws Exception {
		BasicConfig bcStub = new BasicConfigBuilder().build();
		SchedulerConfig scStub = new SchedulerConfigBuilder().build(bcStub);
		SchedulerBuilder builderMock = control.createMock(SchedulerBuilder.class);
		service.setEventQueue(eventQueueMock);
		service.setRuntimeService(rtsMock);
		expect(appConfigMock.getSchedulerConfig()).andReturn(scStub);
		expect(factoryMock.createProbeBuilder()).andReturn(builderMock);
		expect(builderMock.setName("PROBE-SCHEDULER")).andReturn(builderMock);
		expect(builderMock.setExecutionSpeed(1)).andReturn(builderMock);
		expect(builderMock.buildScheduler()).andReturn(probeMock);
		probeMock.addSynchronizer(anyObject()); // TODO:
		rtsMock.addShutdownAction(new ProbeStop(probeMock));
		control.replay();
		
		Scheduler actual = service.getScheduler();
		
		control.verify();
		assertSame(probeMock, actual);
	}
	
	@Test
	public void testGetScheduler_Create_WithInitTime() throws Exception {
		BasicConfig bcStub = new BasicConfigBuilder().build();
		SchedulerConfig scStub = new SchedulerConfigBuilder()
			.withProbeInitialTime(T("2018-05-03T00:06:00Z"))
			.build(bcStub);
		SchedulerBuilder builderMock = control.createMock(SchedulerBuilder.class);
		service.setEventQueue(eventQueueMock);
		service.setRuntimeService(rtsMock);
		expect(appConfigMock.getSchedulerConfig()).andReturn(scStub);
		expect(factoryMock.createProbeBuilder()).andReturn(builderMock);
		expect(builderMock.setName("PROBE-SCHEDULER")).andReturn(builderMock);
		expect(builderMock.setExecutionSpeed(1)).andReturn(builderMock);
		expect(builderMock.setInitialTime(T("2018-05-03T00:06:00Z"))).andReturn(builderMock);
		expect(builderMock.buildScheduler()).andReturn(probeMock);
		probeMock.addSynchronizer(anyObject()); // TODO:
		rtsMock.addShutdownAction(new ProbeStop(probeMock));
		control.replay();
		
		Scheduler actual = service.getScheduler();
		
		control.verify();
		assertSame(probeMock, actual);
	}
	
	@Test
	public void testGetScheduler_Create_WithAutoStart() throws Exception {
		BasicConfig bcStub = new BasicConfigBuilder().build();
		SchedulerConfig scStub = new SchedulerConfigBuilder()
			.withProbeAutoStart(true)
			.build(bcStub);
		SchedulerBuilder builderMock = control.createMock(SchedulerBuilder.class);
		service.setEventQueue(eventQueueMock);
		service.setRuntimeService(rtsMock);
		expect(appConfigMock.getSchedulerConfig()).andReturn(scStub);
		expect(factoryMock.createProbeBuilder()).andReturn(builderMock);
		expect(builderMock.setName("PROBE-SCHEDULER")).andReturn(builderMock);
		expect(builderMock.setExecutionSpeed(1)).andReturn(builderMock);
		expect(builderMock.buildScheduler()).andReturn(probeMock);
		probeMock.addSynchronizer(anyObject()); // TODO:
		rtsMock.addShutdownAction(new ProbeStop(probeMock));
		rtsMock.addStartAction(new ProbeRun(probeMock));
		control.replay();
		
		Scheduler actual = service.getScheduler();
		
		control.verify();
		assertSame(probeMock, actual);
	}
	
	@Test
	public void testGetScheduler_Create_WithAutoStop() throws Exception {
		BasicConfig bcStub = new BasicConfigBuilder().build();
		SchedulerConfig scStub = new SchedulerConfigBuilder()
			.withProbeStopTime(T("2018-05-03T23:59:59Z"))
			.build(bcStub);
		SchedulerBuilder builderMock = control.createMock(SchedulerBuilder.class);
		service.setEventQueue(eventQueueMock);
		service.setRuntimeService(rtsMock);
		expect(appConfigMock.getSchedulerConfig()).andReturn(scStub);
		expect(factoryMock.createProbeBuilder()).andReturn(builderMock);
		expect(builderMock.setName("PROBE-SCHEDULER")).andReturn(builderMock);
		expect(builderMock.setExecutionSpeed(1)).andReturn(builderMock);
		expect(builderMock.buildScheduler()).andReturn(probeMock);
		probeMock.addSynchronizer(anyObject()); // TODO:
		rtsMock.addShutdownAction(new ProbeStop(probeMock));
		expect(probeMock.schedule(new ProbeStop(probeMock), T("2018-05-03T23:59:59Z"))).andReturn(null);
		control.replay();
		
		Scheduler actual = service.getScheduler();
		
		control.verify();
		assertSame(probeMock, actual);
	}

	@Test
	public void testGetScheduler_Create_WithAutoShutdown() throws Exception {
		BasicConfig bcStub = new BasicConfigBuilder().build();
		SchedulerConfig scStub = new SchedulerConfigBuilder()
			.withProbeStopTime(T("2018-05-03T00:00:00Z"))
			.withProbeAutoShutdown(true)
			.build(bcStub);
		SchedulerBuilder builderMock = control.createMock(SchedulerBuilder.class);
		service.setEventQueue(eventQueueMock);
		service.setRuntimeService(rtsMock);
		expect(appConfigMock.getSchedulerConfig()).andReturn(scStub);
		expect(factoryMock.createProbeBuilder()).andReturn(builderMock);
		expect(builderMock.setName("PROBE-SCHEDULER")).andReturn(builderMock);
		expect(builderMock.setExecutionSpeed(1)).andReturn(builderMock);
		expect(builderMock.buildScheduler()).andReturn(probeMock);
		probeMock.addSynchronizer(anyObject()); // TODO:
		rtsMock.addShutdownAction(new ProbeStop(probeMock));
		expect(probeMock.schedule(new AppShutdown(rtsMock), T("2018-05-03T00:00:00Z"))).andReturn(null);
		control.replay();
		
		Scheduler actual = service.getScheduler();
		
		control.verify();
		assertSame(probeMock, actual);
	}
	
	@Test
	public void testGetScheduler_Existing() {
		Scheduler schedulerMock = control.createMock(Scheduler.class);
		service.setScheduler(schedulerMock);
		control.replay();
		
		Scheduler actual = service.getScheduler();
		
		control.verify();
		assertSame(schedulerMock, actual);
		assertSame(schedulerMock, service.getScheduler());
	}
	
	@Test
	public void testGetTerminal_Create() throws Exception {
		BasicConfig bcStub = new BasicConfigBuilder().build();
		TerminalConfig tcStub = new TerminalConfigBuilder()
			.withQFortsDataDirectory(new File("my/data"))
			.withQFortsTestAccount(new Account("HELLO"))
			.withQFortsTestBalance(CDecimalBD.ofRUB2("10000"))
			.build(bcStub);
		QFBuilder qfMock = control.createMock(QFBuilder.class);
		Scheduler schedulerMock = control.createMock(Scheduler.class);
		WUDataFactory wufMock = control.createMock(WUDataFactory.class);
		DataProvider dpQfMock = control.createMock(DataProvider.class),
				dpMock = control.createMock(DataProvider.class);
		BasicTerminalBuilder tbMock = control.createMock(BasicTerminalBuilder.class);
		EditableTerminal termMock = control.createMock(EditableTerminal.class);
		QFortsEnv qfeMock = control.createMock(QFortsEnv.class);
		PriceScaleDBLazy scaleDbLazyMock = control.createMock(PriceScaleDBLazy.class);
		PriceScaleDBTB scaleDbTbMock = control.createMock(PriceScaleDBTB.class);
		service.setEventQueue(eventQueueMock);
		service.setScheduler(schedulerMock);
		service.setPriceScaleDB(scaleDbLazyMock);
		expect(appConfigMock.getTerminalConfig()).andReturn(tcStub);
		expect(factoryMock.createQFBuilder()).andReturn(qfMock);
		expect(factoryMock.createTerminalBuilder()).andReturn(tbMock);
		expect(tbMock.withEventQueue(eventQueueMock)).andReturn(tbMock);
		expect(tbMock.withScheduler(schedulerMock)).andReturn(tbMock);
		expect(tbMock.withTerminalID("BOOTES-TERMINAL")).andReturn(tbMock);
		expect(factoryMock.createWUDataFactory()).andReturn(wufMock);
		expect(qfMock.buildDataProvider()).andReturn(dpQfMock);
		expect(wufMock.decorateForSymbolAndL1DataReplayFM(dpQfMock, schedulerMock, new File("my/data"), scaleDbLazyMock))
			.andReturn(dpMock);
		expect(tbMock.withDataProvider(dpMock)).andReturn(tbMock);
		expect(tbMock.buildTerminal()).andReturn(termMock);
		expect(factoryMock.createPriceScaleDBTB(termMock)).andReturn(scaleDbTbMock);
		scaleDbLazyMock.setParentDB(scaleDbTbMock);
		expect(qfMock.buildEnvironment(termMock)).andReturn(qfeMock);
		expect(qfeMock.createPortfolio(new Account("HELLO"), CDecimalBD.ofRUB2("10000"))).andReturn(null);
		control.replay();
		
		Terminal actual = service.getTerminal();
		
		control.verify();
		assertSame(termMock, actual);
	}
	
	@Test
	public void testGetTerminal_Existing() {
		Terminal termMock = control.createMock(Terminal.class);
		service.setTerminal(termMock);
		control.replay();
		
		Terminal actual = service.getTerminal();
		
		control.verify();
		assertSame(termMock, actual);
		assertSame(termMock, service.getTerminal());
	}

}
