package ru.prolib.bootes.tsgr001a;

import org.apache.commons.lang3.tuple.Pair;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.transaq.engine.Engine;
import ru.prolib.aquila.transaq.engine.EngineBuilder;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQConnectorFactory;
import ru.prolib.aquila.transaq.impl.TQDataProviderImpl;
import ru.prolib.aquila.transaq.ui.TQServiceMenu;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.app.comp.CommonComp;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.config.TQTerminalConfig;
import ru.prolib.bootes.lib.config.TQTerminalConfigSection;
import ru.prolib.bootes.lib.service.UIService;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.StartTerminal;
import ru.prolib.bootes.lib.service.task.StopTerminal;

public class TQTerminalOnlyComp extends CommonComp {
	private static final String CONFIG_SECTION_ID = "transaq-terminal";

	private ARSHandler handler;

	public TQTerminalOnlyComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}

	@Override
	public void init() throws Throwable {
		if ( "transaq".equals(serviceLocator.getConfig().getBasicConfig().getDriver()) == false ) {
			return;
		}
		AppConfig2 app_conf = serviceLocator.getConfig();
		TQTerminalConfig term_conf = app_conf.getSection(CONFIG_SECTION_ID);
		Section tq_conf = new Wini().add("dummy_section");
		tq_conf.put("log_path", term_conf.getLogPath().getAbsolutePath());
		tq_conf.put("log_level", Integer.toString(term_conf.getLogLevel()));
		tq_conf.put("login", term_conf.getLogin());
		tq_conf.put("password", term_conf.getPassword());
		tq_conf.put("host", term_conf.getHost());
		tq_conf.put("port", Integer.toString(term_conf.getPort()));
		
		EngineBuilder eng_builder = new EngineBuilder();
		Pair<ServiceLocator, Engine> eng_res = eng_builder.build();
		ServiceLocator eng_services = eng_res.getLeft();
		Engine eng = eng_res.getRight();
		eng_services.setConnector(new TQConnectorFactory().createInstance(tq_conf, eng));
		eng_builder.initPrimary(eng_services, serviceLocator.getEventQueue());
		
		EditableTerminal terminal = new BasicTerminalBuilder()
				.withEventQueue(serviceLocator.getEventQueue())
				.withScheduler(serviceLocator.getScheduler())
				.withTerminalID(serviceID)
				.withDataProvider(new TQDataProviderImpl(eng, eng_builder, eng_services))
				.buildTerminal();
		serviceLocator.setTerminal(terminal);
		
		if ( ! app_conf.getBasicConfig().isHeadless() ) {
			UIService uis = serviceLocator.getUIService();
			uis.getMainMenu().add(new TQServiceMenu(
					uis.getMessages(),
					uis.getFrame(),
					eng_services.getDirectory()
				).create());
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
		config_service.addSection(CONFIG_SECTION_ID, new TQTerminalConfigSection());
	}

}
