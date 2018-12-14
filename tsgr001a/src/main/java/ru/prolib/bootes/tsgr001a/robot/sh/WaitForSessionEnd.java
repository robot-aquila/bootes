package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerOnTimer;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class WaitForSessionEnd extends CommonHandler implements SMInputAction {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(WaitForSessionEnd.class);
	}
	
	private final SMInput in, in2;

	public WaitForSessionEnd(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		registerExit(E_STOP_TRADING);
		in = registerInput(this);
		in2 = registerInput(new SMInputAction() {
			@Override
			public SMExit input(Object data) {
				state.setPositionParams(state.getContractStrategy().getPositionParams());
				state.getStateListener().limitsUpdated();
				return null;
			}
		});
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Interval csp = state.getContractParams().getTradingPeriod();
		triggers.add(new SMTriggerOnTimer(serviceLocator.getTerminal(), csp.getEnd(), in));
		triggers.add(new SMTriggerOnEvent(state.getSeriesHandlerT0().getSeries().onLengthUpdate(), in2));
		logger.debug("Enter state for symbol {} at time {}",
				state.getContractParams().getSymbol(),
				serviceLocator.getTerminal().getCurrentTime());
		return null;
	}

	@Override
	public SMExit input(Object data) {
		logger.debug("Exit state for symbol {} at time {}",
				state.getContractParams().getSymbol(),
				serviceLocator.getTerminal().getCurrentTime());
		state.getStateListener().sessionDataCleanup();
		return getExit(E_STOP_TRADING);
	}

}
