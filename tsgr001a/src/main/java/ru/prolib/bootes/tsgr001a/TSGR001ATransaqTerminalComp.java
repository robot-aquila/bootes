package ru.prolib.bootes.tsgr001a;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.transaq.impl.TQDataProviderImpl;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.app.comp.CommonComp;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.TerminalConfig;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.StartTerminal;
import ru.prolib.bootes.lib.service.task.StopTerminal;

public class TSGR001ATransaqTerminalComp extends CommonComp {
	private static final String DEFAULT_ID = "BOOTES-TERMINAL-TRANSAQ";
	private ARSHandler handler;

	public TSGR001ATransaqTerminalComp(AppConfig appConfig,
			AppServiceLocator serviceLocator,
			String serviceID)
	{
		super(appConfig, serviceLocator, serviceID);
	}
	
	public TSGR001ATransaqTerminalComp(AppConfig appConfig,
			AppServiceLocator serviceLocator)
	{
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		TerminalConfig conf = appConfig.getTerminalConfig();
		if ( "transaq".equals(conf.getDriverID()) == false ) {
			return;
		}
		
		Section tq_conf = new Wini().add("dummy_section");
		tq_conf.put("log_path", conf.getTransaqLogPath().toString());
		tq_conf.put("log_level", Integer.toString(conf.getTransaqLogLevel()));
		tq_conf.put("login", conf.getTransaqLogin());
		tq_conf.put("password", conf.getTransaqPassword());
		tq_conf.put("host", conf.getTransaqHost());
		tq_conf.put("port", Integer.toString(conf.getTransaqPort()));
		
		EditableTerminal terminal = new BasicTerminalBuilder()
				.withEventQueue(serviceLocator.getEventQueue())
				.withScheduler(serviceLocator.getScheduler())
				.withTerminalID(serviceID)
				.withDataProvider(new TQDataProviderImpl(tq_conf))
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

}
