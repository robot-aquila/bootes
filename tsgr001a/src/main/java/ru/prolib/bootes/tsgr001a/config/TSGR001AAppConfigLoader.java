package ru.prolib.bootes.tsgr001a.config;

import org.apache.commons.cli.Options;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.bootes.lib.config.AppConfigBuilder;
import ru.prolib.bootes.lib.config.AppConfigLoader;
import ru.prolib.bootes.lib.config.BasicConfig;
import ru.prolib.bootes.lib.config.BasicConfigLoader;
import ru.prolib.bootes.lib.config.OHLCHistoryConfigLoader;
import ru.prolib.bootes.lib.config.SchedulerConfigLoader;
import ru.prolib.bootes.lib.config.TerminalConfigLoader;

public class TSGR001AAppConfigLoader extends AppConfigLoader {
	protected final TSGR001AConfigLoader tsgr001aConfigLoader;
	
	public TSGR001AAppConfigLoader(BasicConfigLoader basicConfigLoader,
			SchedulerConfigLoader schedulerConfigLoader,
			OHLCHistoryConfigLoader ohlcHistoryConfigLoader,
			TerminalConfigLoader terminalConfigLoader,
			TSGR001AConfigLoader tsgr001aConfigLoader)
	{
		super(basicConfigLoader,
			  schedulerConfigLoader,
			  ohlcHistoryConfigLoader,
			  terminalConfigLoader);
		this.tsgr001aConfigLoader = tsgr001aConfigLoader;
	}
	
	public TSGR001AAppConfigLoader() {
		this(new BasicConfigLoader(),
			new SchedulerConfigLoader(),
			new OHLCHistoryConfigLoader(),
			new TerminalConfigLoader(),
			new TSGR001AConfigLoader());
	}
	
	@Override
	protected void loadSubsystemsConfig(AppConfigBuilder builder,
										OptionProvider optionProvider,
										BasicConfig basicConfig)
			throws ConfigException
	{
		super.loadSubsystemsConfig(builder, optionProvider, basicConfig);
		tsgr001aConfigLoader.load(
				((TSGR001AAppConfigBuilder) builder).getTSGR001AConfigBuilder(),
				optionProvider,
				basicConfig
			);
	}
	
	@Override
	public void configureOptions(Options options) {
		super.configureOptions(options);
		tsgr001aConfigLoader.configureOptions(options);
	}

}
