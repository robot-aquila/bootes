package ru.prolib.bootes.tsgr001a.config;

import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.BasicConfig;
import ru.prolib.bootes.lib.config.OHLCHistoryConfig;
import ru.prolib.bootes.lib.config.SchedulerConfig;
import ru.prolib.bootes.lib.config.TerminalConfig;

public class TSGR001AAppConfig extends AppConfig {
	private final TSGR001AConfig tsgr001aConfig;

	public TSGR001AAppConfig(BasicConfig basicConfig,
							 SchedulerConfig schedulerConfig,
							 OHLCHistoryConfig ohlcHistoryConfig,
							 TerminalConfig terminalConfig,
							 TSGR001AConfig tsgr001a_config)
	{
		super(basicConfig, schedulerConfig, ohlcHistoryConfig, terminalConfig);
		this.tsgr001aConfig = tsgr001a_config;
	}
	
	public TSGR001AConfig getTSGR001AConfig() {
		return tsgr001aConfig;
	}

}
