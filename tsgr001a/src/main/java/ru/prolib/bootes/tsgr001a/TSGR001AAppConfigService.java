package ru.prolib.bootes.tsgr001a;

import ru.prolib.bootes.lib.app.AppConfigService;
import ru.prolib.bootes.lib.config.AppConfigBuilder;
import ru.prolib.bootes.lib.config.AppConfigLoader;
import ru.prolib.bootes.tsgr001a.config.TSGR001AAppConfigBuilder;
import ru.prolib.bootes.tsgr001a.config.TSGR001AAppConfigLoader;

public class TSGR001AAppConfigService extends AppConfigService {
	
	public static class MyFactory extends AppConfigService.Factory {
		
		@Override
		public AppConfigBuilder createAppConfigBuilder() {
			return new TSGR001AAppConfigBuilder();
		}
		
		@Override
		public AppConfigLoader createAppConfigLoader() {
			return new TSGR001AAppConfigLoader();
		}
		
	}
	
	public TSGR001AAppConfigService() {
		super(new MyFactory());
	}

}
