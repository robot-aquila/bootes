package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;

public class MessagesComp extends CommonComp {
	private static final String DEFAULT_ID = "MESSAGES";

	public MessagesComp(AppConfig appConfig,
			AppServiceLocator serviceLocator,
			String serviceID)
	{
		super(appConfig, serviceLocator, serviceID);
	}
	
	public MessagesComp(AppConfig appConfig,
			AppServiceLocator serviceLocator)
	{
		this(appConfig, serviceLocator, DEFAULT_ID);
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

}
