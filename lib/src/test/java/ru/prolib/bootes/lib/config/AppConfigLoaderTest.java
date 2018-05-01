package ru.prolib.bootes.lib.config;

import static org.easymock.EasyMock.*;

import org.apache.commons.cli.Options;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class AppConfigLoaderTest {
	private IMocksControl control;
	private BasicConfigLoader basicConfigLoaderMock;
	private SchedulerConfigLoader schedulerConfigLoaderMock;
	private OHLCHistoryConfigLoader ohlcHistoryConfigLoaderMock;
	private TerminalConfigLoader terminalConfigLoaderMock;
	private AppConfigLoader service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		basicConfigLoaderMock = control.createMock(BasicConfigLoader.class);
		schedulerConfigLoaderMock = control.createMock(SchedulerConfigLoader.class);
		ohlcHistoryConfigLoaderMock = control.createMock(OHLCHistoryConfigLoader.class);
		terminalConfigLoaderMock = control.createMock(TerminalConfigLoader.class);
		service = new AppConfigLoader(basicConfigLoaderMock,
				schedulerConfigLoaderMock,
				ohlcHistoryConfigLoaderMock,
				terminalConfigLoaderMock);
	}
	
	@Test
	public void testLoad() throws Exception {
		OptionProvider opMock = control.createMock(OptionProvider.class);
		BasicConfigBuilder bcBuilderMock = control.createMock(BasicConfigBuilder.class);
		AppConfigBuilder builder = new AppConfigBuilder(bcBuilderMock,
				new SchedulerConfigBuilder(),
				new OHLCHistoryConfigBuilder(),
				new TerminalConfigBuilder());
		basicConfigLoaderMock.load(bcBuilderMock, opMock);
		BasicConfig bcMock = control.createMock(BasicConfig.class);
		expect(bcBuilderMock.build()).andReturn(bcMock);
		schedulerConfigLoaderMock.load(builder.getSchedulerConfigBuilder(), opMock, bcMock);
		ohlcHistoryConfigLoaderMock.load(builder.getOHLCHistoryConfigBuilder(), opMock, bcMock);
		terminalConfigLoaderMock.load(builder.getTerminalConfigBuilder(), opMock, bcMock);
		control.replay();
		
		service.load(builder, opMock);
		
		control.verify();
	}

	@Test
	public void testConfigureOptions() {
		Options options = new Options();
		basicConfigLoaderMock.configureOptions(options);
		schedulerConfigLoaderMock.configureOptions(options);
		ohlcHistoryConfigLoaderMock.configureOptions(options);
		terminalConfigLoaderMock.configureOptions(options);
		control.replay();
		
		service.configureOptions(options);
		
		control.verify();
	}

}
