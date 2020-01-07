package ru.prolib.bootes.lib.robo.s3;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;

import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
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
import ru.prolib.bootes.lib.cr.ContractParams;
import ru.prolib.bootes.lib.cr.ContractResolver;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.rm.IRMContractStrategy;
import ru.prolib.bootes.lib.robo.s3.S3ClosePosition;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3Speculative;

public class S3ClosePositionTest {
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
	
	public static class StateStub implements IS3Speculative {
		private S3RobotStateListener listener;
		private Portfolio portfolio;
		private Security security;
		private S3Speculation speculation;
		
		StateStub(S3RobotStateListener listener) {
			this.listener = listener;
		}

		@Override
		public S3RobotStateListener getStateListener() {
			return listener;
		}

		@Override
		public Account getAccount() {
			return portfolio.getAccount();
		}

		@Override
		public void setPortfolio(Portfolio portfolio) {
			this.portfolio = portfolio;
		}

		@Override
		public Portfolio getPortfolio() {
			return portfolio;
		}

		@Override
		public ContractResolver getContractResolver() {
			throw new IllegalStateException("Operation not supported");
		}

		@Override
		public ContractParams getContractParamsOrNull() {
			throw new IllegalStateException("Operation not supported");
		}

		@Override
		public ContractParams getContractParams() {
			throw new IllegalStateException("Operation not supported");
		}

		@Override
		public void setContractParams(ContractParams params) {
			throw new IllegalStateException("Operation not supported");
		}

		@Override
		public void setSecurity(Security security) {
			this.security = security;
		}

		@Override
		public Security getSecurity() {
			return security;
		}

		@Override
		public S3Speculation getActiveSpeculation() {
			return speculation;
		}

		@Override
		public void setActiveSpeculation(S3Speculation spec) {
			this.speculation = spec;
		}

		@Override
		public IRMContractStrategy getContractStrategy() {
			throw new IllegalStateException("Operation not supported");
		}

		@Override
		public SubscrHandler getContractSubscrHandler() {
			throw new IllegalStateException("Operation not supported");
		}

		@Override
		public void setContractSubscrHandler(SubscrHandler handler) {
			throw new IllegalStateException("Operation not supported");
		}
		
	}
	
	private IMocksControl control;
	private EditableTerminal terminalMock, terminal;
	private EditableSecurity security;
	private EditableOrder orderMock;
	private S3RobotStateListener rlistenerMock;
	private SMTriggerRegistry tregMock;
	private EventType eventTypeMock;
	private AppServiceLocator serviceLocator;
	private IS3Speculative state;
	private S3ClosePosition service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(EditableTerminal.class);
		orderMock = control.createMock(EditableOrder.class);
		tregMock = control.createMock(SMTriggerRegistry.class);
		eventTypeMock = control.createMock(EventType.class);
		rlistenerMock = control.createMock(S3RobotStateListener.class);
		state = new StateStub(rlistenerMock);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		state.setPortfolio(terminal.getEditablePortfolio(ACCOUNT));
		state.setSecurity(security = terminal.getEditableSecurity(SYMBOL));
		security.consume(new DeltaUpdateBuilder()
				.withToken(SecurityField.UPPER_PRICE_LIMIT, of("190.05"))
				.withToken(SecurityField.LOWER_PRICE_LIMIT, of("105.19"))
				.buildUpdate());
		serviceLocator = new AppServiceLocator(null);
		serviceLocator.setTerminal(terminalMock);
		service = new S3ClosePosition(serviceLocator, state);
	}
	
	@Test
	public void testCtor2() {
		assertSame(service, service.getEnterAction());
		assertSame(service, service.getExitAction());
		
		assertEquals(4, service.getExits().size());
		assertNotNull(service.getExit("CLOSED"));
		assertNotNull(service.getExit("NEED_CLOSE"));
		assertNotNull(service.getExit("ERROR"));
		assertNotNull(service.getExit("INTERRUPT"));
		
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
		expect(orderMock.getCurrentVolume()).andReturn(of(10L));
		control.replay();
		
		assertSame(service.getExit("NEED_CLOSE"), service.onFinish(null));
		
		control.verify();
	}
	
	@Test
	public void testOnFinish_OrderFilled() throws Exception {
		service.setOrder(orderMock);
		expect(orderMock.getCurrentVolume()).andReturn(of(0L));
		control.replay();
		
		assertSame(service.getExit("CLOSED"), service.onFinish(null));
		
		control.verify();
	}
	
	@Test
	public void testEnter_AlreadyClosed() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T19:13:00Z"), of("120.98"), of(50L), of("243.61")));
		spec.setExitPoint(Tick.of(TickType.TRADE, T("2019-03-06T20:15:00Z"), of("130.98"), of(50L), of("260.29")));
		state.setActiveSpeculation(spec);
		control.replay();
		
		assertSame(service.getExit("CLOSED"), service.enter(tregMock));
		
		control.verify();
	}
	
	@Test
	public void testEnter_UnknownSignalType() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.NONE,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T19:13:00Z"), of("120.98"), of(50L), of("243.61")));
		state.setActiveSpeculation(spec);
		control.replay();
		
		assertSame(service.getExit("ERROR"), service.enter(tregMock));
		
		control.verify();
	}
	
	@Test
	public void testEnter_Buy_FirstTryClose() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T19:13:00Z"), of("120.98"), of(50L), of("243.61")));
		state.setActiveSpeculation(spec);
		expect(terminalMock.createOrder(ACCOUNT, SYMBOL, OrderAction.SELL, of(50L), of("105.19"))).andReturn(orderMock);
		expect(terminalMock.getCurrentTime()).andReturn(T("2019-03-06T20:50:00Z"));
		expect(orderMock.onDone()).andReturn(eventTypeMock);
		Capture<SMTriggerOnEvent> cap1 = Capture.newInstance();
		Capture<SMTriggerOnTimer> cap2 = Capture.newInstance();
		tregMock.add(capture(cap1));
		tregMock.add(capture(cap2));
		terminalMock.placeOrder(orderMock);
		control.replay();
		
		assertNull(service.enter(tregMock));
		
		control.verify();
		assertSame(orderMock, service.getOrder());
		assertTrue(new SMTriggerOnEvent(eventTypeMock, new SMInput(service, new OnFinishAction(service))).isEqualTo(cap1.getValue()));
		assertTrue(new SMTriggerOnTimer(terminalMock, T("2019-03-06T20:55:00Z"), new SMInput(service, new OnTimeoutAction(service))).isEqualTo(cap2.getValue()));
	}

	@Test
	public void testEnter_Buy_ClosePartial() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T19:13:00Z"), of("120.98"), of(50L), of("243.61")));
		spec.setExitPoint(Tick.of(TickType.TRADE, T("2019-03-06T20:15:00Z"), of("130.98"), of(30L), of("260.29")));
		state.setActiveSpeculation(spec);
		expect(terminalMock.createOrder(ACCOUNT, SYMBOL, OrderAction.SELL, of(20L), of("105.19"))).andReturn(orderMock);
		expect(terminalMock.getCurrentTime()).andReturn(T("2019-03-06T20:50:00Z"));
		expect(orderMock.onDone()).andReturn(eventTypeMock);
		Capture<SMTriggerOnEvent> cap1 = Capture.newInstance();
		Capture<SMTriggerOnTimer> cap2 = Capture.newInstance();
		tregMock.add(capture(cap1));
		tregMock.add(capture(cap2));		
		terminalMock.placeOrder(orderMock);
		control.replay();
		
		assertNull(service.enter(tregMock));
		
		control.verify();
		assertSame(orderMock, service.getOrder());
		assertTrue(new SMTriggerOnEvent(eventTypeMock, new SMInput(service, new OnFinishAction(service))).isEqualTo(cap1.getValue()));
		assertTrue(new SMTriggerOnTimer(terminalMock, T("2019-03-06T20:55:00Z"), new SMInput(service, new OnTimeoutAction(service))).isEqualTo(cap2.getValue()));
	}

	@Test
	public void testEnter_Sell_FirstTryClose() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T19:13:00Z"), of("120.55"), of(20L), of("237.92")));
		state.setActiveSpeculation(spec);
		expect(terminalMock.createOrder(ACCOUNT, SYMBOL, OrderAction.BUY, of(20L), of("190.05"))).andReturn(orderMock);
		expect(terminalMock.getCurrentTime()).andReturn(T("2019-03-06T19:25:15Z"));
		expect(orderMock.onDone()).andReturn(eventTypeMock);
		Capture<SMTriggerOnEvent> cap1 = Capture.newInstance();
		Capture<SMTriggerOnTimer> cap2 = Capture.newInstance();
		tregMock.add(capture(cap1));
		tregMock.add(capture(cap2));		
		terminalMock.placeOrder(orderMock);
		control.replay();
		
		assertNull(service.enter(tregMock));
		
		control.verify();
		assertSame(orderMock, service.getOrder());

		assertTrue(new SMTriggerOnEvent(eventTypeMock, new SMInput(service, new OnFinishAction(service))).isEqualTo(cap1.getValue()));
		assertTrue(new SMTriggerOnTimer(terminalMock, T("2019-03-06T19:30:15Z"), new SMInput(service, new OnTimeoutAction(service))).isEqualTo(cap2.getValue()));
	}
	
	@Test
	public void testEnter_Sell_ClosePartial() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T19:13:00Z"), of("120.55"), of(20L), of("237.92")));
		spec.setExitPoint(Tick.of(TickType.TRADE, T("2019-03-06T19:20:00Z"), of("115.04"), of(5L), of("104.17")));
		state.setActiveSpeculation(spec);
		expect(terminalMock.createOrder(ACCOUNT, SYMBOL, OrderAction.BUY, of(15L), of("190.05"))).andReturn(orderMock);
		expect(terminalMock.getCurrentTime()).andReturn(T("2019-03-06T19:25:15Z"));
		expect(orderMock.onDone()).andReturn(eventTypeMock);
		Capture<SMTriggerOnEvent> cap1 = Capture.newInstance();
		Capture<SMTriggerOnTimer> cap2 = Capture.newInstance();
		tregMock.add(capture(cap1));
		tregMock.add(capture(cap2));		
		terminalMock.placeOrder(orderMock);
		control.replay();
		
		assertNull(service.enter(tregMock));
		
		control.verify();
		assertSame(orderMock, service.getOrder());
		assertTrue(new SMTriggerOnEvent(eventTypeMock, new SMInput(service, new OnFinishAction(service))).isEqualTo(cap1.getValue()));
		assertTrue(new SMTriggerOnTimer(terminalMock, T("2019-03-06T19:30:15Z"), new SMInput(service, new OnTimeoutAction(service))).isEqualTo(cap2.getValue()));
	}
	
	@Test
	public void testEnter_ErrorPlacingOrder() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T19:13:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T19:13:00Z"), of("120.55"), of(20L), of("237.92")));
		state.setActiveSpeculation(spec);
		expect(terminalMock.createOrder(ACCOUNT, SYMBOL, OrderAction.BUY, of(20L), of("190.05"))).andReturn(orderMock);
		expect(terminalMock.getCurrentTime()).andReturn(T("2019-03-06T19:25:15Z"));
		expect(orderMock.onDone()).andReturn(eventTypeMock);
		Capture<SMTriggerOnEvent> cap1 = Capture.newInstance();
		Capture<SMTriggerOnTimer> cap2 = Capture.newInstance();
		tregMock.add(capture(cap1));
		tregMock.add(capture(cap2));		
		terminalMock.placeOrder(orderMock);
		expectLastCall().andThrow(new OrderException("Test error"));
		control.replay();
		
		assertSame(service.getExit("ERROR"), service.enter(tregMock));
		
		control.verify();
		assertSame(orderMock, service.getOrder());
		assertTrue(new SMTriggerOnEvent(eventTypeMock, new SMInput(service, new OnFinishAction(service))).isEqualTo(cap1.getValue()));
		assertTrue(new SMTriggerOnTimer(terminalMock, T("2019-03-06T19:30:15Z"), new SMInput(service, new OnTimeoutAction(service))).isEqualTo(cap2.getValue()));
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
	public void testExit_Buy_FirstTryClose_ClosedAll() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T21:20:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:20:03Z"), of("19.03"), of(20L), of("761.20")));
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
		rlistenerMock.speculationClosed();
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_exit = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.17"), of(20L), of("766.60"));
		assertEquals(expected_exit, spec.getExitPoint());
		assertEquals(S3Speculation.SF_STATUS_CLOSED, spec.getFlags());
		assertEquals(of("5.40"), spec.getResult());
	}
	
	@Test
	public void testExit_Buy_FirstTryClose_ClosedPartially() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T21:20:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:20:03Z"), of("19.03"), of(20L), of("761.20")));
		state.setActiveSpeculation(spec);
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(20L))
				.withToken(OrderField.CURRENT_VOLUME, of(10L))
				.withToken(OrderField.TIME_DONE, T("2019-03-06T21:27:01Z"))
				.withToken(OrderField.EXECUTED_VALUE, of("383.20"))
				.buildUpdate());
		order.addExecution(1000L, "1000", T("2019-03-06T21:27:00Z"), of("19.15"), of( 5L), of("191.50"));
		order.addExecution(1001L, "1002", T("2019-03-06T21:27:01Z"), of("19.17"), of( 5L), of("191.70"));
		service.setOrder(order);
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_exit = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.16"), of(10L), of("383.20"));
		assertEquals(expected_exit, spec.getExitPoint());
		assertEquals(S3Speculation.SF_NEW, spec.getFlags());
		assertNull(spec.getResult());
	}
	
	@Test
	public void testExit_Buy_SequentialClose_ClosedAll() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T21:20:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:20:03Z"), of("19.03"), of(20L), of("761.20")));
		spec.setExitPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:26:55Z"), of("19.05"), of(10L), of("381.00")));
		state.setActiveSpeculation(spec);
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(10L))
				.withToken(OrderField.CURRENT_VOLUME, of(0L))
				.withToken(OrderField.TIME_DONE, T("2019-03-06T21:27:01Z"))
				.withToken(OrderField.EXECUTED_VALUE, of("383.00"))
				.buildUpdate());
		order.addExecution(1000L, "1000", T("2019-03-06T21:27:00Z"), of("19.15"), of(10L), of("383.00"));
		service.setOrder(order);
		rlistenerMock.speculationClosed();
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_exit = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.10"), of(20L), of("764.00"));
		assertEquals(expected_exit, spec.getExitPoint());
		assertEquals(S3Speculation.SF_STATUS_CLOSED, spec.getFlags());
		assertEquals(of("2.80"), spec.getResult());
	}

	@Test
	public void testExit_Buy_SequentialClose_ClosedPartially() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.BUY,
				T("2019-03-06T21:20:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:20:03Z"), of("19.03"), of(20L), of("761.20")));
		spec.setExitPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:26:55Z"), of("19.05"), of(10L), of("381.00")));
		state.setActiveSpeculation(spec);
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(10L))
				.withToken(OrderField.CURRENT_VOLUME, of(5L))
				.withToken(OrderField.TIME_DONE, T("2019-03-06T21:27:01Z"))
				.withToken(OrderField.EXECUTED_VALUE, of("190.20"))
				.buildUpdate());
		order.addExecution(1000L, "1000", T("2019-03-06T21:27:00Z"), of("19.02"), of(5L), of("190.20"));
		service.setOrder(order);
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_exit = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.04"), of(15L), of("571.20"));
		assertEquals(expected_exit, spec.getExitPoint());
		assertEquals(S3Speculation.SF_NEW, spec.getFlags());
		assertNull(spec.getResult());
	}

	@Test
	public void testExit_Sell_FirstTryClose_ClosedAll() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T21:20:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:20:03Z"), of("19.03"), of(20L), of("761.20")));
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
		rlistenerMock.speculationClosed();
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_exit = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.17"), of(20L), of("766.60"));
		assertEquals(expected_exit, spec.getExitPoint());
		assertEquals(S3Speculation.SF_STATUS_CLOSED, spec.getFlags());
		assertEquals(of("-5.40"), spec.getResult());
	}
	
	@Test
	public void testExit_Sell_FirstTryClose_ClosedPartially() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T21:20:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:20:03Z"), of("19.03"), of(20L), of("761.20")));
		state.setActiveSpeculation(spec);
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(20L))
				.withToken(OrderField.CURRENT_VOLUME, of(15L))
				.withToken(OrderField.TIME_DONE, T("2019-03-06T21:27:01Z"))
				.withToken(OrderField.EXECUTED_VALUE, of("191.50"))
				.buildUpdate());
		order.addExecution(1000L, "1000", T("2019-03-06T21:27:00Z"), of("19.15"), of( 5L), of("191.50"));
		service.setOrder(order);
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_exit = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.15"), of(5L), of("191.50"));
		assertEquals(expected_exit, spec.getExitPoint());
		assertEquals(S3Speculation.SF_NEW, spec.getFlags());
		assertNull(spec.getResult());
	}
	
	@Test
	public void testExit_Sell_SequentialClose_ClosedAll() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T21:20:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:20:03Z"), of("19.03"), of(20L), of("761.20")));
		spec.setExitPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:25:00Z"), of("19.27"), of(10L), of("385.40")));
		state.setActiveSpeculation(spec);
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(10L))
				.withToken(OrderField.CURRENT_VOLUME, of(0L))
				.withToken(OrderField.TIME_DONE, T("2019-03-06T21:27:01Z"))
				.withToken(OrderField.EXECUTED_VALUE, of("382.20"))
				.buildUpdate());
		order.addExecution(1000L, "1000", T("2019-03-06T21:27:00Z"), of("19.11"), of(10L), of("382.20"));
		service.setOrder(order);
		rlistenerMock.speculationClosed();
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_exit = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.19"), of(20L), of("767.60"));
		assertEquals(expected_exit, spec.getExitPoint());
		assertEquals(S3Speculation.SF_STATUS_CLOSED, spec.getFlags());
		assertEquals(of("-6.40"), spec.getResult());
	}
	
	@Test
	public void testExit_Sell_SequentialClose_ClosedPartially() throws Exception {
		S3Speculation spec = new S3Speculation(new S3TradeSignal(
				SignalType.SELL,
				T("2019-03-06T21:20:00Z"),
				of("120.96"),
				of(50L),
				of("13.00"),
				of("5.00"),
				of("1.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			));
		spec.setEntryPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:20:03Z"), of("19.03"), of(20L), of("761.20")));
		spec.setExitPoint(Tick.of(TickType.TRADE, T("2019-03-06T21:25:00Z"), of("19.27"), of(10L), of("385.40")));
		state.setActiveSpeculation(spec);
		EditableOrder order = terminal.createOrder(ACCOUNT, SYMBOL);
		order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.INITIAL_VOLUME, of(10L))
				.withToken(OrderField.CURRENT_VOLUME, of(5L))
				.withToken(OrderField.TIME_DONE, T("2019-03-06T21:27:01Z"))
				.withToken(OrderField.EXECUTED_VALUE, of("191.10"))
				.buildUpdate());
		order.addExecution(1000L, "1000", T("2019-03-06T21:27:00Z"), of("19.11"), of(5L), of("191.10"));
		service.setOrder(order);
		control.replay();
		
		service.exit();
		
		control.verify();
		Tick expected_exit = Tick.of(TickType.TRADE, T("2019-03-06T21:27:01Z"), of("19.22"), of(15L), of("576.50"));
		assertEquals(expected_exit, spec.getExitPoint());
		assertEquals(S3Speculation.SF_NEW, spec.getFlags());
		assertNull(spec.getResult());
	}

}
