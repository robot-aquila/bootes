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
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.sm.OnFinishAction;
import ru.prolib.bootes.lib.sm.OnTimeoutAction;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

/**
 * Open long position for speculative trading (fast or fail).
 */
public class S3OpenPosition extends CommonHandler implements
	SMExitAction,
	OnTimeoutAction.Handler,
	OnFinishAction.Handler
{
	/**
	 * Timeout to open position in seconds.
	 */
	public static final long TIMEOUT_SECONDS = 60 * 5; // 5 minutes
	
	public static final String E_OPEN = "OPEN";
	public static final String E_SKIPPED = "SKIPPED";
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(S3OpenPosition.class);
	}
	
	private final SMInput inTimeout, inFinish;
	private Order order;

	public S3OpenPosition(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		setExitAction(this);
		registerExit(E_OPEN);
		registerExit(E_SKIPPED);
		inTimeout = registerInput(new OnTimeoutAction(this));
		inFinish = registerInput(new OnFinishAction(this));
	}

	/**
	 * For testing purposes only.
	 * <p>
	 * @param order - order instance to set as active order
	 */
	void setOrder(Order order) {
		this.order = order;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return active order instance
	 */
	Order getOrder() {
		return order;
	}

	private boolean cancelOrder() {
		if ( order != null && ! order.getStatus().isFinal() ) {
			try {
				serviceLocator.getTerminal().cancelOrder(order);
			} catch ( OrderException e ) {
				logger.error("Order cancellation failed: ", e);
				return false;
			}
		}
		return true;
	}

	@Override
	public SMExit onInterrupt(Object data) {
		cancelOrder();
		return super.onInterrupt(data);
	}

	@Override
	public SMExit onTimeout(Object data) {
		return cancelOrder() ? null : getExit(E_ERROR);
	}
	
	@Override
	public SMExit onFinish(Object data) {
		return getExit(order.getCurrentVolume().equals(order.getInitialVolume()) ? E_SKIPPED : E_OPEN);
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
		
		CDecimal qty = signal.getExpectedQty();
		if ( qty.compareTo(CDecimalBD.ZERO) == 0 ) {
			return getExit(E_SKIPPED);
		}

		SignalType sig_type = signal.getType();
		OrderAction action;
		CDecimal price = signal.getExpectedPrice();
		switch ( sig_type ) {
		case BUY:
			action = OrderAction.BUY;
			price = price.add(signal.getSlippagePts()); // make trade a bit more valuable for counter agent
			break;
		case SELL:
			action = OrderAction.SELL;
			price = price.subtract(signal.getSlippagePts());
			break;
		default:
			logger.error("Unexpected signal type: {}", sig_type);
			return getExit(E_ERROR);
		}
		
		Terminal terminal = serviceLocator.getTerminal();
		order = terminal.createOrder(
				portfolio.getAccount(),
				security.getSymbol(),
				action,
				qty,
				price
			);
		
		Instant cancell_at = terminal.getCurrentTime().plusSeconds(TIMEOUT_SECONDS);
		triggers.add(newTriggerOnEvent(order.onDone(), inFinish));
		triggers.add(newExitOnTimer(terminal, cancell_at, inTimeout));
		
		try {
			terminal.placeOrder(order);
		} catch ( OrderException e ) {
			logger.error("Order failed: ", e);
			return getExit(E_ERROR);
		}
		
		return null;
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
		CDecimal cum_price = null;
		for ( OrderExecution execution : order.getExecutions() ) {
			CDecimal exec_cum_price = execution.getPricePerUnit().multiply(execution.getVolume());
			if ( cum_price == null ) {
				cum_price = exec_cum_price;
			} else {
				cum_price = cum_price.add(exec_cum_price);
			}
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
