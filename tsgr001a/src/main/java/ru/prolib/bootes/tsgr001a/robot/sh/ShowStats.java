package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class ShowStats extends CommonHandler {

	public ShowStats(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);

		return getExit(E_OK);
	}

}
