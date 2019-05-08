package ru.prolib.bootes.lib.config;

public class AppConfigBuilder {
	protected final BasicConfigBuilder basicConfigBuilder;
	protected final SchedulerConfigBuilder schedulerConfigBuilder;
	protected final OHLCHistoryConfigBuilder ohlcHistoryConfigBuilder;
	protected final TerminalConfigBuilder terminalConfigBuilder;
	
	public AppConfigBuilder(BasicConfigBuilder basicConfigBuilder,
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
		boolean skipSubsystems = bc.isShowHelp();
		return new AppConfig(bc,
			skipSubsystems ? null : schedulerConfigBuilder.build(bc),
			skipSubsystems ? null : ohlcHistoryConfigBuilder.build(bc),
			skipSubsystems ? null : terminalConfigBuilder.build(bc)
		);
	}

}
