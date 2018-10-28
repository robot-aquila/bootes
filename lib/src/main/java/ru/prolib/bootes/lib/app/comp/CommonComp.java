package ru.prolib.bootes.lib.app.comp;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;

public abstract class CommonComp implements AppComponent {
	protected final AppConfig appConfig;
	protected final AppServiceLocator serviceLocator;
	protected final String serviceID;
	
	public CommonComp(AppConfig appConfig, AppServiceLocator serviceLocator, String serviceID) {
		this.appConfig = appConfig;
		this.serviceLocator = serviceLocator;
		this.serviceID = serviceID;
	}

}
