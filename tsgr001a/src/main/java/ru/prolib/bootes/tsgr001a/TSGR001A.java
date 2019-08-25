package ru.prolib.bootes.tsgr001a;

import java.time.ZoneId;
import java.util.List;

import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.app.DriverRegistry;

public class TSGR001A extends App {

	public static void main(String[] args) throws Throwable {
		System.exit(new TSGR001A().run(args));
	}
	
	@Override
	protected void registerTerminalServices(DriverRegistry registry) {
		super.registerTerminalServices(registry);
		registry.registerDriver("transaq", new TQTerminalComp(getServiceLocator()));
	}

	@Override
	protected void registerApplications(List<AppComponent> list) {
		list.add(new TSGR001AAppComp(getServiceLocator()));
	}
	
	@Override
	protected AppServiceLocator createServiceLocator() {
		AppServiceLocator locator = super.createServiceLocator();
		locator.setZoneID(ZoneId.of("Europe/Moscow"));
		return locator;
	}

}
