package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.E_NEXT_TRADING;
import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.E_STOP_TRADING;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.TStampedVal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class SimTrackPosition extends CommonHandler
	implements SMInputAction, SMExitAction
{
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SimTrackPosition.class);
	}
	
	public interface Ctrl {
		CDecimal getSpeculationResult(Speculation spec);
		boolean isTakeProfit(CDecimal lastPrice, Speculation spec);
		boolean isStopLoss(CDecimal lastPrice, Speculation spec);
		boolean isBreakEven(CDecimal lastPrice, Speculation spec);
	}
	
	private final Ctrl ctrl;
	private final SMInput in;

	public SimTrackPosition(AppServiceLocator serviceLocator,
			RobotState state,
			Ctrl ctrl)
	{
		super(serviceLocator, state);
		this.ctrl = ctrl;
		registerExit(E_STOP_TRADING);
		registerExit(E_NEXT_TRADING);
		in = registerInput(this);
		setExitAction(this);
	}
	
	protected void closePosition(Instant time,
			CDecimal price,
			Speculation spec,
			Security security)
	{
		CDecimal volume = spec.getEntryPoint().getSize();
		Tick exit = Tick.of(TickType.TRADE,
				time,
				price,
				volume,
				security.priceToValueWR(price, volume)
			);
		spec.setExitPoint(exit);
		int flags = spec.getFlags() | Speculation.SF_STATUS_CLOSED;
		CDecimal result = ctrl.getSpeculationResult(spec);
		if ( result.compareTo(CDecimalBD.ZERO) > 0 ) {
			flags |= Speculation.SF_RESULT_PROFIT;
		}
		spec.setFlags(flags);
		logger.debug("Closed with result: {}", result);
		logger.debug("Closed speculation: {}", spec);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		synchronized ( state ) {
			triggers.add(newExitOnTimer(serviceLocator.getTerminal(),
				state.getContractParams().getTradeAllowedPeriod().getEnd(), in));
			triggers.add(newTriggerOnEvent(state.getSecurity().onLastTrade(), in));
		}
		return null;
	}

	@Override
	public SMExit input(Object data) {
		Instant currTime = serviceLocator.getTerminal().getCurrentTime();
		Security security = null;
		Speculation spec = null;
		synchronized ( state ) {
			security = state.getSecurity();
			spec = state.getActiveSpeculation();
		}
		synchronized ( spec ) {
			CDecimal lastPrice = security.getLastTrade().getPrice();
			if ( data instanceof Instant ) {
				logger.debug("Timeout at {} @ {}", currTime, lastPrice);
				spec.setFlags(spec.getFlags() | Speculation.SF_TIMEOUT);
				closePosition(currTime, lastPrice, spec, security);
				return getExit(E_STOP_TRADING);
			}
			
			TStampedVal<CDecimal> low = spec.getLowestPrice(), high = spec.getHighestPrice();
			if ( low == null || lastPrice.compareTo(low.getValue()) < 0 ) {
				spec.setLowestPrice(currTime, lastPrice);
			}
			if ( high == null || lastPrice.compareTo(high.getValue()) > 0 ) {
				spec.setHighestPrice(currTime, lastPrice);
			}
			
			if ( ctrl.isTakeProfit(lastPrice, spec) ) {
				logger.debug("Take-Profit at {} @ {}", currTime, lastPrice);
				closePosition(currTime, lastPrice, spec, security);
				return getExit(E_NEXT_TRADING);
			}
			if ( ctrl.isStopLoss(lastPrice, spec) ) {
				logger.debug("Stop-Loss at {} @ {}", currTime, lastPrice);
				closePosition(currTime, lastPrice, spec, security);
				return getExit(E_NEXT_TRADING);
			}
			
			if ( (Speculation.SF_BREAK_EVEN & spec.getFlags()) == 0
			  && ctrl.isBreakEven(lastPrice, spec) )
			{
				CDecimal prev_sl = spec.getStopLossAt();
				spec.setFlags(spec.getFlags() | Speculation.SF_BREAK_EVEN);
				spec.setStopLossAt(spec.getEntryPoint().getPrice());
				logger.debug("Break-Even at {} @ {}", currTime, lastPrice);
				logger.debug("Stop-Loss moved from {} to {}", prev_sl, spec.getStopLossAt());
			}
		}
		return null;
	}

	@Override
	public void exit() {

	}

}
