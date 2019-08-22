package xx.mix.bootes.kinako.exante;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.exante.XDataProviderFactory;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.app.comp.CommonComp;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.lib.service.task.StartTerminal;
import ru.prolib.bootes.lib.service.task.StopTerminal;

public class XTerminalComp extends CommonComp {
	private static final String DEFAULT_ID = "BOOTES-TERMINAL-EXANTE";
	private static final String CONFIG_SECTION_ID = "exante-terminal";
	
	private ARSHandler handler;

	public XTerminalComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}
	
	public XTerminalComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		if ( "exante".equals(serviceLocator.getConfig().getBasicConfig().getDriver()) == false ) {
			return;
		}
		XTerminalConfig term_conf = serviceLocator.getConfig().getSection(CONFIG_SECTION_ID);
		EditableTerminal terminal = new BasicTerminalBuilder()
				.withEventQueue(serviceLocator.getEventQueue())
				.withScheduler(serviceLocator.getScheduler())
				.withTerminalID(serviceID)
				.withDataProvider(new XDataProviderFactory().build(term_conf.getSettingFilename().getAbsolutePath()))
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
		config_service.addSection(CONFIG_SECTION_ID, new XTerminalConfigSection());
	}

}
