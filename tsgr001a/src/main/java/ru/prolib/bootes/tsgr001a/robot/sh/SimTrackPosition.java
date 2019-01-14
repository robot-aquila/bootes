package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.TStampedVal;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class SimTrackPosition extends CommonHandler implements SMInputAction {
	public static final String E_CLOSE_POSITION = "CLOSE_POSITION";
	
	public interface Ctrl {
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
		registerExit(E_CLOSE_POSITION);
		in = registerInput(this);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		synchronized ( state ) {
			Interval trade_period = state.getContractParams().getTradeAllowedPeriod(); 
			triggers.add(newExitOnTimer(terminal,trade_period.getEnd(), in));
			triggers.add(newTriggerOnEvent(state.getSecurity().onLastTrade(), in));
		}
		return null;
	}

	@Override
	public SMExit input(Object data) {
		Instant curr_time = serviceLocator.getTerminal().getCurrentTime();
		Security security = null;
		Speculation spec = null;
		synchronized ( state ) {
			security = state.getSecurity();
			spec = state.getActiveSpeculation();
		}
		synchronized ( spec ) {
			CDecimal last_price = security.getLastTrade().getPrice();
			if ( data instanceof Instant ) {
				spec.setFlags(spec.getFlags() | Speculation.SF_TIMEOUT);
				return getExit(E_CLOSE_POSITION);
			}
			
			TStampedVal<CDecimal> low = spec.getLowestPrice(), high = spec.getHighestPrice();
			if ( low == null || last_price.compareTo(low.getValue()) < 0 ) {
				spec.setLowestPrice(curr_time, last_price);
			}
			if ( high == null || last_price.compareTo(high.getValue()) > 0 ) {
				spec.setHighestPrice(curr_time, last_price);
			}
			
			if ( ctrl.isTakeProfit(last_price, spec)
			  || ctrl.isStopLoss(last_price, spec) )
			{
				return getExit(E_CLOSE_POSITION);
			}
			
			if ( (Speculation.SF_BREAK_EVEN & spec.getFlags()) == 0
			  && ctrl.isBreakEven(last_price, spec) )
			{
				spec.setFlags(spec.getFlags() | Speculation.SF_BREAK_EVEN);
				spec.setStopLossAt(spec.getEntryPoint().getPrice());
			}
		}
		return null;
	}

}
