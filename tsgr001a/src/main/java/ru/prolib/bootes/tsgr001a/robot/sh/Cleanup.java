package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.RobotStateListener;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class Cleanup extends CommonHandler {
	public static final String E_OK = "OK";

	public Cleanup(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		RobotStateListener listener = null;
		synchronized ( state ) {
			listener = state.getStateListener();
		}
		synchronized ( listener ) {
			listener.robotStopped();
		}
		return getExit(E_OK);
	}

}
