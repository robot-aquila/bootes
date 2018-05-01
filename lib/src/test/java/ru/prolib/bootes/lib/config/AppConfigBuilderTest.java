package ru.prolib.bootes.lib.config;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class AppConfigBuilderTest {
	private IMocksControl control;
	private BasicConfigBuilder basicConfigBuilderMock;
	private SchedulerConfigBuilder schedulerConfigBuilderMock;
	private OHLCHistoryConfigBuilder ohlcHistoryConfigBuilderMock;
	private TerminalConfigBuilder terminalConfigBuilderMock;
	private AppConfigBuilder service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		basicConfigBuilderMock = control.createMock(BasicConfigBuilder.class);
		schedulerConfigBuilderMock = control.createMock(SchedulerConfigBuilder.class);
		ohlcHistoryConfigBuilderMock = control.createMock(OHLCHistoryConfigBuilder.class);
		terminalConfigBuilderMock = control.createMock(TerminalConfigBuilder.class);
		service = new AppConfigBuilder(basicConfigBuilderMock,
				schedulerConfigBuilderMock,
				ohlcHistoryConfigBuilderMock,
				terminalConfigBuilderMock);
	}
	
	@Test
	public void testCtor4() {
		assertSame(basicConfigBuilderMock, service.getBasicConfigBuilder());
		assertSame(schedulerConfigBuilderMock, service.getSchedulerConfigBuilder());
		assertSame(ohlcHistoryConfigBuilderMock, service.getOHLCHistoryConfigBuilder());
		assertSame(terminalConfigBuilderMock, service.getTerminalConfigBuilder());
	}
	
	@Test
	public void testCtor0() {
		service = new AppConfigBuilder();
		assertNotNull(service.getBasicConfigBuilder());
		assertNotNull(service.getSchedulerConfigBuilder());
		assertNotNull(service.getOHLCHistoryConfigBuilder());
		assertNotNull(service.getTerminalConfigBuilder());
	}

	@Test
	public void testBuild() throws Exception {
		BasicConfig bcMock = control.createMock(BasicConfig.class);
		SchedulerConfig schedulerConfigMock = control.createMock(SchedulerConfig.class);
		OHLCHistoryConfig ohlcHistoryConfigMock = control.createMock(OHLCHistoryConfig.class);
		TerminalConfig terminalConfigMock = control.createMock(TerminalConfig.class);
		expect(basicConfigBuilderMock.build()).andReturn(bcMock);
		expect(schedulerConfigBuilderMock.build(bcMock)).andReturn(schedulerConfigMock);
		expect(ohlcHistoryConfigBuilderMock.build(bcMock)).andReturn(ohlcHistoryConfigMock);
		expect(terminalConfigBuilderMock.build(bcMock)).andReturn(terminalConfigMock);
		control.replay();
		
		AppConfig actual = service.build();
		
		control.verify();
		AppConfig expected = new AppConfig(bcMock, schedulerConfigMock, ohlcHistoryConfigMock, terminalConfigMock);
		assertEquals(expected, actual);
	}

}
