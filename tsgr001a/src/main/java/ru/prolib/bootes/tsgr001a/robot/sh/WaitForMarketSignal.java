package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.mscan.sensors.TradeSignal;
import ru.prolib.bootes.tsgr001a.mscan.sensors.S3CESDSignalTrigger;
import ru.prolib.bootes.tsgr001a.mscan.sensors.SignalType;
import ru.prolib.bootes.tsgr001a.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;

public class WaitForMarketSignal extends CommonHandler implements SMInputAction {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(WaitForMarketSignal.class);
	}
	
	private final CommonActions ca;
	private final S3CESDSignalTrigger trigger;
	private final SMInput in;

	public WaitForMarketSignal(AppServiceLocator serviceLocator,
			RobotState state,
			CommonActions ca)
	{
		super(serviceLocator, state);
		this.ca = ca;
		registerExit(E_STOP_TRADING);
		registerExit(E_BUY);
		registerExit(E_SELL);
		in = registerInput(this);
		trigger = new S3CESDSignalTrigger();
	}
	
	private CDecimal getLastPrice() {
		return state.getSecurity().getLastTrade().getPrice();
	}
	
	private SMExit determineExit() {
		ca.updatePositionParams(serviceLocator, state);
		Instant time = serviceLocator.getTerminal().getCurrentTime();
		RMContractStrategyPositionParams cspp = null;
		switch ( trigger.getSignal(time) ) {
		case BUY:
			 cspp = state.getPositionParams();
			 state.setActiveSignal(new TradeSignal(
					SignalType.BUY,
					time,
					getLastPrice(),
					CDecimalBD.of((long) cspp.getNumberOfContracts()),
					cspp.getTakeProfitPts(),
					cspp.getStopLossPts()
			    ));
			logger.debug("Detected: {}", state.getActiveSignal());
			return getExit(E_BUY);
		case SELL:
			cspp = state.getPositionParams();
			state.setActiveSignal(new TradeSignal(
					SignalType.SELL,
					time,
					getLastPrice(),
					CDecimalBD.of((long) cspp.getNumberOfContracts()),
					cspp.getTakeProfitPts(),
					cspp.getStopLossPts()
				));
			logger.debug("Detected: {}", state.getActiveSignal());
			return getExit(E_SELL);
		default:
			return null;
		}
	}

	@Override
	public SMExit input(Object data) {
		return determineExit();
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Interval tap = state.getContractParams().getTradeAllowedPeriod();
		triggers.add(newExitOnTimer(serviceLocator.getTerminal(), tap.getEnd(), E_STOP_TRADING));
		triggers.add(newTriggerOnEvent(state.getSeriesHandlerT0().getSeries().onLengthUpdate(), in));
		trigger.setSource(state.getSeriesHandlerT0().getSeries().getSeries(SetupT0.SID_PVC_WAVG));
		state.setActiveSignal(null);
		return null;
		//return determineExit();
	}

}
