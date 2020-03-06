package ru.prolib.bootes.protos;

import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.s3.S3CommonReports;

public class PROTOS extends App {
	private final int numRobots;
	private final Map<String, PROTOSRobotComp> instances;
	
	public PROTOS(int num_robots) {
		this.numRobots = num_robots;
		this.instances = new LinkedHashMap<>();
	}
	
	public PROTOS() {
		this(1);
	}

	public static void main(String[] args) throws Throwable {
		System.exit(new PROTOS().run(args));
	}
	
	public S3CommonReports getReports(String instance_id) {
		PROTOSRobotComp instance = instances.get(instance_id);
		if ( instance == null ) {
			throw new IllegalArgumentException("Instance not found: " + instance_id);
		}
		return instance.getReports();
	}

	@Override
	protected void registerApplications(List<AppComponent> list) {
		for ( int i = 1; i <= numRobots; i ++ ) {
			String instance_id = "protos" + i;
			PROTOSRobotComp instance = new PROTOSRobotComp(instance_id, getServiceLocator());
			instances.put(instance_id, instance);
			list.add(instance);
		}
	}
	
	@Override
	protected AppServiceLocator createServiceLocator() {
		AppServiceLocator locator = super.createServiceLocator();
		locator.setZoneID(ZoneId.of("Europe/Moscow"));
		return locator;
	}

}
