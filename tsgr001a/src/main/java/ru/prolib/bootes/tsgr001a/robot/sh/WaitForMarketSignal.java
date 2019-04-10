package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Duration;
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
import ru.prolib.bootes.lib.data.ts.S3CESDSignalTrigger;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.FilterSet;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSetState;
import ru.prolib.bootes.lib.data.ts.filter.impl.CooldownFilter;
import ru.prolib.bootes.lib.report.s3rep.utils.S3RLastSpeculationEndTime;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RoboServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;
import ru.prolib.bootes.tsgr001a.robot.filter.ByTrendT1;
import ru.prolib.bootes.tsgr001a.robot.filter.FilterFCSD;
import ru.prolib.bootes.tsgr001a.robot.filter.MADevLimit;
import ru.prolib.bootes.tsgr001a.robot.filter.StopLossGtATR;

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
	private final S3CESDSignalTrigger trigger;
	private final SMInput in;
	private final IFilterSet<S3TradeSignal> filters;
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
		trigger = new S3CESDSignalTrigger();
		filters = new FilterSet<S3TradeSignal>()
			.addFilter(new CooldownFilter(new S3RLastSpeculationEndTime(
					roboServices.getTradesReport()),
					Duration.ofMinutes(30)
				))
			.addFilter(new StopLossGtATR(state))
			.addFilter(new MADevLimit(state))
			.addFilter(new ByTrendT1(state)) // filtered too much, not so effective, check it
			.addFilter(new FilterFCSD(state));
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
		
		S3TradeSignal signal = null;
		RMContractStrategyPositionParams cspp = null;
		switch ( trigger.getSignal(curr_time) ) {
		case BUY:
			synchronized ( state ) {
				cspp = state.getPositionParams();
				signal = new S3TradeSignal(
						SignalType.BUY,
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
			}
			break;
		case SELL:
			synchronized ( state ) {
				cspp = state.getPositionParams();
				signal = new S3TradeSignal(
						SignalType.SELL,
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
			}
			break;
		default:
			return null;
		}

		IFilterSetState result = filters.approve(signal);
		if ( result.hasDeclined() ) {
			//logger.debug("Signal declined: {}", toString(result));
			return null;
		} else {
			//logger.debug("Signal approved: {}", toString(result));
		}
		
		synchronized ( state ) {
			state.setActiveSpeculation(new Speculation(signal));
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
			trigger.setSource(state.getSeriesHandlerT0().getSeries().getSeries(SetupT0.SID_PVC_WAVG));
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
