package xx.mix.bootes.kinako.exante;

import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.app.comp.CombinedComp;
import ru.prolib.bootes.lib.app.comp.RTSchedulerComp;
import ru.prolib.bootes.lib.app.comp.TerminalUIComp;

public class XTerminalComp extends CombinedComp {

	public XTerminalComp(AppServiceLocator serviceLocator, String serviceID) {
		super(toList(
				new RTSchedulerComp(serviceLocator, serviceID + "-SCHEDULER"),
				new XTerminalOnlyComp(serviceLocator, serviceID + "-TERMINAL"),
				new TerminalUIComp(serviceLocator, serviceID + "-UI")
			));
	}
	
	public XTerminalComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, "EXANTE");
	}

}
