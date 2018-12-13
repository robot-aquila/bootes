package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

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
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerOnTimer;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.ContractParams;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

/**
 * Choosing the contract. 
 * <p>
 * The state handler to choose current contract. Require terminal and contract
 * resolver to be set.
 */
public class ChooseContract extends CommonHandler implements SMInputAction, SMExitAction {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ChooseContract.class);
	}
	
	private final SMInput in;
	private final ChooseContractStateCheck stateCheck;

	public ChooseContract(AppServiceLocator serviceLocator,
			RobotState state,
			ChooseContractStateCheck stateCheck)
	{
		super(serviceLocator, state);
		this.stateCheck = stateCheck;
		setExitAction(this);
		registerExit(E_OK);
		registerExit(E_NEW_SESSION);
		in = registerInput(this);
	}
	
	public ChooseContract(AppServiceLocator serviceLocator,
			RobotState state)
	{
		this(serviceLocator,
			 state,
			 new ChooseContractStateCheck(serviceLocator, state));
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		Instant ctime = terminal.getCurrentTime();
		ContractParams ccParams = state.getContractResolver().determineContract(ctime);
		Interval csPeriod = ccParams.getTradingPeriod();
		if ( ctime.compareTo(csPeriod.getEnd()) >= 0 ) {
			// This is an error! Period of the closest session must be
			// determined correctly and its end should be in future.
			throw new RuntimeException("Trading calendar error");
		}

		triggers.add(new SMTriggerOnEvent(terminal.onSecurityAvailable(), in));
		triggers.add(new SMTriggerOnTimer(terminal, csPeriod.getEnd(), in));
		if ( ctime.compareTo(csPeriod.getStart()) < 0 ) {
			triggers.add(new SMTriggerOnTimer(terminal, csPeriod.getStart(), in));
		}
		
		ContractParams pcParams = state.isContractParamsDefined() ? state.getContractParams() : null;
		Symbol ccSymbol = ccParams.getSymbol(),
			   pcSymbol = pcParams == null ? null : pcParams.getSymbol();
		logger.debug("Contract selected: {} at time {}", ccSymbol, ctime);
		if ( ! ccSymbol.equals(pcSymbol) ) {
			if ( pcSymbol != null ) {
				terminal.unsubscribe(pcSymbol);
				logger.debug("Unsubscribed: {}", pcSymbol);
			}
			terminal.subscribe(ccSymbol);
			logger.debug("Subscribed: {}", ccSymbol);
		}
		state.setContractParams(ccParams);
		
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
