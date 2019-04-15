package ru.prolib.bootes.lib.robo.sh;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.robo.sh.statereq.ISessionDataTrackable;

public class BOOTESCleanSessionData extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	
	protected final ISessionDataTrackable state;
	
	public BOOTESCleanSessionData(ISessionDataTrackable state) {
		this.state = state;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		state.getSessionDataHandler().cleanSession();
		state.getStateListener().sessionDataCleanup();
		return getExit(E_OK);
	}

}
