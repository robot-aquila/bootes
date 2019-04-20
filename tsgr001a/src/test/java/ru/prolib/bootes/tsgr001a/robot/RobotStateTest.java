package ru.prolib.bootes.tsgr001a.robot;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.bootes.lib.cr.ContractParams;
import ru.prolib.bootes.lib.cr.ContractResolver;
import ru.prolib.bootes.lib.rm.RMContractStrategy;
import ru.prolib.bootes.lib.rm.RMContractStrategyParams;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;

public class RobotStateTest {
	private IMocksControl control;
	private RobotState service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new RobotState();
	}
	
	@Test
	public void testCtor() {
		assertNotNull(service.getStateListener());
	}

	@Test
	public void testSettersAndGetters() {
		ContractResolver crMock = control.createMock(ContractResolver.class);
		ContractParams cpMock = control.createMock(ContractParams.class);
		RMContractStrategy csMock = control.createMock(RMContractStrategy.class);
		Portfolio portfolioMock = control.createMock(Portfolio.class);
		Security securityMock = control.createMock(Security.class);
		STSeriesHandler sht0Mock = control.createMock(STSeriesHandler.class);
		STSeriesHandler sht1Mock = control.createMock(STSeriesHandler.class);
		STSeriesHandler sht2Mock = control.createMock(STSeriesHandler.class);
		RMContractStrategyPositionParams ppMock = control.createMock(RMContractStrategyPositionParams.class);
		S3Speculation specMock = control.createMock(S3Speculation.class);
		RMContractStrategyParams cspMock = control.createMock(RMContractStrategyParams.class);
		
		service.setContractName("RTS");
		service.setAccount(new Account("ZX-48"));
		service.setContractResolver(crMock);
		service.setContractParams(cpMock);
		service.setContractStrategy(csMock);
		service.setPortfolio(portfolioMock);
		service.setSecurity(securityMock);
		service.setSeriesHandlerT0(sht0Mock);
		service.setSeriesHandlerT1(sht1Mock);
		service.setSeriesHandlerT2(sht2Mock);
		service.setPositionParams(ppMock);
		service.setActiveSpeculation(specMock);
		service.setContractStrategyParams(cspMock);
		
		assertEquals("RTS", service.getContractName());
		assertEquals(new Account("ZX-48"), service.getAccount());
		assertSame(crMock, service.getContractResolver());
		assertSame(cpMock, service.getContractParams());
		assertSame(csMock, service.getContractStrategy());
		assertSame(portfolioMock, service.getPortfolio());
		assertSame(securityMock, service.getSecurity());
		assertSame(sht0Mock, service.getSeriesHandlerT0());
		assertSame(sht1Mock, service.getSeriesHandlerT1());
		assertSame(sht2Mock, service.getSeriesHandlerT2());
		assertSame(ppMock, service.getPositionParams());
		assertSame(specMock, service.getActiveSpeculation());
		assertSame(cspMock, service.getContractStrategyParams());
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetContractName_ThrowsIfNotDefined() {
		service.getContractName();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetAccount_ThrowsIfNotDefined() {
		service.getAccount();
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
	public void testGetContractStrategy_ThrowsIfNotDefined() {
		service.getContractStrategy();
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
	public void testGetSeriesHandlerT0_ThrowsIfNotDefined() {
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
	
	@Test (expected=NullPointerException.class)
	public void testGetPositionParams_ThrowsIfNotDefined() {
		service.getPositionParams();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetActiveSpeculation_ThrowsIfNotDefined() {
		service.getActiveSpeculation();
	}
	
	@Test (expected=NullPointerException.class)
	public void testGetContractStrategyParams_ThrowsIfNotDefined() {
		service.getContractStrategyParams();
	}
	
	@Test
	public void testIsSeriesHandlerT0Defined() {
		STSeriesHandler shMock = control.createMock(STSeriesHandler.class);
		assertFalse(service.isSeriesHandlerT0Defined());
		service.setSeriesHandlerT0(shMock);
		assertTrue(service.isSeriesHandlerT0Defined());
		service.setSeriesHandlerT0(null);
		assertFalse(service.isSeriesHandlerT0Defined());
	}
	
	@Test
	public void testIsSeriesHandlerT1Defined() {
		STSeriesHandler shMock = control.createMock(STSeriesHandler.class);
		assertFalse(service.isSeriesHandlerT1Defined());
		service.setSeriesHandlerT1(shMock);
		assertTrue(service.isSeriesHandlerT1Defined());
		service.setSeriesHandlerT1(null);
		assertFalse(service.isSeriesHandlerT1Defined());
	}
	
	@Test
	public void testIsSeriesHandlerT2Defined() {
		STSeriesHandler shMock = control.createMock(STSeriesHandler.class);
		assertFalse(service.isSeriesHandlerT2Defined());
		service.setSeriesHandlerT2(shMock);
		assertTrue(service.isSeriesHandlerT2Defined());
		service.setSeriesHandlerT2(null);
		assertFalse(service.isSeriesHandlerT2Defined());
	}
	
	@Test
	public void testIsPositionParamsDefined() {
		RMContractStrategyPositionParams ppMock = control.createMock(RMContractStrategyPositionParams.class);
		assertFalse(service.isPositionParamsDefined());
		service.setPositionParams(ppMock);
		assertTrue(service.isPositionParamsDefined());
		service.setPositionParams(null);
		assertFalse(service.isPositionParamsDefined());
	}

}
