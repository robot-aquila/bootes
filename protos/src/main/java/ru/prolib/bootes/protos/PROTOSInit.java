package ru.prolib.bootes.protos;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.cr.MOEXContractResolverRegistry;
import ru.prolib.bootes.lib.robo.s3.S3RobotState;

public class PROTOSInit extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	
	protected final AppServiceLocator serviceLocator;
	protected final S3RobotState state;
	
	public PROTOSInit(AppServiceLocator serviceLocator, S3RobotState state) {
		this.serviceLocator = serviceLocator;
		this.state = state;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		final String contract_name = "RTS", account_name = "QFORTS-TEST";
		state.setRobotTitle("PROTOS-" + contract_name);
		state.setContractResolver(new MOEXContractResolverRegistry().getResolver(contract_name));
		state.setAccount(new Account(account_name));
		state.setSessionDataHandler(new PROTOSDataHandler(serviceLocator, state));
		state.getStateListener().robotStarted();
		return getExit(E_OK);
	}

}
