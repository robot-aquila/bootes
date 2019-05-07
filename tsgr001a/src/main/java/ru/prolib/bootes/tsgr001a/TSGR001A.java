package ru.prolib.bootes.tsgr001a;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.bootes.lib.AccountInfo;
import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppRuntimeService;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ARobotComp;

public class TSGR001A extends App {
	private static final List<TSGR001AInstConfig> instance_config;
	
	static {
		instance_config = new ArrayList<>();
		
		String filter_defs = "CoolDown30, SLgtATR, MADevLim, ByTrendT1, FCSD"; 
		instance_config.add(new TSGR001AInstConfig(
				new Account("QFORTS-TEST1"),
				"TSGR001A-ALLF",
				filter_defs,
				new StringBuilder()
					.append("Test TSGR001A with all known signal filters: ")
					.append(filter_defs)
					.toString()
			));
		
		filter_defs = "CoolDown30, SLgtATR, MADevLim";
		instance_config.add(new TSGR001AInstConfig(
				new Account("QFORTS-TEST2"),
				"TSGR001A-MINF",
				filter_defs,
				new StringBuilder()
					.append("Test TSGR001A with minimal set of filters: ")
					.append(filter_defs)
					.toString()
			));
		
		instance_config.add(new TSGR001AInstConfig(
				new Account("QFORTS-TEST3"),
				"TSGR001A-MINF-DUP",
				filter_defs,
				new StringBuilder()
					.append("Test TSGR001A with minimal set of filters (duplicate): ")
					.append(filter_defs)
					.toString()
			));

		
		filter_defs = "";
		instance_config.add(new TSGR001AInstConfig(
				new Account("QFORST-TEST4"),
				"TSGR001A-NOF",
				filter_defs,
				"Test TSGR001A without signal filters"
			));
		
	};

	public static void main(String[] args) throws Throwable {
		System.exit(new TSGR001A().run(args));
	}

	@Override
	protected void registerApplications(AppRuntimeService ars) {
		for ( TSGR001AInstConfig config : instance_config ) {
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
	protected List<AccountInfo> getExpectedAccounts() {
		List<AccountInfo> list = new ArrayList<>();
		CDecimal one_mill = CDecimalBD.ofRUB2("1000000");
		for ( TSGR001AInstConfig config : instance_config ) {
			list.add(new AccountInfo(config.getAccount(), one_mill));
		}
		return list;
	}

}
