package ru.prolib.bootes.protos;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;

public class PROTOSRobotComp implements AppComponent {
	private final AppConfig appConfig;
	private final AppServiceLocator serviceLocator;

	public PROTOSRobotComp(AppConfig appConfig, AppServiceLocator serviceLocator) {
		this.appConfig = appConfig;
		this.serviceLocator = serviceLocator;
	}
	
	@Override
	public void init() throws Throwable {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startup() throws Throwable {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() throws Throwable {
		// TODO Auto-generated method stub
		
	}

}
