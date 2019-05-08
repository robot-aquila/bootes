package ru.prolib.bootes.tsgr001a;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.bootes.lib.AccountInfo;
import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppConfigService;
import ru.prolib.bootes.lib.app.AppRuntimeService;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.config.TSGR001AAppConfig;
import ru.prolib.bootes.tsgr001a.config.TSGR001AInstConfig;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ARobotComp;

public class TSGR001A extends App {

	public static void main(String[] args) throws Throwable {
		System.exit(new TSGR001A().run(args));
	}

	@Override
	protected void registerApplications(AppRuntimeService ars) {
		for ( TSGR001AInstConfig config : getInstConfigs() ) {
			ars.addApplication(new TSGR001ARobotComp(
					appConfig,
					serviceLocator,
					config
				));
		}
	}
	
	@Override
	protected AppServiceLocator createServiceLocator() {
		AppServiceLocator locator = super.createServiceLocator();
		locator.setZoneID(ZoneId.of("Europe/Moscow"));
		return locator;
	}
	
	@Override
	protected AppConfigService createConfigService() {
		return new TSGR001AAppConfigService();
	}
	
	@Override
	protected List<AccountInfo> getExpectedAccounts() {
		List<AccountInfo> list = new ArrayList<>();
		CDecimal one_mill = CDecimalBD.ofRUB2("1000000");
		for ( TSGR001AInstConfig config : getInstConfigs() ) {
			list.add(new AccountInfo(config.getAccount(), one_mill));
		}
		return list;
	}
	
	protected List<TSGR001AInstConfig> getInstConfigs() {
		TSGR001AAppConfig conf = (TSGR001AAppConfig) appConfig;
		return conf.getTSGR001AConfig().getListOfInstances();
	}

}
