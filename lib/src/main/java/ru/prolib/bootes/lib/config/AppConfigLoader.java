package ru.prolib.bootes.lib.config;

import org.apache.commons.cli.Options;

public class AppConfigLoader {
	protected final BasicConfigLoader basicConfigLoader;
	protected final SchedulerConfigLoader schedulerConfigLoader;
	protected final OHLCHistoryConfigLoader ohlcHistoryConfigLoader;
	protected final TerminalConfigLoader terminalConfigLoader;
	
	public AppConfigLoader(BasicConfigLoader basicConfigLoader,
			SchedulerConfigLoader schedulerConfigLoader,
			OHLCHistoryConfigLoader ohlcHistoryConfigLoader,
			TerminalConfigLoader terminalConfigLoader)
	{
		this.basicConfigLoader = basicConfigLoader;
		this.schedulerConfigLoader = schedulerConfigLoader;
		this.ohlcHistoryConfigLoader = ohlcHistoryConfigLoader;
		this.terminalConfigLoader = terminalConfigLoader;
	}
	
	public AppConfigLoader() {
		this(new BasicConfigLoader(),
			 new SchedulerConfigLoader(),
			 new OHLCHistoryConfigLoader(),
			 new TerminalConfigLoader());
	}
	
	public void load(AppConfigBuilder builder,
					 OptionProvider optionProvider)
			throws ConfigException
	{
		basicConfigLoader.load(builder.getBasicConfigBuilder(), optionProvider);
		BasicConfig basicConfig = builder.getBasicConfigBuilder().build();
		if ( ! basicConfig.isShowHelp() ) {
			loadSubsystemsConfig(builder, optionProvider, basicConfig);
		}
	}
	
	protected void loadSubsystemsConfig(AppConfigBuilder builder,
										OptionProvider optionProvider,
										BasicConfig basicConfig)
			throws ConfigException
	{
		schedulerConfigLoader.load(builder.getSchedulerConfigBuilder(), optionProvider, basicConfig);
		ohlcHistoryConfigLoader.load(builder.getOHLCHistoryConfigBuilder(), optionProvider, basicConfig);
		terminalConfigLoader.load(builder.getTerminalConfigBuilder(), optionProvider, basicConfig);		
	}
	
	public void configureOptions(Options options) {
		basicConfigLoader.configureOptions(options);
		schedulerConfigLoader.configureOptions(options);
		ohlcHistoryConfigLoader.configureOptions(options);
		terminalConfigLoader.configureOptions(options);
	}

}
