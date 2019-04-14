package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

@Deprecated
public class InitSessionData extends CommonHandler {
	public static final String E_OK = "OK";
	
	private final CommonActions ca;

	public InitSessionData(AppServiceLocator serviceLocator,
			RobotState state,
			CommonActions ca)
	{
		super(serviceLocator, state);
		this.ca = ca;
		registerExit(E_OK);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		//ca.cleanupCurrentDataHandlers(state);
		return getExit(ca.initDataHandlers(serviceLocator, state) ? E_OK : E_ERROR);
	}
	
}
