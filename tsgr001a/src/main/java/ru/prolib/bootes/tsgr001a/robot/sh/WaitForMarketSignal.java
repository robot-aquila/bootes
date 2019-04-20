package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.aquila.core.utils.LocalTimeTable;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSetState;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.S3Speculation;
import ru.prolib.bootes.tsgr001a.robot.RoboServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class WaitForMarketSignal extends CommonHandler implements SMInputAction {
	public static final String E_STOP_TRADING = "STOP_TRADING";
	public static final String E_BUY = "BUY";
	public static final String E_SELL = "SELL";
	
	@SuppressWarnings("unused")
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(WaitForMarketSignal.class);
	}
	
	private final CommonActions ca;
	private final SMInput in;
	private Interval tradingPeriod, trackingPeriod;
	private LocalTimeTable timetable;

	public WaitForMarketSignal(AppServiceLocator serviceLocator,
			RoboServiceLocator roboServices,
			RobotState state,
			CommonActions ca)
	{
		super(serviceLocator, state);
		this.ca = ca;
		registerExit(E_STOP_TRADING);
		registerExit(E_BUY);
		registerExit(E_SELL);
		in = registerInput(this);
	}
	
	private CDecimal getLastPrice() {
		return state.getSecurity().getLastTrade().getPrice();
	}
	
	@Override
	public SMExit input(Object data) {
		ca.updatePositionParams(serviceLocator, state);
		Instant curr_time = serviceLocator.getTerminal().getCurrentTime();
		if ( isAfterTrackingPeriod(curr_time) ) {
			return onTrackingPeriodEnd();
		}
		if ( isBeforeTradingPeriod(curr_time) ) {
			return null;
		}
		if ( ! tradingPeriod.contains(curr_time) ) {
			tradingPeriod = timetable.getActiveOrComing(curr_time);
			//logger.debug("New trading period: {}", tradingPeriod);
			return null;
		}
		
		SignalType sig_type = state.getSignalTrigger().getSignal(curr_time);
		if ( sig_type == null || sig_type == SignalType.NONE ) {
			return null;
		}
		RMContractStrategyPositionParams cspp = state.getPositionParams();
		S3TradeSignal signal = new S3TradeSignal(
				sig_type,
				curr_time,
				getLastPrice(),
				CDecimalBD.of((long) cspp.getNumberOfContracts()),
				cspp.getTakeProfitPts(),
				cspp.getStopLossPts(),
				cspp.getSlippagePts(),
				cspp.getTradeGoalCap(),
				cspp.getTradeLossCap(),
				cspp.getBaseCap()
			);

		IFilterSetState result = state.getSignalFilter().approve(signal);
		if ( result.hasDeclined() ) {
			//logger.debug("Signal declined: {}", toString(result));
			return null;
		} else {
			//logger.debug("Signal approved: {}", toString(result));
		}
		
		synchronized ( state ) {
			state.setActiveSpeculation(new S3Speculation(signal));
		}
		//logger.debug("Detected signal: {}", signal);
		return getExit(signal.getType() == SignalType.BUY ? E_BUY : E_SELL);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		Instant curr_time = terminal.getCurrentTime();
		synchronized ( state ) {
			state.setActiveSpeculation(null);
			timetable = state.getContractStrategy().getTradingTimetable();
			tradingPeriod = timetable.getActiveOrComing(curr_time);
			trackingPeriod = state.getContractParams().getDataTrackingPeriod();
			if ( isAfterTrackingPeriod(curr_time) ) {
				return onTrackingPeriodEnd();
			}
			triggers.add(newExitOnTimer(terminal, trackingPeriod.getEnd(), E_STOP_TRADING));
			triggers.add(newTriggerOnEvent(state.getSeriesHandlerT0().getSeries().onLengthUpdate(), in));
		}
		return null;
	}

	String toString(IFilterSetState result) {
		return result.toString();
	}
	
	private boolean isAfterTrackingPeriod(Instant time) {
		return time.compareTo(trackingPeriod.getEnd()) >= 0; 
	}
	
	private boolean isBeforeTradingPeriod(Instant time) {
		return time.compareTo(tradingPeriod.getStart()) < 0;
	}
	
	private SMExit onTrackingPeriodEnd() {
		//logger.debug("Tracking period ended: {}", trackingPeriod);
		return getExit(E_STOP_TRADING);
	}

}
