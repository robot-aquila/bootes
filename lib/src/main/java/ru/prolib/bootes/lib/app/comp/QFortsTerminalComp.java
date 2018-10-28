package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.qforts.impl.QFBuilder;
import ru.prolib.aquila.web.utils.WUDataFactory;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.TerminalConfig;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.StartTerminal;
import ru.prolib.bootes.lib.service.task.StopTerminal;

public class QFortsTerminalComp extends CommonComp {
	private static final String DEFAULT_ID = "BOOTES-TERMINAL";
	private ARSHandler handler;

	public QFortsTerminalComp(AppConfig appConfig, AppServiceLocator serviceLocator, String serviceID) {
		super(appConfig, serviceLocator, serviceID);
	}
	
	public QFortsTerminalComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		TerminalConfig conf = appConfig.getTerminalConfig();
		QFBuilder qf = new QFBuilder();
		EditableTerminal terminal = new BasicTerminalBuilder()
			.withEventQueue(serviceLocator.getEventQueue())
			.withScheduler(serviceLocator.getScheduler())
			.withTerminalID(serviceID)
			.withDataProvider(new WUDataFactory()
					.decorateForSymbolAndL1DataReplayFM(qf.buildDataProvider(),
							serviceLocator.getScheduler(),
							conf.getQFortsDataDirectory(),
							serviceLocator.getPriceScaleDB()))
			.buildTerminal();
		serviceLocator.setTerminal(terminal);
		
		qf.buildEnvironment(terminal)
				.createPortfolio(conf.getQForstTestAccount(), conf.getQForstTestBalance());
		handler = new ARSHandlerBuilder()
				.withID(serviceID)
				.addStartupAction(new StartTerminal(terminal))
				.addShutdownAction(new StopTerminal(terminal))
				.build();
	}

	@Override
	public void startup() throws Throwable {
		handler.startup();
	}

	@Override
	public void shutdown() throws Throwable {
		handler.shutdown();
	}

}
