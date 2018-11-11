package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.ContractResolverRegistry;

public class Init extends CommonHandler {

	public Init(AppServiceLocator serviceLocator, State state) {
		super(serviceLocator, state);
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		final String cn = "RTS";
		state.setContractName(cn);
		state.setContractResolver(new ContractResolverRegistry().getResolver(cn));
		return getExit(E_OK);
	}

}
