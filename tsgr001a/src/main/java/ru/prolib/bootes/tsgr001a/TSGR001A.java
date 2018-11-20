package ru.prolib.bootes.tsgr001a;

import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppRuntimeService;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ARobotComp;

public class TSGR001A extends App {

	public static void main(String[] args) throws Throwable {
		System.exit(new TSGR001A().run(args));
	}

	@Override
	protected void registerApplications(AppRuntimeService ars) {
		ars.addApplication(new TSGR001ARobotComp(appConfig, serviceLocator));
	}

}
