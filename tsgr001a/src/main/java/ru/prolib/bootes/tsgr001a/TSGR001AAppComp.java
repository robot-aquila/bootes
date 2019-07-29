package ru.prolib.bootes.tsgr001a;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.app.comp.CommonComp;
import ru.prolib.bootes.lib.service.ars.ARSAction;
import ru.prolib.bootes.lib.service.ars.ARSHandler;
import ru.prolib.bootes.lib.service.ars.ARSHandlerBuilder;
import ru.prolib.bootes.tsgr001a.config.TSGR001AConfig;
import ru.prolib.bootes.tsgr001a.config.TSGR001AConfigSection;
import ru.prolib.bootes.tsgr001a.config.TSGR001AInstConfig;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ARobotComp;

public class TSGR001AAppComp extends CommonComp {
	
	static class StartupComp implements ARSAction {
		private final AppComponent comp;
		
		public StartupComp(AppComponent comp) {
			this.comp = comp;
		}

		@Override
		public void run() throws Throwable {
			comp.startup();
		}
		
	}
	
	static class ShutdownComp implements ARSAction {
		private final AppComponent comp;
		
		public ShutdownComp(AppComponent comp) {
			this.comp = comp;
		}

		@Override
		public void run() throws Throwable {
			comp.shutdown();
		}
		
	}
	
	private static final String DEFAULT_ID = "TSGR001A";
	private static final String CONFIG_SECTION_ID = "tsgr001a-app";
	private ARSHandler handler;
	
	public TSGR001AAppComp(AppServiceLocator service_locator, String service_id) {
		super(service_locator, service_id);
	}
	
	public TSGR001AAppComp(AppServiceLocator service_locator) {
		this(service_locator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		ARSHandlerBuilder builder = new ARSHandlerBuilder().withID(DEFAULT_ID);
		TSGR001AConfig conf = serviceLocator.getConfig().getSection(CONFIG_SECTION_ID);
		for ( TSGR001AInstConfig inst_conf : conf.getListOfInstances() ) {
			TSGR001ARobotComp inst_comp = new TSGR001ARobotComp(serviceLocator, inst_conf);
			inst_comp.init();
			builder.addStartupAction(new StartupComp(inst_comp));
			builder.addShutdownAction(new ShutdownComp(inst_comp));
		}
		handler = builder.build();
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
		config_service.addSection(CONFIG_SECTION_ID, new TSGR001AConfigSection());
	}

}
