package ru.prolib.bootes.lib.robo.s3;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.bootes.lib.cr.ContractParams;
import ru.prolib.bootes.lib.cr.ContractResolver;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalTrigger;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;
import ru.prolib.bootes.lib.rm.IRMContractStrategy;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.ISessionDataHandler;

public class S3RobotStateTest {
	private IMocksControl control;
	private S3RobotState service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new S3RobotState();
	}
	
	@Test
	public void testCtor0() {
		assertNotNull(service.getStateListener());
	}
	
	@Test
	public void testCtor1() {
		S3RobotStateListenerComp rslMock = control.createMock(S3RobotStateListenerComp.class);
		service = new S3RobotState(rslMock);
		assertSame(rslMock, service.getStateListener());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetAccount_ThrowsIfNotDefined() {
		service.getAccount();
	}
	
	@Test
	public void testGetAccount() {
		Account expected = new Account("foo", "bar");
		service.setAccount(expected);
		assertSame(expected, service.getAccount());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetPortfolio_ThrowsIfNotDefined() {
		service.getPortfolio();
	}
	
	@Test
	public void testGetPortfolio() {
		Portfolio expected = control.createMock(Portfolio.class);
		service.setPortfolio(expected);
		assertSame(expected, service.getPortfolio());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetContractResolver_ThrowsIfNotDefined() {
		service.getContractResolver();
	}
	
	@Test
	public void testGetContractResolver() {
		ContractResolver expected = control.createMock(ContractResolver.class);
		service.setContractResolver(expected);
		assertSame(expected, service.getContractResolver());
	}
	
	@Test
	public void testGetContractParamsOrNull() {
		assertNull(service.getContractParamsOrNull());
		ContractParams expected = control.createMock(ContractParams.class);
		service.setContractParams(expected);
		assertSame(expected, service.getContractParams());
		service.setContractParams(null);
		assertNull(service.getContractParamsOrNull());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetContractParams_ThrowsIfNotDefined() {
		service.getContractParams();
	}
	
	@Test
	public void testGetContractParams() {
		ContractParams expected = control.createMock(ContractParams.class);
		service.setContractParams(expected);
		assertSame(expected, service.getContractParams());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSecurity_ThrowsIfNotDefined() {
		service.getSecurity();
	}
	
	@Test
	public void testGetSecurity() {
		Security expected = control.createMock(Security.class);
		service.setSecurity(expected);
		assertSame(expected, service.getSecurity());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetActiveSpeculation_ThrowsIfNotDefined() {
		service.getActiveSpeculation();
	}
	
	@Test
	public void testGetActiveSpeculation() {
		S3Speculation expected = control.createMock(S3Speculation.class);
		service.setActiveSpeculation(expected);
		assertSame(expected, service.getActiveSpeculation());
	}
	
	@Test
	public void testIsSpeculationActive() {
		S3Speculation specMock = control.createMock(S3Speculation.class);
		assertFalse(service.isSpeculationActive());
		service.setActiveSpeculation(specMock);
		assertTrue(service.isSpeculationActive());
		service.setActiveSpeculation(null);
		assertFalse(service.isSpeculationActive());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetRobotTitle_ThrowsIfNotDefined() {
		service.getRobotTitle();
	}
	
	@Test
	public void testGetRobotTitle() {
		service.setRobotTitle("Bender-26");
		assertEquals("Bender-26", service.getRobotTitle());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSessionDataHandler_ThrowsIfNotDefined() {
		service.getSessionDataHandler();
	}

	@Test
	public void testGetSessionDataHandler() {
		ISessionDataHandler expected = control.createMock(ISessionDataHandler.class);
		service.setSessionDataHandler(expected);
		assertSame(expected, service.getSessionDataHandler());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSignalTrigger_ThrowsIfNotDefined() {
		service.getSignalTrigger();
	}
	
	@Test
	public void testgetSignalTrigger() {
		SignalTrigger expected = control.createMock(SignalTrigger.class);
		service.setSignalTrigger(expected);
		assertSame(expected, service.getSignalTrigger());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSignalFilter_ThrowsIfNotDefined() {
		service.getSignalFilter();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetSignalFilter() {
		IFilterSet<S3TradeSignal> expected = control.createMock(IFilterSet.class);
		service.setSignalFilter(expected);
		assertSame(expected, service.getSignalFilter());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetContractStrategy_ThrowsIfNotDefined() {
		service.getContractStrategy();
	}
	
	@Test
	public void testGetContractStrategy() {
		IRMContractStrategy expected = control.createMock(IRMContractStrategy.class);
		service.setContractStrategy(expected);
		assertSame(expected, service.getContractStrategy());
	}
	
	@Test
	public void testGetPositionParamsOrNull() {
		RMContractStrategyPositionParams expected = control.createMock(RMContractStrategyPositionParams.class);
		assertNull(service.getPositionParamsOrNull());
		service.setPositionParams(expected);
		assertSame(expected, service.getPositionParamsOrNull());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetPositionParams_ThrowsIfNotDefined() {
		service.getPositionParams();
	}
	
	@Test
	public void testGetPositionParams() {
		RMContractStrategyPositionParams expected = control.createMock(RMContractStrategyPositionParams.class);
		service.setPositionParams(expected);
		assertSame(expected, service.getPositionParams());
	}
	
	@Test
	public void testGetContractSubscrHandler() {
		SubscrHandler actual = service.getContractSubscrHandler();
		
		assertNull(actual);
		
		SubscrHandler expected = control.createMock(SubscrHandler.class);
		service.setContractSubscrHandler(expected);
		
		actual = service.getContractSubscrHandler();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLastOrder() {
		assertNull(service.getLastOrder());
		
		Order orderMock = control.createMock(Order.class);
		
		service.setLastOrder(orderMock);
		assertSame(orderMock, service.getLastOrder());
	}

}
