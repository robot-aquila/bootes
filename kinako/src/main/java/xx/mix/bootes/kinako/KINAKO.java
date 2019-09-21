package xx.mix.bootes.kinako;

import java.util.List;

import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.DriverRegistry;
import ru.prolib.bootes.lib.app.comp.SqlDBComp;
import xx.mix.bootes.kinako.exante.XTerminalComp;

public class KINAKO extends App {
	
	public static void main(String[] args) throws Throwable {
		System.exit(new KINAKO().run(args));
	}

	@Override
	protected void registerApplications(List<AppComponent> list) {
		list.add(new KINAKORobotComp(getServiceLocator()));
	}
	
	@Override
	protected void registerTerminalServices(DriverRegistry registry) {
		super.registerTerminalServices(registry);
		registry.registerDriver("exante", new XTerminalComp(getServiceLocator()));
	}
	
	@Override
	protected void registerServices(List<AppComponent> list) {
		list.add(new SqlDBComp(getServiceLocator()));
		super.registerServices(list);
	}

}
