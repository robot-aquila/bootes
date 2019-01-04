package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.mscan.sensors.TradeSignal;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class WaitForMarketSignalResult extends CommonHandler
	implements SMInputAction, SMExitAction
{
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(WaitForMarketSignalResult.class);
	}
	
	private final SMInput in;
	
	public WaitForMarketSignalResult(AppServiceLocator serviceLocator,
			RobotState state)
	{
		super(serviceLocator, state);
		registerExit(E_STOP_TRADING);
		registerExit(E_TAKE_PROFIT);
		registerExit(E_STOP_LOSS);
		in = registerInput(this);
		setExitAction(this);
	}

	@Override
	public SMExit input(Object data) {
		CDecimal last = state.getSecurity().getLastTrade().getPrice();
		Instant time = serviceLocator.getTerminal().getCurrentTime();
		if ( data instanceof Instant ) {
			logger.debug("Closed by time @{} time={}", last, time);
			return getExit(E_STOP_TRADING);
		}
		
		TradeSignal signal = state.getActiveSignal();
		CDecimal pr = signal.getExpectedPrice();
		CDecimal tp = signal.getTakeProfitPts();
		CDecimal sl = signal.getStopLossPts();
		switch ( signal.getType() ) {
		case BUY:
			if ( last.compareTo(pr.add(tp)) >= 0 ) {
				logger.debug("Take profit @{} time={}", last, time);
				return getExit(E_TAKE_PROFIT);
			}
			if ( last.compareTo(pr.subtract(sl)) <= 0 ) {
				logger.debug("Stop-loss @{} time={}", last, time);
				return getExit(E_STOP_LOSS);
			}
			break;
		case SELL:
			if ( last.compareTo(pr.subtract(tp)) <= 0 ) {
				logger.debug("Take profit @{} time={}", last, time);
				return getExit(E_TAKE_PROFIT);
			}
			if ( last.compareTo(pr.add(sl)) >= 0 ) {
				logger.debug("Stop-loss @{} time={}", last, time);
				return getExit(E_STOP_LOSS);
			}
			break;
		default:
			break;
		}
		return null;
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Interval tap = state.getContractParams().getTradeAllowedPeriod();
		triggers.add(newExitOnTimer(serviceLocator.getTerminal(), tap.getEnd(), in));
		triggers.add(newTriggerOnEvent(state.getSecurity().onLastTrade(), in));
		return null;
	}

	@Override
	public void exit() {
		state.setActiveSignal(null);
	}

}
