package ru.prolib.bootes.lib.robo.sh;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.sh.statereq.IAccountDeterminable;

public class BOOTESWaitForAccount extends SMStateHandlerEx implements SMInputAction {
	public static final String E_OK = "OK";
	
	private final AppServiceLocator serviceLocator;
	private final IAccountDeterminable state;
	private final SMInput in;
	
	public BOOTESWaitForAccount(AppServiceLocator serviceLocator,
						  IAccountDeterminable state)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
		registerExit(E_OK);
		in = registerInput(this);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		triggers.add(new SMTriggerOnEvent(serviceLocator.getTerminal().onPortfolioAvailable(), in));
		return objectsAvailable();
	}

	@Override
	public SMExit input(Object data) {
		return objectsAvailable();
	}
	
	private SMExit objectsAvailable() {
		try {
			Terminal terminal = serviceLocator.getTerminal();
			Account account = state.getAccount();
			if ( ! terminal.isPortfolioExists(account) ) {
				return null;
			}
			Portfolio portfolio = terminal.getPortfolio(account);
			if ( ! portfolio.isAvailable() ) {
				return null;
			}
			state.setPortfolio(portfolio);
			state.getStateListener().accountSelected();
			return getExit(E_OK);

		} catch ( Exception e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		}
	}

}
