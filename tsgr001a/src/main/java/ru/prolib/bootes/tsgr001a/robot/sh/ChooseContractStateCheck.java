package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.E_NEW_SESSION;
import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.E_OK;

import java.time.Instant;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.ContractParams;

public class ChooseContractStateCheck {
	private final AppServiceLocator serviceLocator;
	private final State state;
	
	public ChooseContractStateCheck(AppServiceLocator serviceLocator, State state) {
		this.serviceLocator = serviceLocator;
		this.state = state;
	}

	public String checkState() {
		Terminal terminal = serviceLocator.getTerminal();
		Instant ctime = terminal.getCurrentTime();
		ContractParams ccp = state.getContractParams();
		Interval csp = ccp.getTradingPeriod();
		// This check should be first
		if ( ctime.compareTo(csp.getEnd()) >= 0 ) {
			return E_NEW_SESSION;
		}
		
		Symbol symbol = ccp.getSymbol();
		if ( ! terminal.isSecurityExists(symbol) ) {
			return null;
		}
		
		try {
			Security security = terminal.getSecurity(symbol);
			if ( ! security.isAvailable() ) {
				return null;
			}
		} catch ( SecurityException e ) {
			throw new IllegalStateException("Unexpected exception", e);
		}
		
		if ( csp.contains(ctime) ) {
			return E_OK;
		}
		return null;
	}

}
