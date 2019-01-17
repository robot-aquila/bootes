package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.TStampedVal;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.mscan.sensors.TradeSignal;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class SimTrackPosition extends CommonHandler implements SMInputAction, SMExitAction {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SimTrackPosition.class);
	}
	
	public static final String E_CLOSE_POSITION = "CLOSE_POSITION";
	
	private final SMInput in;
	private Speculation spec;
	private TradeSignal sig;
	private CDecimal stopLoss, takeProfit, breakEven;
	private TStampedVal<CDecimal> low, high;

	public SimTrackPosition(AppServiceLocator serviceLocator,
			RobotState state)
	{
		super(serviceLocator, state);
		setExitAction(this);
		registerExit(E_CLOSE_POSITION);
		in = registerInput(this);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		Instant curr_time = terminal.getCurrentTime();
		synchronized ( state ) {
			Interval trade_period = state.getContractParams().getTradeAllowedPeriod(); 
			triggers.add(newExitOnTimer(terminal,trade_period.getEnd(), in));
			triggers.add(newTriggerOnEvent(state.getSecurity().onLastTrade(), in));
			spec = state.getActiveSpeculation();
		}
		
		high = null;
		low = null;
		
		synchronized ( spec ) {
			sig = spec.getTradeSignal();
			CDecimal price = spec.getEntryPoint().getPrice();
			switch ( sig.getType() ) {
			case BUY:
				stopLoss = price.subtract(sig.getStopLossPts());
				takeProfit = price.add(sig.getTakeProfitPts());
				breakEven = price.add(sig.getStopLossPts().multiply(2L));
				logger.debug("{} Long stop-loss @{}", curr_time, stopLoss);
				logger.debug("{} Long take-profit @{}", curr_time, takeProfit);
				logger.debug("{} Long break-even @{}", curr_time, breakEven);
				break;
			case SELL:
				stopLoss = price.add(sig.getStopLossPts());
				takeProfit = price.subtract(sig.getTakeProfitPts());
				breakEven = price.subtract(sig.getStopLossPts().multiply(2L));
				logger.debug("{} Short stop-loss @{}", curr_time, stopLoss);
				logger.debug("{} Short take-profit @{}", curr_time, takeProfit);
				logger.debug("{} Short break-even @{}", curr_time, breakEven);
				break;
			default:
				throw new IllegalStateException("Unsupported signal type: " + sig.getType());
			}
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
			
			if ( low == null || last_price.compareTo(low.getValue()) < 0 ) {
				low = new TStampedVal<>(curr_time, last_price);
			}
			if ( high == null || last_price.compareTo(high.getValue()) > 0 ) {
				high = new TStampedVal<>(curr_time, last_price);
			}
			
			switch ( sig.getType() ) {
			case BUY:
				if ( last_price.compareTo(takeProfit) >= 0 ) {
					logger.debug("{} Close long by take-profit @{}", curr_time, last_price);
					return getExit(E_CLOSE_POSITION);
				}
				if ( last_price.compareTo(stopLoss) <= 0 ) {
					logger.debug("{} Close long by stop-loss @{}", curr_time, last_price);
					return getExit(E_CLOSE_POSITION);
				}
				if ( breakEven != null
				  && (Speculation.SF_BREAK_EVEN & spec.getFlags()) == 0
				  && last_price.compareTo(breakEven) >= 0 )
				{
					spec.setFlags(spec.getFlags() | Speculation.SF_BREAK_EVEN);
					// TODO: more precise stop-loss calculation
					stopLoss = spec.getEntryPoint().getPrice();
					logger.debug("{} Long became a break-even @{}", curr_time, last_price);
					logger.debug("{} New stop-loss is @{}", curr_time, stopLoss);
				}
				
				break;
			case SELL:
				if ( last_price.compareTo(takeProfit) <= 0 ) {
					logger.debug("{} Close short by take-profit @{}", curr_time, last_price);
					return getExit(E_CLOSE_POSITION);
				}
				if ( last_price.compareTo(stopLoss) >= 0 ) {
					logger.debug("{} Close short by stop-loss @{}", curr_time, last_price);
					return getExit(E_CLOSE_POSITION);
				}
				if ( breakEven != null
				  && (Speculation.SF_BREAK_EVEN & spec.getFlags()) == 0
				  && last_price.compareTo(breakEven) <= 0 )
				{
					spec.setFlags(spec.getFlags() | Speculation.SF_BREAK_EVEN);
					// TODO: more precise stop-loss calculation
					stopLoss = spec.getEntryPoint().getPrice();
					logger.debug("{} Short became a break-even @{}", curr_time, last_price);
					logger.debug("{} New stop-loss is @{}", curr_time, stopLoss);
				}
				break;
			default:
				throw new IllegalStateException();
			}

		}
		return null;
	}

	@Override
	public void exit() {
		logger.debug(" low={}", low);
		logger.debug("high={}", high);
	}

}
