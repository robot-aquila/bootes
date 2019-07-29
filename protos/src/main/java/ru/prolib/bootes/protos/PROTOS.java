package ru.prolib.bootes.protos;

import java.time.ZoneId;
import java.util.List;

import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class PROTOS extends App {

	public static void main(String[] args) throws Throwable {
		System.exit(new PROTOS().run(args));
	}

	@Override
	protected void registerApplications(List<AppComponent> list) {
		list.add(new PROTOSRobotComp(getServiceLocator()));
	}
	
	@Override
	protected AppServiceLocator createServiceLocator() {
		AppServiceLocator locator = super.createServiceLocator();
		locator.setZoneID(ZoneId.of("Europe/Moscow"));
		return locator;
	}

}
