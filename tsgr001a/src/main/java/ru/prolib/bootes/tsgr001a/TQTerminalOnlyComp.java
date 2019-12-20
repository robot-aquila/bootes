package ru.prolib.bootes.tsgr001a;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.transaq.TransaqBuilder;
import ru.prolib.aquila.transaq.impl.TQDataProviderImpl;
import ru.prolib.aquila.transaq.remote.MessageInterceptor;
import ru.prolib.aquila.transaq.remote.MessageInterceptorStub;
import ru.prolib.aquila.transaq.remote.SessionLogger;
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
import ru.prolib.bootes.lib.service.task.CloseTerminal;
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
		
		MessageInterceptor interceptor = new MessageInterceptorStub();
		if ( term_conf.isMsgDumpEnabled() ) {
			interceptor = term_conf.getMsgDumpFile() == null
					? new SessionLogger() : new SessionLogger(term_conf.getMsgDumpFile());
		}
		
		TQDataProviderImpl data_provider = (TQDataProviderImpl) new TransaqBuilder()
				.withServiceID(serviceID)
				.withEventQueue(serviceLocator.getEventQueue())
				.withConnectorFactoryStd(tq_conf, interceptor)
				.build();
		EditableTerminal terminal = new BasicTerminalBuilder()
				.withEventQueue(serviceLocator.getEventQueue())
				.withScheduler(serviceLocator.getScheduler())
				.withTerminalID(serviceID)
				.withDataProvider(data_provider)
				.buildTerminal();
		serviceLocator.setTerminal(terminal);
		
		if ( ! app_conf.getBasicConfig().isHeadless() ) {
			UIService uis = serviceLocator.getUIService();
			uis.getMainMenu().add(new TQServiceMenu(
					uis.getMessages(),
					uis.getFrame(),
					data_provider.getServices().getDirectory()
				).create());
		}
		
		handler = new ARSHandlerBuilder()
				.withID(serviceID)
				.addStartupAction(new StartTerminal(terminal))
				.addShutdownAction(new StopTerminal(terminal))
				.addShutdownAction(new CloseTerminal(terminal))
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
