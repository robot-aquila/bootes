package ru.prolib.bootes.protos.sh;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class PROTOSInit extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	
	protected final AppServiceLocator serviceLocator;
	
	public PROTOSInit(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		final String cn = "RTS", an = "QFORTS-TEST";
		// TODO: not yet done
		return getExit(E_OK);
	}

}
