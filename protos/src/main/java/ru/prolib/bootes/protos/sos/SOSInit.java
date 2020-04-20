package ru.prolib.bootes.protos.sos;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.cr.MOEXContractResolverRegistry;
import ru.prolib.bootes.protos.PROTOSRobotState;

public class SOSInit extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	
	protected final AppServiceLocator serviceLocator;
	protected final PROTOSRobotState state;

	public SOSInit(AppServiceLocator serviceLocator, PROTOSRobotState state) {
		this.serviceLocator = serviceLocator;
		this.state = state;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		state.setRobotTitle("SOS-TEST");
		state.setAccount(new Account("SOS-TEST-ACCOUNT"));
		state.setContractResolver(new MOEXContractResolverRegistry().getResolver("RTS"));
		state.getStateListener().robotStarted();
		return getExit(E_OK);
	}

}
