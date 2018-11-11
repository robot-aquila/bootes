package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppComponent;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.sh.CommonHandler;

public class TSGR001ARobotComp implements AppComponent {
	private final AppServiceLocator serviceLocator;
	private SMStateMachine automat;
	
	public TSGR001ARobotComp(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	@Override
	public void init() throws Throwable {
		automat = new TSGR001ARobotBuilder(serviceLocator).build();
	}

	@Override
	public void startup() throws Throwable {
		automat.start();
	}

	@Override
	public void shutdown() throws Throwable {
		if ( ! automat.finished() ) {
			CommonHandler sh = (CommonHandler) automat.getCurrentState();
			automat.input(sh.getInputOfInterruption(), null);
		}
	}

}
