package ru.prolib.bootes.lib.robo.s3;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.TStampedVal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.robo.s3.statereq.IS3Speculative;

public class S3TrackPosition extends SMStateHandlerEx implements
	SMInputAction,
	SMExitAction
{
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(S3TrackPosition.class);
	}
	
	public static final String E_CLOSE_POSITION = "CLOSE_POSITION";
	
	protected final AppServiceLocator serviceLocator;
	protected final IS3Speculative state;
	private final SMInput in;
	private S3Speculation spec;
	private S3TradeSignal sig;
	private CDecimal stopLoss, takeProfit, breakEven;
	private TStampedVal<CDecimal> low, high;
	private SubscrHandler subscription;

	public S3TrackPosition(AppServiceLocator serviceLocator,
							IS3Speculative state)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
		setExitAction(this);
		registerExit(E_CLOSE_POSITION);
		in = registerInput(this);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Instant curr_time = serviceLocator.getScheduler().getCurrentTime();
		subscription = serviceLocator.getTerminal().subscribe(state.getSecurity().getSymbol(), MDLevel.L1);
		subscription.getConfirmation().join();
		triggers.add(newTriggerOnTimer(serviceLocator.getScheduler(), state.getContractStrategy()
				.getTradingTimetable()
				.getActiveOrComing(curr_time)
				.getEnd(), in));
		triggers.add(newTriggerOnEvent(state.getSecurity().onLastTrade(), in));
		spec = state.getActiveSpeculation();
		
		high = null;
		low = null;
		
		synchronized ( spec ) {
			sig = spec.getTradeSignal();
			CDecimal price = spec.getEntryPoint().getPrice();
			switch ( sig.getType() ) {
			case BUY:
				spec.setStopLoss(stopLoss = price.subtract(sig.getStopLossPts()));
				spec.setTakeProfit(takeProfit = price.add(sig.getTakeProfitPts()));
				spec.setBreakEven(breakEven = price.add(sig.getStopLossPts().multiply(2L)));
				logger.debug("{} Long stop-loss @{}", curr_time, stopLoss);
				logger.debug("{} Long take-profit @{}", curr_time, takeProfit);
				logger.debug("{} Long break-even @{}", curr_time, breakEven);
				break;
			case SELL:
				spec.setStopLoss(stopLoss = price.add(sig.getStopLossPts()));
				spec.setTakeProfit(takeProfit = price.subtract(sig.getTakeProfitPts()));
				spec.setBreakEven(breakEven = price.subtract(sig.getStopLossPts().multiply(2L)));
				logger.debug("{} Short stop-loss @{}", curr_time, stopLoss);
				logger.debug("{} Short take-profit @{}", curr_time, takeProfit);
				logger.debug("{} Short break-even @{}", curr_time, breakEven);
				break;
			default:
				throw new IllegalStateException("Unsupported signal type: " + sig.getType());
			}
		}
		state.getStateListener().speculationUpdate();
		return null;
	}

	@Override
	public SMExit input(Object data) {
		Instant curr_time = serviceLocator.getScheduler().getCurrentTime();
		CDecimal last_price = state.getSecurity().getLastPrice();
		S3Speculation spec = state.getActiveSpeculation();
		synchronized ( spec ) {
			if ( data instanceof Instant ) {
				spec.setFlags(spec.getFlags() | S3Speculation.SF_TIMEOUT);
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
				  && (S3Speculation.SF_BREAK_EVEN & spec.getFlags()) == 0
				  && last_price.compareTo(breakEven) >= 0 )
				{
					spec.setFlags(spec.getFlags() | S3Speculation.SF_BREAK_EVEN);
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
				  && (S3Speculation.SF_BREAK_EVEN & spec.getFlags()) == 0
				  && last_price.compareTo(breakEven) <= 0 )
				{
					spec.setFlags(spec.getFlags() | S3Speculation.SF_BREAK_EVEN);
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
		subscription.close();
		logger.debug(" low={}", low);
		logger.debug("high={}", high);
	}

}
