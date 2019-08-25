package ru.prolib.bootes.tsgr001a;

import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.app.comp.CombinedComp;
import ru.prolib.bootes.lib.app.comp.RTSchedulerComp;
import ru.prolib.bootes.lib.app.comp.TerminalUIComp;

public class TQTerminalComp extends CombinedComp {

	public TQTerminalComp(AppServiceLocator serviceLocator, String serviceID) {
		super(toList(
				new RTSchedulerComp(serviceLocator, serviceID + "-SCHEDULER"),
				new TQTerminalOnlyComp(serviceLocator, serviceID + "-TERMINAL"),
				new TerminalUIComp(serviceLocator, serviceID + "-UI")
			));
	}
	
	public TQTerminalComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, "TRANSAQ");
	}

}
