package ru.prolib.bootes.lib.robo.sh;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.aquila.core.BusinessEntities.OrderAction.*;
import static ru.prolib.aquila.core.BusinessEntities.OrderType.*;

import java.time.Instant;

import org.apache.log4j.BasicConfigurator;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.sm.OnFinishAction;
import ru.prolib.aquila.core.sm.OnTimeoutAction;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerOnTimer;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.RobotStateListener;

public class BOOTESExecuteOrderTest {
	static Account ACCOUNT = new Account("TEST");
	static Symbol SYMBOL = new Symbol("AQLA");

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}

	IMocksControl control;
	DataProvider dpMock;
	SMTriggerRegistry tregMock;
	RobotStateListener stateListenerMock;
	SchedulerStub schedulerStub;
	EditableTerminal terminal;
	AppServiceLocator serviceLocator;
	BOOTESExecuteOrder service;
	OrderDefinitionBuilder builder;
	Capture<SMTriggerOnEvent> toe_cap;
	Capture<SMTriggerOnTimer> tot_cap;
	Capture<EditableOrder> order_cap;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dpMock = control.createMock(DataProvider.class);
		tregMock = control.createMock(SMTriggerRegistry.class);
		stateListenerMock = control.createMock(RobotStateListener.class);
		schedulerStub = new SchedulerStub();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(dpMock)
				.withScheduler(schedulerStub)
				.buildTerminal();
		terminal.getEditablePortfolio(ACCOUNT);
		terminal.getEditableSecurity(SYMBOL);
		serviceLocator = new AppServiceLocator(null);
		serviceLocator.setTerminal(terminal);
		service = new BOOTESExecuteOrder(serviceLocator, stateListenerMock);
		builder = new OrderDefinitionBuilder()
				.withAccount(ACCOUNT)
				.withSymbol(SYMBOL)
				.withMaxExecutionTime(1000L);
		toe_cap = Capture.newInstance();
		tot_cap = Capture.newInstance();
		order_cap = Capture.newInstance();
	}
	
	void assertTriggerOnEvent(EventType expected_type, SMTriggerOnEvent trigger) {
		assertTrue(new SMTriggerOnEvent(expected_type, new SMInput(service, new OnFinishAction(service)))
				.isEqualTo(trigger));
	}
	
	void assertTriggerOnTimer(String time_string, SMTriggerOnTimer trigger) {
		assertTrue(new SMTriggerOnTimer(terminal, T(time_string),
				new SMInput(service, new OnTimeoutAction(service))).isEqualTo(trigger));
	}
	
	@Test
	public void testEnter_ErrorIfZeroQty() throws Exception {
		service.setIncomingData(builder.withLimitBuy(of(0L), of(120L)).buildDefinition());
		control.replay();
		
		SMExit actual = service.enter(tregMock);
		
		control.verify();
		assertEquals(service.getExit("ERROR"), actual);
	}
	
	@Test
	public void testEnter_ErrorIfOrderFailed() throws Exception {
		schedulerStub.setFixedTime("2020-04-07T18:35:00Z");
		service.setIncomingData(builder.withLimitBuy(of(10L), of("45.93")).buildDefinition());
		expect(dpMock.getNextOrderID()).andReturn(450278L);
		tregMock.add(capture(toe_cap));
		tregMock.add(capture(tot_cap));
		dpMock.registerNewOrder(capture(order_cap));
		expectLastCall().andThrow(new OrderException("Test error"));
		control.replay();
		
		SMExit actual = service.enter(tregMock);
		
		control.verify();
		assertEquals(service.getExit("ERROR"), actual);
		Order actual_order = service.getResultData();
		assertTriggerOnEvent(actual_order.onDone(), toe_cap.getValue());
		assertTriggerOnTimer("2020-04-07T18:35:01Z", tot_cap.getValue());
		assertEquals(ACCOUNT, actual_order.getAccount());
		assertEquals(SYMBOL, actual_order.getSymbol());
		assertEquals(450278L, actual_order.getID());
		assertEquals(new DeltaUpdateBuilder()
				.withToken(OrderField.TIME, T("2020-04-07T18:35:00Z"))
				.withToken(OrderField.ACTION, BUY)
				.withToken(OrderField.TYPE, LMT)
				.withToken(OrderField.PRICE, of("45.93"))
				.withToken(OrderField.INITIAL_VOLUME, of(10L))
				.withToken(OrderField.CURRENT_VOLUME, of(10L))
				.withToken(OrderField.STATUS, OrderStatus.PENDING)
				.buildUpdate().getContents(), actual_order.getContents());
	}
	
	@Test
	public void testEnter_OK() throws Exception {
		schedulerStub.setFixedTime("2020-04-07T12:45:05Z");
		service.setIncomingData(builder.withMarketSell(of(20L)).buildDefinition());
		expect(dpMock.getNextOrderID()).andReturn(600987L);
		tregMock.add(capture(toe_cap));
		tregMock.add(capture(tot_cap));
		dpMock.registerNewOrder(capture(order_cap));
		control.replay();
		
		SMExit actual = service.enter(tregMock);
		
		control.verify();
		assertNull(actual);
		Order actual_order = service.getResultData();
		assertTriggerOnEvent(actual_order.onDone(), toe_cap.getValue());
		assertTriggerOnTimer("2020-04-07T12:45:06Z", tot_cap.getValue());
		assertEquals(ACCOUNT, actual_order.getAccount());
		assertEquals(SYMBOL, actual_order.getSymbol());
		assertEquals(600987L, actual_order.getID());
		assertEquals(new DeltaUpdateBuilder()
				.withToken(OrderField.TIME, T("2020-04-07T12:45:05Z"))
				.withToken(OrderField.ACTION, SELL)
				.withToken(OrderField.TYPE, MKT)
				.withToken(OrderField.INITIAL_VOLUME, of(20L))
				.withToken(OrderField.CURRENT_VOLUME, of(20L))
				.withToken(OrderField.STATUS, OrderStatus.PENDING)
				.buildUpdate().getContents(), actual_order.getContents());
	}
	
	@Test
	public void testOnInterrupt_SkipIfOrderNotDefined() throws Exception {
		control.replay();
		
		SMExit actual = service.onInterrupt(null);
		
		control.verify();
		assertEquals(service.getExit("INTERRUPT"), actual);
	}

	@Test
	public void testOnInterrupt_SkipIfOrderInFinalState() throws Exception {
		EditableOrder order = terminal.createOrder(440761L, ACCOUNT, SYMBOL);
		order.update(OrderField.STATUS, OrderStatus.CANCELLED);
		service.setResultData(order);
		control.replay();
		
		SMExit actual = service.onInterrupt(null);
		
		control.verify();
		assertEquals(service.getExit("INTERRUPT"), actual);
	}
	
	@Test
	public void testOnInterrupt_ErrorIfCancelFailed() throws Exception {
		EditableOrder order = terminal.createOrder(440761L, ACCOUNT, SYMBOL);
		order.update(OrderField.STATUS, OrderStatus.ACTIVE);
		service.setResultData(order);
		dpMock.cancelOrder(order);
		expectLastCall().andThrow(new OrderException("Test error"));
		control.replay();
		
		SMExit actual = service.onInterrupt(null);
		
		control.verify();
		assertEquals(service.getExit("ERROR"), actual);
	}
	
	@Test
	public void testOnInterrupt_OK() throws Exception {
		EditableOrder order = terminal.createOrder(112345L, ACCOUNT, SYMBOL);
		order.update(OrderField.STATUS, OrderStatus.ACTIVE);
		service.setResultData(order);
		dpMock.cancelOrder(order);
		control.replay();
		
		SMExit actual = service.onInterrupt(null);
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testOnTimeout_ImmediatelyIfOrderNotDefined() throws Exception {
		control.replay();
		
		SMExit actual = service.onTimeout(null);
		
		control.verify();
		assertEquals(service.getExit("TIMEOUT"), actual);
	}
	
	@Test
	public void testOnTimeout_ImmediatelyIfOrderInFinalState() throws Exception {
		EditableOrder order = terminal.createOrder(176213L, ACCOUNT, SYMBOL);
		order.update(OrderField.STATUS, OrderStatus.CANCELLED);
		service.setResultData(order);
		control.replay();
		
		SMExit actual = service.onTimeout(null);
		
		control.verify();
		assertEquals(service.getExit("TIMEOUT"), actual);
	}
	
	@Test
	public void testOnTimeout_ImmediatelyIfCancelFailed() throws Exception {
		EditableOrder order = terminal.createOrder(176213L, ACCOUNT, SYMBOL);
		order.update(OrderField.STATUS, OrderStatus.ACTIVE);
		service.setResultData(order);
		dpMock.cancelOrder(order);
		expectLastCall().andThrow(new OrderException("Test error"));
		control.replay();
		
		SMExit actual = service.onTimeout(null);
		
		control.verify();
		assertEquals(service.getExit("ERROR"), actual);
	}
	
	@Test
	public void testOnTimeout_OK() throws Exception {
		EditableOrder order = terminal.createOrder(176213L, ACCOUNT, SYMBOL);
		order.update(OrderField.STATUS, OrderStatus.ACTIVE);
		service.setResultData(order);
		dpMock.cancelOrder(order);
		control.replay();
		
		SMExit actual = service.onTimeout(null);
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testOnFinish_Interrupt() throws Exception {
		EditableOrder order = terminal.createOrder(267812L, ACCOUNT, SYMBOL);
		order.update(OrderField.STATUS, OrderStatus.CANCELLED);
		service.setResultData(order);
		control.replay();
		service.onInterrupt(null);
		
		SMExit actual = service.onFinish(null);
		
		control.verify();
		assertEquals(service.getExit("INTERRUPT"), actual);
	}
	
	@Test
	public void testOnFinish_Timeout() throws Exception {
		EditableOrder order = terminal.createOrder(267812L, ACCOUNT, SYMBOL);
		order.update(OrderField.STATUS, OrderStatus.CANCELLED);
		order.update(OrderField.INITIAL_VOLUME, of(35L));
		order.update(OrderField.CURRENT_VOLUME, of(35L));
		service.setResultData(order);
		control.replay();
		service.onTimeout(null);
		
		SMExit actual = service.onFinish(null);
		
		control.verify();
		assertEquals(service.getExit("TIMEOUT"), actual);
	}
	
	@Test
	public void testOnFinish_NoneExecuted() throws Exception {
		EditableOrder order = terminal.createOrder(267812L, ACCOUNT, SYMBOL);
		order.update(OrderField.INITIAL_VOLUME, of(35L));
		order.update(OrderField.CURRENT_VOLUME, of(35L));
		service.setResultData(order);
		control.replay();

		SMExit actual = service.onFinish(null);
		
		control.verify();
		assertEquals(service.getExit("EXEC_NONE"), actual);
	}
	
	@Test
	public void testOnFinish_FullyExecuted() throws Exception {
		EditableOrder order = terminal.createOrder(267812L, ACCOUNT, SYMBOL);
		order.update(OrderField.INITIAL_VOLUME, of(35L));
		order.update(OrderField.CURRENT_VOLUME, of( 0L));
		service.setResultData(order);
		control.replay();

		SMExit actual = service.onFinish(null);
		
		control.verify();
		assertEquals(service.getExit("EXEC_FULL"), actual);
	}
	
	@Test
	public void testOnFinish_PartiallyExecuted() throws Exception {
		EditableOrder order = terminal.createOrder(267812L, ACCOUNT, SYMBOL);
		order.update(OrderField.INITIAL_VOLUME, of(35L));
		order.update(OrderField.CURRENT_VOLUME, of(10L));
		service.setResultData(order);
		control.replay();

		SMExit actual = service.onFinish(null);
		
		control.verify();
		assertEquals(service.getExit("EXEC_PART"), actual);
	}
	
	@Test
	public void testExit_DoNotNotifyRobotListenerIfNoStateListener() throws Exception {
		service = new BOOTESExecuteOrder(serviceLocator);
		control.replay();
		
		service.exit();
		
		control.verify();
	}
	
	@Test
	public void testExit_DoNotNotifyRobotListenerIfNoOrder() throws Exception {
		control.replay();
		
		service.exit();
		
		control.verify();
	}
	
	@Test
	public void testExit_NotifyRobotListener() throws Exception {
		EditableOrder order = terminal.createOrder(887612L, ACCOUNT, SYMBOL);
		service.setResultData(order);
		stateListenerMock.orderFinished(order);
		control.replay();
		
		service.exit();
		
		control.verify();
	}

}
