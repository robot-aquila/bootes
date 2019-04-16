package ru.prolib.bootes.lib.robo.sh;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.sh.statereq.IContractDeterminable;

public class BOOTESWaitForSessionEnd extends SMStateHandlerEx {
	public static final String E_SESSION_END = "SESSION_END";
	
	protected final AppServiceLocator serviceLocator;
	protected final IContractDeterminable state;
	
	public BOOTESWaitForSessionEnd(AppServiceLocator serviceLocator,
								   IContractDeterminable state)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
		registerExit(E_SESSION_END);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		triggers.add(newExitOnTimer(serviceLocator.getTerminal(),
				state.getContractParams().getDataTrackingPeriod().getEnd(),
				E_SESSION_END)
			);
		return null;
	}
	
}
