package ru.prolib.bootes.tsgr001a;

import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.ConfigException;
import ru.prolib.bootes.lib.service.AppConfigService;
import ru.prolib.bootes.lib.service.AppServiceLocator;

public class App {
	public static final int STATUS_OK = 0;
	public static final int STATUS_ERR_INIT_FAILED = 1;

	private AppServiceLocator serviceLocator = null;
	
	public int run(String[] args) throws Exception {
		try {
			AppConfig appConfig = new AppConfigService().loadConfig(args);
			serviceLocator = new AppServiceLocator(appConfig);
		} catch ( ConfigException e ) {
			return STATUS_ERR_INIT_FAILED;
		}
		// TODO: get global exit signal
		// TODO: register shutdown hook
		// TODO: wait on global exit signal
		// TODO: call cleanup code on app
		return STATUS_OK;
	}

}
