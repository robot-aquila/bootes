package ru.prolib.bootes.tsgr001a.robot;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;

public class RobotStateTest {
	private IMocksControl control;
	private RobotStateListener stateListenerStub;
	private RobotState service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		stateListenerStub = new RobotStateListenerStub();
		service = new RobotState(stateListenerStub);
	}
	
	@Test
	public void testCtor() {
		assertSame(stateListenerStub, service.getStateListener());
	}

	@Test
	public void testSettersAndGetters() {
		ContractResolver crMock = control.createMock(ContractResolver.class);
		ContractParams cpMock = control.createMock(ContractParams.class);
		Portfolio portfolioMock = control.createMock(Portfolio.class);
		Security securityMock = control.createMock(Security.class);
		STSeriesHandler sht0Mock = control.createMock(STSeriesHandler.class);
		STSeriesHandler sht1Mock = control.createMock(STSeriesHandler.class);
		STSeriesHandler sht2Mock = control.createMock(STSeriesHandler.class);
		
		service.setContractName("RTS");
		service.setAccountCode("ZX-48");
		service.setContractResolver(crMock);
		service.setContractParams(cpMock);
		service.setPortfolio(portfolioMock);
		service.setSecurity(securityMock);
		service.setSeriesHandlerT0(sht0Mock);
		service.setSeriesHandlerT1(sht1Mock);
		service.setSeriesHandlerT2(sht2Mock);
		
		assertEquals("RTS", service.getContractName());
		assertEquals("ZX-48", service.getAccountCode());
		assertSame(crMock, service.getContractResolver());
		assertSame(cpMock, service.getContractParams());
		assertSame(portfolioMock, service.getPortfolio());
		assertSame(securityMock, service.getSecurity());
		assertSame(sht0Mock, service.getSeriesHandlerT0());
		assertSame(sht1Mock, service.getSeriesHandlerT1());
		assertSame(sht2Mock, service.getSeriesHandlerT2());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetContractName_ThrowsIfNotDefined() {
		service.getContractName();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetAccountCode_ThrowsIfNotDefined() {
		service.getAccountCode();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetContractResolver_ThrowsIfNotDefined() {
		service.getContractResolver();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetContractParams_ThrowsIfNotDefined() {
		service.getContractParams();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetPortfolio_ThrowsIfNotDefined() {
		service.getPortfolio();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSecurity_ThrowsIfNotDefined() {
		service.getSecurity();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSeriesHandlerH0_ThrowsIfNotDefined() {
		service.getSeriesHandlerT0();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSeriesHandlerT1_ThrowsIfNotDefined() {
		service.getSeriesHandlerT1();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetSeriesHandlerT2_ThrowsIfNotDefined() {
		service.getSeriesHandlerT0();
	}

}
