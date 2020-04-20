package ru.prolib.bootes.protos.sos;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.OrderDefinitionProvider;
import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.s3.S3CommonReports;

public class SOSApp extends App {
	private final OrderDefinitionProvider signalProvider;
	private final List<SOSExtension> initExtensions, startupExtensions, shutdownExtensions;
	private SOSComp comp;
	
	public SOSApp(OrderDefinitionProvider signal_provider) {
		this.signalProvider = signal_provider;
		this.initExtensions = new ArrayList<>();
		this.startupExtensions = new ArrayList<>();
		this.shutdownExtensions = new ArrayList<>();
	}
	
	public SOSApp onInit(SOSExtension ext) {
		initExtensions.add(ext);
		return this;
	}
	
	public SOSApp onStartup(SOSExtension ext) {
		startupExtensions.add(ext);
		return this;
	}
	
	public SOSApp onShutdown(SOSExtension ext) {
		shutdownExtensions.add(ext);
		return this;
	}

	public S3CommonReports getReports() {
		return comp.getReports();
	}

	@Override
	protected void registerApplications(List<AppComponent> list) {
		list.add(comp = createComponent());
	}
	
	@Override
	protected AppServiceLocator createServiceLocator() {
		AppServiceLocator locator = super.createServiceLocator();
		locator.setZoneID(ZoneId.of("Europe/Moscow"));
		return locator;
	}
	
	protected SOSComp createComponent() {
		return new SOSComp("sos", getServiceLocator(), signalProvider,
				initExtensions, startupExtensions, shutdownExtensions);
	}

}
