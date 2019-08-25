package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.BusinessEntities.SchedulerLocal;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class RTSchedulerComp extends CommonComp {

	public RTSchedulerComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}

	@Override
	public void init() throws Throwable {
		serviceLocator.setScheduler(new SchedulerLocal(serviceID));
	}

	@Override
	public void startup() throws Throwable {
		
	}

	@Override
	public void shutdown() throws Throwable {
		
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		
	}

}
