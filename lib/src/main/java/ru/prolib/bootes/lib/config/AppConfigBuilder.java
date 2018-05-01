package ru.prolib.bootes.lib.config;

public class AppConfigBuilder {
	private final BasicConfigBuilder basicConfigBuilder;
	private final SchedulerConfigBuilder schedulerConfigBuilder;
	private final OHLCHistoryConfigBuilder ohlcHistoryConfigBuilder;
	private final TerminalConfigBuilder terminalConfigBuilder;
	
	AppConfigBuilder(BasicConfigBuilder basicConfigBuilder,
			SchedulerConfigBuilder schedulerConfigBuilder,
			OHLCHistoryConfigBuilder ohlcHistoryConfigBuilder,
			TerminalConfigBuilder terminalConfigBuilder)
	{
		this.basicConfigBuilder = basicConfigBuilder;
		this.schedulerConfigBuilder = schedulerConfigBuilder;
		this.ohlcHistoryConfigBuilder = ohlcHistoryConfigBuilder;
		this.terminalConfigBuilder = terminalConfigBuilder;
	}
	
	public AppConfigBuilder() {
		this(new BasicConfigBuilder(),
			new SchedulerConfigBuilder(),
			new OHLCHistoryConfigBuilder(),
			new TerminalConfigBuilder());
	}
	
	public BasicConfigBuilder getBasicConfigBuilder() {
		return basicConfigBuilder;
	}
	
	public SchedulerConfigBuilder getSchedulerConfigBuilder() {
		return schedulerConfigBuilder;
	}
	
	public OHLCHistoryConfigBuilder getOHLCHistoryConfigBuilder() {
		return ohlcHistoryConfigBuilder;
	}
	
	public TerminalConfigBuilder getTerminalConfigBuilder() {
		return terminalConfigBuilder;
	}
	
	public AppConfig build() throws ConfigException {
		BasicConfig bc = basicConfigBuilder.build();
		return new AppConfig(bc,
			schedulerConfigBuilder.build(bc),
			ohlcHistoryConfigBuilder.build(bc),
			terminalConfigBuilder.build(bc));
	}

}
