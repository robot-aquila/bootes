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
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.ts.S3CESDSignalTrigger;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.data.ts.TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.FilterSet;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSetState;
import ru.prolib.bootes.lib.data.ts.filter.impl.CooldownFilter;
import ru.prolib.bootes.lib.report.s3rep.utils.S3RLastSpeculationEndTime;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.tsgr001a.robot.RoboServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;

public class WaitForMarketSignal extends CommonHandler implements SMInputAction {
	public static final String E_STOP_TRADING = "STOP_TRADING";
	public static final String E_BUY = "BUY";
	public static final String E_SELL = "SELL";
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(WaitForMarketSignal.class);
	}
	
	private final CommonActions ca;
	private final S3CESDSignalTrigger trigger;
	private final SMInput in;
	private final IFilterSet filters;

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
		filters = new FilterSet()
			.addFilter(new CooldownFilter(new S3RLastSpeculationEndTime(roboServices.getS3Report()), Duration.ofMinutes(30)));
	}
	
	private CDecimal getLastPrice() {
		return state.getSecurity().getLastTrade().getPrice();
	}
	
	@Override
	public SMExit input(Object data) {
		ca.updatePositionParams(serviceLocator, state);
		Instant time = serviceLocator.getTerminal().getCurrentTime();
		TradeSignal signal = null;
		RMContractStrategyPositionParams cspp = null;
		switch ( trigger.getSignal(time) ) {
		case BUY:
			synchronized ( state ) {
				cspp = state.getPositionParams();
				signal = new TradeSignal(
						SignalType.BUY,
						time,
						getLastPrice(),
						CDecimalBD.of((long) cspp.getNumberOfContracts()),
						cspp.getTakeProfitPts(),
						cspp.getStopLossPts()
					);
			}
			break;
		case SELL:
			synchronized ( state ) {
				cspp = state.getPositionParams();
				signal = new TradeSignal(
						SignalType.SELL,
						time,
						getLastPrice(),
						CDecimalBD.of((long) cspp.getNumberOfContracts()),
						cspp.getTakeProfitPts(),
						cspp.getStopLossPts()
					);
			}
			break;
		default:
			return null;
		}

		IFilterSetState result = filters.approve(signal);
		if ( result.hasDeclined() ) {
			logger.debug("Signal skipped due to filters");
			return null;
		}
		
		synchronized ( state ) {
			state.setActiveSpeculation(new Speculation(signal));
		}
		logger.debug("Detected signal: {}", signal);
		return getExit(signal.getType() == SignalType.BUY ? E_BUY : E_SELL);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		Instant curr_time = terminal.getCurrentTime();
		synchronized ( state ) {
			state.setActiveSpeculation(null);
			Interval trade_period = state.getContractParams().getTradeAllowedPeriod();
			if ( curr_time.compareTo(trade_period.getEnd()) >= 0 ) {
				return getExit(E_STOP_TRADING);
			}
			triggers.add(newExitOnTimer(terminal, trade_period.getEnd(), E_STOP_TRADING));
			triggers.add(newTriggerOnEvent(state.getSeriesHandlerT0().getSeries().onLengthUpdate(), in));
			trigger.setSource(state.getSeriesHandlerT0().getSeries().getSeries(SetupT0.SID_PVC_WAVG));
		}
		return null;
	}

}
