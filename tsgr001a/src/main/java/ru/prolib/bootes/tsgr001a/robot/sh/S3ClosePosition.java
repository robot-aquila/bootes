package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.sm.OnTimeoutAction;
import ru.prolib.bootes.lib.sm.OnFinishAction;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.RobotStateListener;

public class S3ClosePosition extends CommonHandler implements
	SMExitAction,
	OnTimeoutAction.Handler,
	OnFinishAction.Handler
{
	/**
	 * Timeout to close position.
	 */
	public static final long TIMEOUT_SECONDS = 15;
	
	public static final String E_CLOSED = "CLOSED";
	public static final String E_NEED_CLOSE = "NEED_CLOSE";
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(S3ClosePosition.class);
	}
	
	private final SMInput inTimeout, inFinish;
	private Order order;

	public S3ClosePosition(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		setExitAction(this);
		registerExit(E_CLOSED);
		registerExit(E_NEED_CLOSE);
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
		return getExit(order.getCurrentVolume().compareTo(CDecimalBD.ZERO) == 0 ? E_CLOSED : E_NEED_CLOSE);
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
		
		Tick entry_point, exit_point;
		SignalType sig_type;
		synchronized ( spec ) {
			entry_point = spec.getEntryPoint();
			exit_point = spec.getExitPoint();
			sig_type = spec.getSignalType();
		}
		
		CDecimal entry_size = entry_point.getSize();
		CDecimal qty = exit_point == null ? entry_size : entry_size.subtract(exit_point.getSize());
		if ( qty.compareTo(CDecimalBD.ZERO) <= 0 ) {
			return getExit(E_CLOSED);
		}
		CDecimal price;
		OrderAction action;
		switch ( sig_type ) {
		case BUY:
			// We have to sell to close position.
			// So choose the best possible price.
			action = OrderAction.SELL;
			price = security.getLowerPriceLimit();
			break;
		case SELL:
			action = OrderAction.BUY;
			price = security.getUpperPriceLimit();
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
		
		Tick entry_point, exit_point;
		S3TradeSignal signal;
		synchronized ( spec ) {
			entry_point = spec.getEntryPoint();
			exit_point = spec.getExitPoint();
			signal = spec.getTradeSignal();
		}
		CDecimal prev_cum_price = null, prev_cum_vol = CDecimalBD.ZERO, prev_exec_val = null;
		if ( exit_point != null ) {
			prev_cum_vol = exit_point.getSize();
			prev_cum_price = exit_point.getPrice().multiply(prev_cum_vol);
			prev_exec_val = exit_point.getValue();
		}
		CDecimal order_exec_val = order.getExecutedValue();
		CDecimal total_filled_volume = filled_volume.add(prev_cum_vol);
		CDecimal total_cum_price = prev_cum_price == null ? cum_price : cum_price.add(prev_cum_price);
		CDecimal total_exec_val = prev_exec_val == null ? order_exec_val : prev_exec_val.add(order_exec_val); 
		exit_point = Tick.of(TickType.TRADE,
				order.getTimeDone(),
				total_cum_price.divide(total_filled_volume),
				total_filled_volume,
				total_exec_val
			);
		boolean closed = total_filled_volume.compareTo(entry_point.getSize()) >= 0;
		synchronized ( spec ) {
			spec.setExitPoint(exit_point);
			if ( closed ) {
				spec.setFlags(spec.getFlags() | Speculation.SF_STATUS_CLOSED);
				switch ( signal.getType() ) {
				case BUY:
					spec.setResult(exit_point.getValue().subtract(entry_point.getValue()));
					break;
				case SELL:
					spec.setResult(entry_point.getValue().subtract(exit_point.getValue()));
					break;
				default:
					logger.warn("Unexpected signal type: {}", signal.getType());
					break;
				}
			}
		}
		if ( closed ) {
			listener.speculationClosed();
		}
	}
	
}
