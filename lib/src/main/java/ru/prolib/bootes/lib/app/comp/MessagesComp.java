package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class MessagesComp extends CommonComp {
	private static final String DEFAULT_ID = "MESSAGES";

	public MessagesComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}
	
	public MessagesComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		serviceLocator.setMessages(new Messages());
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
