package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.qforts.impl.QFBuilder;
import ru.prolib.aquila.web.utils.WUDataFactory;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.config.QFTerminalConfig;
import ru.prolib.bootes.lib.config.QFTerminalConfigSection;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.StartTerminal;
import ru.prolib.bootes.lib.service.task.StopTerminal;

public class QFortsTerminalComp extends CommonComp {
	private static final String DEFAULT_ID = "BOOTES-TERMINAL";
	private static final String CONFIG_SECTION_ID = "qforts-terminal";
	private ARSHandler handler;

	public QFortsTerminalComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}
	
	public QFortsTerminalComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		AppConfig2 conf = serviceLocator.getConfig();
		QFTerminalConfig term_conf = conf.getSection(CONFIG_SECTION_ID);
		String driver_id = conf.getBasicConfig().getDriver();
		if ( !driver_id.equals("default") && !driver_id.equals("qforts") ) {
			return;
		}
		QFBuilder qf = new QFBuilder();
		DataSource data_source = new WUDataFactory()
				.createForSymbolAndL1DataReplayFM(
						serviceLocator.getScheduler(),
						term_conf.getDataDirectory(),
						serviceLocator.getPriceScaleDB()
					);
		EditableTerminal terminal = new BasicTerminalBuilder()
			.withEventQueue(serviceLocator.getEventQueue())
			.withScheduler(serviceLocator.getScheduler())
			.withTerminalID(serviceID)
			.withDataProvider(qf.withDataSource(data_source).buildDataProvider())
			.buildTerminal();
		serviceLocator.setTerminal(terminal);
		
		handler = new ARSHandlerBuilder()
				.withID(serviceID)
				.addStartupAction(new StartTerminal(terminal))
				.addShutdownAction(new StopTerminal(terminal))
				.build();
	}

	@Override
	public void startup() throws Throwable {
		if ( handler != null ) {
			handler.startup();
		}
	}

	@Override
	public void shutdown() throws Throwable {
		if ( handler != null ) {
			handler.shutdown();
		}
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		config_service.addSection(CONFIG_SECTION_ID, new QFTerminalConfigSection());
	}

}
