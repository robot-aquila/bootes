package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class WaitForSessionEnd extends CommonHandler implements SMInputAction {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(WaitForSessionEnd.class);
	}
	
	private final CommonActions ca;
	private final SMInput in;

	public WaitForSessionEnd(AppServiceLocator serviceLocator,
			RobotState state,
			CommonActions ca) {
		super(serviceLocator, state);
		this.ca = ca;
		registerExit(E_STOP_DATA_TRACKING);
		in = registerInput(this);
	}
	
	private void updatePositionParams() {
		ca.updatePositionParams(serviceLocator, state);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Interval dtp = state.getContractParams().getDataTrackingPeriod();
		triggers.add(newExitOnTimer(serviceLocator.getTerminal(), dtp.getEnd(), E_STOP_DATA_TRACKING));
		triggers.add(newTriggerOnEvent(state.getSeriesHandlerT0().getSeries().onLengthUpdate(), in));
		updatePositionParams();
		logger.debug("Enter state for symbol {} at time {}",
				state.getContractParams().getSymbol(),
				serviceLocator.getTerminal().getCurrentTime());
		return null;
	}

	@Override
	public SMExit input(Object data) {
		updatePositionParams();
		return null;
	}

}
