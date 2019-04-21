package ru.prolib.bootes.lib.robo.s3;

import java.time.Instant;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTrigger;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSetState;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3SignalDeterminable;

abstract public class S3WaitForMarketSignal extends SMStateHandlerEx implements SMInputAction {

	/**
	 * The current trading period has ended. A trading session may contain
	 * one or more trading periods which are defined by trading strategy.
	 * Trading schedule is not directly related to exchange trading hours.
	 * It's related to trading strategy requirements. 
	 */
	public static final String E_TRADING_END = "TRADING_END";
	/**
	 * Period of exchange's trading session has ended.
	 */
	public static final String E_SESSION_END = "SESSION_END";
	public static final String E_BUY = "BUY";
	public static final String E_SELL = "SELL";
	
	protected final AppServiceLocator serviceLocator;
	protected final IS3SignalDeterminable state;
	private final SMInput in;
	protected Interval tradingPeriod, trackingPeriod;

	public S3WaitForMarketSignal(AppServiceLocator serviceLocator,
									 IS3SignalDeterminable state)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
		registerExit(E_SESSION_END);
		registerExit(E_TRADING_END);
		registerExit(E_BUY);
		registerExit(E_SELL);
		in = registerInput(this);
	}

	private CDecimal getLastPrice() {
		return state.getSecurity().getLastTrade().getPrice();
	}

	@Override
	public SMExit input(Object data) {
		Instant curr_time = serviceLocator.getTerminal().getCurrentTime();
		onSignalDetectionTrigger(curr_time);
		
		if ( ! tradingPeriod.contains(curr_time) ) {
			return null; // Outside of trading period
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
			return null;
		}
		
		state.setActiveSpeculation(new S3Speculation(signal));
		return getExit(sig_type == SignalType.BUY ? E_BUY : E_SELL);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Scheduler scheduler = serviceLocator.getScheduler();
		Instant curr_time = scheduler.getCurrentTime();
		synchronized ( state ) {
			state.setActiveSpeculation(null);
			tradingPeriod = state.getContractStrategy().getTradingTimetable().getActiveOrComing(curr_time);
			trackingPeriod = state.getContractParams().getDataTrackingPeriod();
		}
		triggers.add(newExitOnTimer(scheduler, trackingPeriod.getEnd(), E_SESSION_END));
		triggers.add(newExitOnTimer(scheduler, tradingPeriod.getEnd(), E_TRADING_END));
		triggers.add(createTriggerInitiator(in));
		return null;
	}
	
	/**
	 * Create trigger-initiator which will initiate signal check as reaction on
	 * some kind event. For example it may be recurrent call based on timer. Or
	 * it may be call based on event of data series length update. etc...
	 * <p>
	 * @param target_input - input to receive data
	 * @return trigger
	 */
	abstract protected SMTrigger createTriggerInitiator(SMInput target_input);
	
	/**
	 * Called each time when trigger-initiator signals for event.
	 * <p>
	 * @param curr_time - current time (according to app scheduler)
	 */
	abstract protected void onSignalDetectionTrigger(Instant curr_time);

}