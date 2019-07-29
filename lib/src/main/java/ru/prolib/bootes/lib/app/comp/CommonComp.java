package ru.prolib.bootes.lib.app.comp;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public abstract class CommonComp implements AppComponent {
	protected final AppServiceLocator serviceLocator;
	protected final String serviceID;
	
	public CommonComp(AppServiceLocator serviceLocator, String serviceID) {
		this.serviceLocator = serviceLocator;
		this.serviceID = serviceID;
	}

}
