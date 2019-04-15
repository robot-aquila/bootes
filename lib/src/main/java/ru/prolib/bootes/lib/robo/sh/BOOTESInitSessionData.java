package ru.prolib.bootes.lib.robo.sh;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.robo.sh.statereq.ISessionDataTrackable;

public class BOOTESInitSessionData extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	
	protected final ISessionDataTrackable state;
	
	public BOOTESInitSessionData(ISessionDataTrackable state) {
		this.state = state;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		if ( state.getSessionDataHandler().startSession() ) {
			state.getStateListener().sessionDataAvailable();
			return getExit(E_OK);
		} else {
			return getExit(E_ERROR);
		}
	}
	
}
