package ru.prolib.bootes.lib.sm;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerOnTimer;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.cr.ContractParams;
import ru.prolib.bootes.lib.sm.statereq.IContractDeterminable;

/**
 * Choosing the contract. 
 * <p>
 * The state handler to choose current contract and wait for security and data
 * tracking period. Require terminal and contract resolver to be set.
 */
public class ChooseContract extends SMStateHandlerEx
	implements SMInputAction, SMExitAction
{
	public static final String E_OK = "OK";
	public static final String E_NEW_SESSION = "NEW_SESSION";
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ChooseContract.class);
	}
	
	private final AppServiceLocator serviceLocator;
	private final IContractDeterminable state;
	private final SMInput in;
	private final ChooseContractStateCheck stateCheck;

	public ChooseContract(AppServiceLocator serviceLocator,
			IContractDeterminable state,
			ChooseContractStateCheck stateCheck)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
		this.stateCheck = stateCheck;
		setExitAction(this);
		registerExit(E_OK);
		registerExit(E_NEW_SESSION);
		in = registerInput(this);
	}
	
	public ChooseContract(AppServiceLocator serviceLocator,
			IContractDeterminable state)
	{
		this(serviceLocator,
			 state,
			 new ChooseContractStateCheck(serviceLocator, state));
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		Instant currTime = terminal.getCurrentTime();
		ContractParams currParams = state.determineContractParams(currTime);
		Interval dtp = currParams.getDataTrackingPeriod();
		if ( currTime.compareTo(dtp.getEnd()) >= 0 ) {
			// This is an error! Period of the closest session must be
			// determined correctly and its end should be in future.
			throw new RuntimeException("Trading calendar error");
		}

		triggers.add(new SMTriggerOnEvent(terminal.onSecurityAvailable(), in));
		triggers.add(new SMTriggerOnTimer(terminal, dtp.getEnd(), in));
		if ( ! dtp.contains(currTime) ) {
			triggers.add(new SMTriggerOnTimer(terminal, dtp.getStart(), in));
		}
		
		ContractParams prevParams = state.getContractParamsOrNull();
		Symbol currSymbol = currParams.getSymbol();
		Symbol prevSymbol = prevParams == null ? null : prevParams.getSymbol();
		logger.debug("Contract selected: {} at time {}", currSymbol, currTime);
		if ( ! currSymbol.equals(prevSymbol) ) {
			if ( prevSymbol != null ) {
				terminal.unsubscribe(prevSymbol);
				logger.debug("Unsubscribed: {}", prevSymbol);
			}
			terminal.subscribe(currSymbol);
			logger.debug("Subscribed: {}", currSymbol);
		}
		state.setContractParams(currParams);
		
		return checkState();
	}

	@Override
	public SMExit input(Object data) {
		return checkState();
	}

	@Override
	public void exit() {
		
	}
	
	private SMExit checkState() {
		String exitID = stateCheck.checkState();
		if ( exitID != null && E_OK.equals(exitID) ) {
			try {
				state.setSecurity(serviceLocator.getTerminal()
					.getSecurity(state.getContractParams().getSymbol()));
			} catch ( SecurityException e ) {
				throw new IllegalStateException("Unexpected exception", e);
			}
			state.getStateListener().contractSelected();
		}
		return getExit(exitID);
	}

}
