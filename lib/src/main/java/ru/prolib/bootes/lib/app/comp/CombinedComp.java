package ru.prolib.bootes.lib.app.comp;

import java.util.ArrayList;
import java.util.List;

import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppConfigService2;

public class CombinedComp implements AppComponent {
	
	public static List<AppComponent> toList(AppComponent... components) {
		List<AppComponent> result = new ArrayList<>();
		for ( AppComponent comp : components ) {
			result.add(comp);
		}
		return result;
	}

	private final List<AppComponent> components;
	
	public CombinedComp(List<AppComponent> components) {
		this.components = components;
	}

	@Override
	public void init() throws Throwable {
		for ( AppComponent comp : components ) {
			comp.init();
		}
	}

	@Override
	public void startup() throws Throwable {
		for ( AppComponent comp : components ) {
			comp.startup();
		}
	}

	@Override
	public void shutdown() throws Throwable {
		for ( AppComponent comp : components ) {
			comp.shutdown();
		}
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		for ( AppComponent comp : components ) {
			comp.registerConfig(config_service);
		}
	}

}
