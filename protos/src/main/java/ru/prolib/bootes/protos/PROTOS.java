package ru.prolib.bootes.protos;

import java.time.ZoneId;

import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppRuntimeService;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class PROTOS extends App {

	public static void main(String[] args) throws Throwable {
		System.exit(new PROTOS().run(args));
	}

	@Override
	protected void registerApplications(AppRuntimeService ars) {
		ars.addApplication(new PROTOSRobotComp(appConfig, serviceLocator));
	}
	
	@Override
	protected AppServiceLocator createServiceLocator() {
		AppServiceLocator locator = super.createServiceLocator();
		locator.setZoneID(ZoneId.of("Europe/Moscow"));
		return locator;
	}

}
