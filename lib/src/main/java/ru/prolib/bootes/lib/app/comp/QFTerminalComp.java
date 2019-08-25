package ru.prolib.bootes.lib.app.comp;

import ru.prolib.bootes.lib.app.AppServiceLocator;

public class QFTerminalComp extends CombinedComp {

	public QFTerminalComp(AppServiceLocator serviceLocator, String serviceID) {
		super(toList(
				new ProbeSchedulerComp(serviceLocator, serviceID + "-PROBE-SCHEDULER"),
				new QFTerminalOnlyComp(serviceLocator, serviceID + "-TERMINAL"),
				new TerminalUIComp(serviceLocator, serviceID + "-UI")
			));
	}
	
	public QFTerminalComp(AppServiceLocator serviceLocator) {
		this(serviceLocator, "QFORTS");
	}

}
