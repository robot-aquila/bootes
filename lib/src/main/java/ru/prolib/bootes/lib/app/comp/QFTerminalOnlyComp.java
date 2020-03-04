package ru.prolib.bootes.lib.app.comp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
//import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.qforts.impl.QFBuilder;
import ru.prolib.aquila.qforts.ui.QFServiceMenu;
import ru.prolib.aquila.web.utils.WUDataFactory;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.config.QFTerminalConfig;
import ru.prolib.bootes.lib.config.QFTerminalConfigSection;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.StartTerminal;
import ru.prolib.bootes.lib.service.task.StopTerminal;

public class QFTerminalOnlyComp extends CommonComp {
	static final Logger logger = LoggerFactory.getLogger(QFTerminalOnlyComp.class);
	private static final String CONFIG_SECTION_ID = "qforts-terminal";
	private ARSHandler handler;

	public QFTerminalOnlyComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}

	@Override
	public void init() throws Throwable {
		AppConfig2 conf = serviceLocator.getConfig();
		QFTerminalConfig term_conf = conf.getSection(CONFIG_SECTION_ID);
		String driver_id = conf.getBasicConfig().getDriver();
		if ( !driver_id.equals("default") && !driver_id.equals("qforts") ) {
			return;
		}
		WUDataFactory wu_factory = new WUDataFactory();
		DataSource data_source = wu_factory.createForSymbolAndL1DataReplayFM(serviceLocator.getScheduler(),
				term_conf.getDataDirectory());
		QFBuilder qfb = new QFBuilder()
				.withEventQueue(serviceLocator.getEventQueue())
				.withLiquidityMode(term_conf.getLiquidityMode())
				.withOrderExecutionTriggerMode(term_conf.getOrderExecTriggerMode());
		if ( term_conf.isLegacySymbolDataService() ) {
			qfb.withLegacySymbolDataService(true);
			logger.debug("Selected legacy symbol data service. Possible long execution.");
		}
		EditableTerminal terminal = new BasicTerminalBuilder()
			.withEventQueue(serviceLocator.getEventQueue())
			.withScheduler(serviceLocator.getScheduler())
			.withTerminalID(serviceID)
			.withDataProvider(qfb.withDataSource(data_source).buildDataProvider())
			.buildTerminal();
		qfb.buildEnvironment(terminal).createPortfolio(term_conf.getTestAccount(), term_conf.getTestBalance());
		
		// TODO: In that form this does not make big sense because it will not give securities in AVAILABLE state.
		// If we'll add subscription on symbol data here it will give huge overhead on reading and dispatching lot data.
		// So, the best solution is - subscribe on symbol when you actually need it.
		//
		//System.out.println("Scanning for symbols: " + term_conf.getDataDirectory());
		//for ( Symbol symbol : wu_factory.createContractDataStorage(term_conf.getDataDirectory()).getSymbols() ) {
		//	System.out.println("found symbol: " + symbol);
		//	terminal.getEditableSecurity(symbol);
		//}
		
		serviceLocator.setTerminal(terminal);
		
		if ( conf.getBasicConfig().isHeadless() == false ) {
			UIService uis = serviceLocator.getUIService();
			uis.getMainMenu().add(new QFServiceMenu(uis.getFrame(), uis.getMessages()).create(terminal));
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

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		config_service.addSection(CONFIG_SECTION_ID, new QFTerminalConfigSection());
	}

}
