package ru.prolib.bootes.lib.robo.sh;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.sh.statereq.IStateObservable;

public class BOOTESCleanup extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	private final IStateObservable state;

	public BOOTESCleanup(AppServiceLocator serviceLocator, IStateObservable state) {
		this.state = state;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		state.getStateListener().robotStopped();
		return getExit(E_OK);
	}

}
