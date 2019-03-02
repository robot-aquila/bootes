package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

/**
 * Open long position for speculative trading (fast or fail).
 */
public class S3OpenPosition extends CommonHandler implements SMExitAction {
	/**
	 * Timeout to open position in seconds.
	 */
	public static final long TIMEOUT_SECONDS = 60 * 5; // 5 minutes
	
	public static final String E_OPEN = "OPEN";
	public static final String E_SKIPPED = "SKIPPED";
	public static final String E_NEED_CLOSE = "NEED_CLOSE";
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(S3OpenPosition.class);
	}
	
	public static class OnTimeout implements SMInputAction {
		private final S3OpenPosition handler;
		
		public OnTimeout(S3OpenPosition handler) {
			this.handler = handler;
		}

		@Override
		public SMExit input(Object data) {
			return handler.onTimeout();
		}
		
	}
	
	public static class OnFinish implements SMInputAction {
		private final S3OpenPosition handler;
		
		public OnFinish(S3OpenPosition handler) {
			this.handler = handler;
		}

		@Override
		public SMExit input(Object data) {
			return handler.onFinish();
		}
		
	}
	
	private final SMInput inTimeout, inFinish;
	private Order order;

	public S3OpenPosition(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		inTimeout = registerInput(new OnTimeout(this));
		inFinish = registerInput(new OnFinish(this));
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		
		Speculation spec;
		Security security;
		Portfolio portfolio;
		synchronized ( state ) {
			spec = state.getActiveSpeculation();
			security = state.getSecurity();
			portfolio = state.getPortfolio();
		}
		S3TradeSignal signal;
		synchronized ( spec ) {
			signal = spec.getTradeSignal();
		}
		OrderAction action;
		CDecimal price = security.getLastTrade().getPrice();
		CDecimal qty = signal.getExpectedQty();
		if ( qty.compareTo(CDecimalBD.ZERO) == 0 ) {
			return getExit(E_SKIPPED);
		}

		switch ( signal.getType() ) {
		case BUY:
			action = OrderAction.BUY;
			price = price.add(signal.getSlippagePts()); // make trade a bit more valuable for counteragent
			break;
		case SELL:
			action = OrderAction.SELL;
			price = price.subtract(signal.getSlippagePts());
			break;
		default:
			return getExit(E_SKIPPED);
		}
		
		Terminal terminal = serviceLocator.getTerminal();
		order = terminal.createOrder(
				portfolio.getAccount(),
				security.getSymbol(),
				action,
				qty,
				price
			);
		
		Scheduler scheduler = serviceLocator.getScheduler();
		Instant cancell_at = scheduler.getCurrentTime().plusSeconds(TIMEOUT_SECONDS);
		triggers.add(newTriggerOnEvent(order.onFailed(), inFinish));
		triggers.add(newTriggerOnEvent(order.onFilled(), inFinish));
		triggers.add(newTriggerOnEvent(order.onCancelled(), inFinish));
		triggers.add(newExitOnTimer(scheduler, cancell_at, inTimeout));
		
		try {
			terminal.placeOrder(order);
		} catch ( OrderException e ) {
			logger.error("Order failed: ", e);
			return getExit(E_ERROR);
		}
		
		return null;
	}
	
	SMExit onTimeout() {
		try {
			serviceLocator.getTerminal().cancelOrder(order);
			return null; // wait for order cancel
		} catch ( OrderException e ) {
			logger.error("Order cancellation failed: ", e);
			return getExit(E_ERROR);
		}
	}
	
	SMExit onFinish() {
		return getExit(order.getCurrentVolume().equals(order.getInitialVolume()) ? E_SKIPPED : E_OPEN);
	}

	@Override
	public void exit() {
		CDecimal filled_volume = order.getInitialVolume().subtract(order.getCurrentVolume());
		if ( filled_volume.compareTo(CDecimalBD.ZERO) == 0 ) {
			return;
		}
		Speculation spec;
		RobotStateListener listener;
		synchronized ( state ) {
			spec = state.getActiveSpeculation();
			listener = state.getStateListener();
		}
		CDecimal cum_price = CDecimalBD.ZERO;
		for ( OrderExecution execution : order.getExecutions() ) {
			cum_price = cum_price.add(execution.getPricePerUnit());
		}
		Tick entry = Tick.of(TickType.TRADE,
				order.getTimeDone(),
				cum_price.divide(filled_volume),
				filled_volume,
				order.getExecutedValue()
			);
		spec.setFlags(Speculation.SF_NEW);
		spec.setEntryPoint(entry);
		listener.speculationOpened();
	}

}
