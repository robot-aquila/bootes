package ru.prolib.bootes.tsgr001a.robot.sh;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.ofRUB5;

import java.time.Instant;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.core.sm.OnFinishAction;
import ru.prolib.aquila.core.sm.OnInterruptAction;
import ru.prolib.aquila.core.sm.OnTimeoutAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerOnTimer;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.s3.S3RobotStateListener;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class S3OpenPositionTest {
	private static Account ACCOUNT = new Account("TEST");
	private static Symbol SYMBOL = new Symbol("AQLA");
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private EditableTerminal terminalMock, terminal;
	private EditableOrder orderMock;
	private S3RobotStateListener rlistenerMock;
	private SMTriggerRegistry tregMock;
	private EventType eventTypeMock;
	private AppServiceLocator serviceLocator;
	private RobotState state;
	private S3OpenPosition service;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(EditableTerminal.class);
		orderMock = control.createMock(EditableOrder.class);
		tregMock = control.createMock(SMTriggerRegistry.class);
		eventTypeMock = control.createMock(EventType.class);
		rlistenerMock = control.createMock(S3RobotStateListener.class);
		state = new RobotState(rlistenerMock);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		state.setPortfolio(terminal.getEditablePortfolio(ACCOUNT));
		state.setSecurity(terminal.getEditableSecurity(SYMBOL));
		serviceLocator = new AppServiceLocator(null);
		serviceLocator.setTerminal(terminalMock);
		service = new S3OpenPosition(serviceLocator, state);
	}
	
	@Test
	public void testCtor2() {
		assertSame(service, service.getEnterAction());
		assertSame(service, service.getExitAction());
		
		assertEquals(4, service.getExits().size());
		assertNotNull(service.getExit("OPEN"));
		assertNotNull(service.getExit("SKIPPED"));
		assertNotNull(service.getExit("INTERRUPT"));
		assertNotNull(service.getExit("ERROR"));
		
		List<SMInput> actual_inputs = service.getInputs();
		assertEquals(3, actual_inputs.size());
		assertTrue(actual_inputs.contains(new SMInput(service, new OnTimeoutAction(service))));
		assertTrue(actual_inputs.contains(new SMInput(service, new OnFinishAction(service))));
		assertTrue(actual_inputs.contains(new SMInput(service, new OnInterruptAction(service))));
	}
	
	@Test
	public void testOnInterrupt_OrderNotDefined() throws Exception {
		control.replay();
		
		assertSame(service.getExit("INTERRUPT"), service.onInterrupt(null));
		
		control.verify();
	}
	
	@Test
	public void testOnInterrupt_OrderInFinalState() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getStatus()).andReturn(OrderStatus.FILLED);
		control.replay();
		
		assertSame(service.getExit("INTERRUPT"), service.onInterrupt(null));
		
		control.verify();
	}
	
	@Test
	public void testOnInterrupt_ErrorAtCancel() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getStatus()).andReturn(OrderStatus.ACTIVE);
		terminalMock.cancelOrder(orderMock);
		expectLastCall().andThrow(new OrderException("Test error"));
		control.replay();
		
		assertSame(service.getExit("INTERRUPT"), service.onInterrupt(null));
		
		control.verify();
	}
	
	@Test
	public void testOnInterrupt_Cancelled() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getStatus()).andReturn(OrderStatus.ACTIVE);
		terminalMock.cancelOrder(orderMock);
		control.replay();
		
		assertSame(service.getExit("INTERRUPT"), service.onInterrupt(null));
		
		control.verify();
	}
	
	@Test
	public void testOnTimeout_OrderInFinalState() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getStatus()).andReturn(OrderStatus.FILLED);
		control.replay();
		
		assertNull(service.onTimeout(null));
		
		control.verify();
	}
	
	@Test
	public void testOnTimeout_ErrorAtCancel() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getStatus()).andReturn(OrderStatus.ACTIVE);
		terminalMock.cancelOrder(orderMock);
		expectLastCall().andThrow(new OrderException("Test error"));
		control.replay();
		
		assertSame(service.getExit("ERROR"), service.onTimeout(null));
		
		control.verify();
	}
	
	@Test
	public void testOnTimeout_Cancelled() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getStatus()).andReturn(OrderStatus.ACTIVE);
		terminalMock.cancelOrder(orderMock);
		control.replay();
		
		assertNull(service.onTimeout(null));
		
		control.verify();
	}

	@Test
	public void testOnFinish_OrderNotFilled() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getInitialVolume()).andStubReturn(of(10L));
		expect(orderMock.getCurrentVolume()).andStubReturn(of(10L));
		control.replay();
		
		assertSame(service.getExit("SKIPPED"), service.onFinish(null));
		
		control.verify();
	}
	
	@Test
	public void testOnFinish_OrderFilled() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getInitialVolume()).andStubReturn(of(20L));
		expect(orderMock.getCurrentVolume()).andStubReturn(of(11L));
		control.replay();
		
		assertSame(service.getExit("OPEN"), service.onFinish(null));
		
		control.verify();
	}
	
	@Test
	public void testEnter_NothingToOpen() throws Exception {
		Speculation spec = new Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(0L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		state.setActiveSpeculation(spec);
		control.replay();
		
		assertSame(service.getExit("SKIPPED"), service.enter(tregMock));
		
		control.verify();
	}
	
	@Test
	public void testEnter_UnknownSignalType() throws Exception {
		Speculation spec = new Speculation(new S3TradeSignal(
				SignalType.NONE,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(10L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		state.setActiveSpeculation(spec);
		control.replay();
		
		assertSame(service.getExit("ERROR"), service.enter(tregMock));
		
		control.verify();
	}
	
	@Test
	public void testEnter_Buy() throws Exception {
		Speculation spec = new Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(10L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		state.setActiveSpeculation(spec);
		expect(terminalMock.createOrder(ACCOUNT, SYMBOL, OrderAction.BUY, of(10L), of("121.96"))).andReturn(orderMock);
		expect(terminalMock.getCurrentTime()).andReturn(T("2019-03-06T19:13:30Z"));
		expect(orderMock.onDone()).andReturn(eventTypeMock);
		tregMock.add(new SMTriggerOnEvent(eventTypeMock, new SMInput(service, new OnFinishAction(service))));
		tregMock.add(new SMTriggerOnTimer(terminalMock,
				T("2019-03-06T19:18:30Z"),
				new SMInput(service, new OnTimeoutAction(service))
			));
		terminalMock.placeOrder(orderMock);
		control.replay();
		
		assertNull(service.enter(tregMock));
		
		control.verify();
		assertSame(orderMock, service.getOrder());
	}
	
	@Test
	public void testEnter_Sell() throws Exception {
		Speculation spec = new Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(10L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		state.setActiveSpeculation(spec);
		expect(terminalMock.createOrder(ACCOUNT, SYMBOL, OrderAction.SELL, of(10L), of("119.96"))).andReturn(orderMock);
		expect(terminalMock.getCurrentTime()).andReturn(T("2019-03-06T19:13:30Z"));
		expect(orderMock.onDone()).andReturn(eventTypeMock);
		tregMock.add(new SMTriggerOnEvent(eventTypeMock, new SMInput(service, new OnFinishAction(service))));
		tregMock.add(new SMTriggerOnTimer(terminalMock,
				T("2019-03-06T19:18:30Z"),
				new SMInput(service, new OnTimeoutAction(service))
			));
		terminalMock.placeOrder(orderMock);
		control.replay();
		
		assertNull(service.enter(tregMock));
		
		control.verify();
		assertSame(orderMock, service.getOrder());
	}
	
	@Test
	public void testEnter_ErrorPlacingOrder() throws Exception {
		Speculation spec = new Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(10L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		state.setActiveSpeculation(spec);
		expect(terminalMock.createOrder(ACCOUNT, SYMBOL, OrderAction.SELL, of(10L), of("119.96"))).andReturn(orderMock);
		expect(terminalMock.getCurrentTime()).andReturn(T("2019-03-06T19:13:30Z"));
		expect(orderMock.onDone()).andReturn(eventTypeMock);
		tregMock.add(new SMTriggerOnEvent(eventTypeMock, new SMInput(service, new OnFinishAction(service))));
		tregMock.add(new SMTriggerOnTimer(terminalMock,
				T("2019-03-06T19:18:30Z"),
				new SMInput(service, new OnTimeoutAction(service))
			));
		terminalMock.placeOrder(orderMock);
		expectLastCall().andThrow(new OrderException("Test error"));
		control.replay();
		
		assertEquals(service.getExit("ERROR"), service.enter(tregMock));
		
		control.verify();
		assertSame(orderMock, service.getOrder());
	}
	
	@Test
	public void testExit_NothingFilled() throws Exception {
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(20L))
				.withToken(OrderField.CURRENT_VOLUME, of(20L))
				.buildUpdate());
		service.setOrder(order);
		control.replay();
		
		service.exit();
		
		control.verify();
	}
	
	@Test
	public void testExit_PartiallyFilled() throws Exception {
		Speculation spec = new Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(10L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		state.setActiveSpeculation(spec);
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(20L))
				.withToken(OrderField.CURRENT_VOLUME, of( 5L))
				.withToken(OrderField.TIME_DONE, T("2019-03-06T21:27:01Z"))
				.withToken(OrderField.EXECUTED_VALUE, of("575.10"))
				.buildUpdate());
		order.addExecution(1001L, "1002", T("2019-03-06T21:27:01Z"), of("19.17"), of(15L), of("575.10"));
		service.setOrder(order);
		rlistenerMock.speculationOpened();
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_enter = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.17"), of(15L), of("575.10"));
		assertEquals(expected_enter, spec.getEntryPoint());
		assertEquals(Speculation.SF_NEW, spec.getFlags());
		assertNull(spec.getResult());
	}
	
	@Test
	public void testExit_CompletelyFilled() throws Exception {
		Speculation spec = new Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(10L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		state.setActiveSpeculation(spec);
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(20L))
				.withToken(OrderField.CURRENT_VOLUME, of(0L))
				.withToken(OrderField.TIME_DONE, T("2019-03-06T21:27:01Z"))
				.withToken(OrderField.EXECUTED_VALUE, of("766.60"))
				.buildUpdate());
		order.addExecution(1000L, "1000", T("2019-03-06T21:27:00Z"), of("19.15"), of( 5L), of("191.50"));
		order.addExecution(1001L, "1002", T("2019-03-06T21:27:01Z"), of("19.17"), of(15L), of("575.10"));
		service.setOrder(order);
		rlistenerMock.speculationOpened();
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_enter = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.17"), of(20L), of("766.60"));
		assertEquals(expected_enter, spec.getEntryPoint());
		assertEquals(Speculation.SF_NEW, spec.getFlags());
		assertNull(spec.getResult());
	}

}
