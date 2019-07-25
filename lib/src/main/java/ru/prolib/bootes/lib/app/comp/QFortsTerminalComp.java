package ru.prolib.bootes.lib.app.comp;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.qforts.impl.QFBuilder;
import ru.prolib.aquila.qforts.impl.QFortsEnv;
import ru.prolib.aquila.web.utils.WUDataFactory;
import ru.prolib.bootes.lib.AccountInfo;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.TerminalConfig;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.StartTerminal;
import ru.prolib.bootes.lib.service.task.StopTerminal;

public class QFortsTerminalComp extends CommonComp {
	private static final String DEFAULT_ID = "BOOTES-TERMINAL";
	private final List<AccountInfo> expectedAccounts;
	private ARSHandler handler;

	public QFortsTerminalComp(AppConfig appConfig,
							  AppServiceLocator serviceLocator,
							  String serviceID,
							  List<AccountInfo> expected_accounts)
	{
		super(appConfig, serviceLocator, serviceID);
		this.expectedAccounts = expected_accounts;
	}
	
	public QFortsTerminalComp(AppConfig appConfig,
							  AppServiceLocator serviceLocator,
							  List<AccountInfo> expected_accounts)
	{
		this(appConfig, serviceLocator, DEFAULT_ID, expected_accounts);
	}

	@Override
	public void init() throws Throwable {
		TerminalConfig conf = appConfig.getTerminalConfig();
		String driver_id = conf.getDriverID();
		if ( !driver_id.equals("default") && !driver_id.equals("qforts") ) {
			return;
		}
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
		
		QFortsEnv env = qf.buildEnvironment(terminal);
		for ( AccountInfo ai : expectedAccounts ) {
			env.createPortfolio(ai.getAccount(), ai.getBalance());			
		}
		
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

}
