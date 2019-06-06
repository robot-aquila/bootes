package ru.prolib.bootes.tsgr001a.config;

import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.bootes.lib.config.AppConfigBuilder;
import ru.prolib.bootes.lib.config.BasicConfig;
import ru.prolib.bootes.lib.config.BasicConfigBuilder;
import ru.prolib.bootes.lib.config.OHLCHistoryConfigBuilder;
import ru.prolib.bootes.lib.config.SchedulerConfigBuilder;
import ru.prolib.bootes.lib.config.TerminalConfigBuilder;

public class TSGR001AAppConfigBuilder extends AppConfigBuilder {
	private final TSGR001AConfigBuilder tsgr001aConfigBuilder;
	
	public TSGR001AAppConfigBuilder(BasicConfigBuilder basicConfigBuilder,
			SchedulerConfigBuilder schedulerConfigBuilder,
			OHLCHistoryConfigBuilder ohlcHistoryConfigBuilder,
			TerminalConfigBuilder terminalConfigBuilder,
			TSGR001AConfigBuilder tsgr001aConfigBuilder)
	{
		super(basicConfigBuilder,
			  schedulerConfigBuilder,
			  ohlcHistoryConfigBuilder,
			  terminalConfigBuilder);
		this.tsgr001aConfigBuilder = tsgr001aConfigBuilder;
	}
	
	public TSGR001AAppConfigBuilder() {
		this(new BasicConfigBuilder(),
			 new SchedulerConfigBuilder(),
			 new OHLCHistoryConfigBuilder(),
			 new TerminalConfigBuilder(),
			 new TSGR001AConfigBuilder());
	}
	
	public TSGR001AConfigBuilder getTSGR001AConfigBuilder() {
		return tsgr001aConfigBuilder;
	}
	
	@Override
	public TSGR001AAppConfig build() throws ConfigException {
		BasicConfig bc = basicConfigBuilder.build();
		boolean sss = bc.isShowHelp();
		return new TSGR001AAppConfig(bc,
				sss ? null : schedulerConfigBuilder.build(bc),
				sss ? null : ohlcHistoryConfigBuilder.build(bc),
				sss ? null : terminalConfigBuilder.build(bc),
				sss ? null : tsgr001aConfigBuilder.build()
			);
	}
	
}
