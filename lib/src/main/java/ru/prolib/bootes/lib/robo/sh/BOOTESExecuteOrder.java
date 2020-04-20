package ru.prolib.bootes.lib.robo.sh;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.sm.*;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.RobotStateListener;

public class BOOTESExecuteOrder extends SMStateHandlerEx
	implements OnTimeoutAction.Handler, OnFinishAction.Handler, SMExitAction
{
	static final Logger logger = LoggerFactory.getLogger(BOOTESExecuteOrder.class);
	
	/**
	 * Order was executed completely.
	 */
	public static final String E_EXEC_FULL	= "EXEC_FULL";
	
	/**
	 * Order was executed partially.
	 */
	public static final String E_EXEC_PART	= "EXEC_PART";
	
	/**
	 * Order was cancelled without executions.
	 * This case may appear for example when order was cancelled manually.
	 */
	public static final String E_EXEC_NONE = "EXEC_NONE";
	
	/**
	 * Order was not executed even partially and was cancelled due to timeout.
	 */
	public static final String E_TIMEOUT	= "TIMEOUT";
	
	protected final AppServiceLocator serviceLocator;
	protected final RobotStateListener stateListener;
	private final SMInput inTimeout, inFinish;
	private boolean isInterrupt = false, isTimeout = false;

	public BOOTESExecuteOrder(AppServiceLocator service_locator, RobotStateListener state_listener) {
		this.serviceLocator = service_locator;
		this.stateListener = state_listener;
		setExitAction(this);
		registerExit(E_EXEC_NONE);
		registerExit(E_EXEC_FULL);
		registerExit(E_EXEC_PART);
		registerExit(E_TIMEOUT);
		inTimeout = registerInput(new OnTimeoutAction(this));
		inFinish = registerInput(new OnFinishAction(this));
		setIncomingDataType(OrderDefinition.class);
		setResultDataType(Order.class);
	}
	
	public BOOTESExecuteOrder(AppServiceLocator service_locator) {
		this(service_locator, null);
	}
	
	private Order getOrder() {
		return getResultData();
	}
	
	/**
	 * Cancel order if active.
	 * <p>
	 * @return -1 - in case of error,
	 * 0 - cancellation scheduled, have to wait for order finalization,
	 * 1 - order not exists or already finished, can exit immediately
	 */
	private int cancelOrder() {
		Order order = getOrder();
		if ( order != null && ! order.getStatus().isFinal() ) {
			try {
				serviceLocator.getTerminal().cancelOrder(order);
				return 0;
			} catch ( OrderException e ) {
				logger.error("Order cancellation failed: ", e);
				return -1;
			}
		}
		return 1;
	}

	@Override
	public SMExit onInterrupt(Object data) {
		isInterrupt = true;
		switch ( cancelOrder() ) {
		case -1: return getExit(E_ERROR);
		case  0: return null;
		case  1: return getExit(E_INTERRUPT);
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public SMExit onTimeout(Object data) {
		isTimeout = true;
		switch ( cancelOrder() ) {
		case -1: return getExit(E_ERROR);
		case  0: return null;
		case  1: return getExit(E_TIMEOUT);
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public SMExit onFinish(Object data) {
		if ( isInterrupt ) {
			return getExit(E_INTERRUPT);
		}
		Order order = getOrder();
		CDecimal initial_qty = order.getInitialVolume();
		CDecimal executed_qty = initial_qty.subtract(order.getCurrentVolume());
		if ( ZERO.compareTo(executed_qty) == 0 ) {
			// None executed
			return getExit(isTimeout ? E_TIMEOUT : E_EXEC_NONE);
		}
		return getExit(executed_qty.compareTo(initial_qty) == 0 ? E_EXEC_FULL : E_EXEC_PART);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		
		OrderDefinition def = getIncomingData();
		CDecimal qty = def.getQty();
		if ( qty.compareTo(ZERO) == 0 ) {
			logger.warn("Requested order with zero qty: {}", def);
			return getExit(E_ERROR);
		}
		
		Terminal term = serviceLocator.getTerminal();
		Order order = term.createOrder(def.getAccount(),
				def.getSymbol(),
				def.getType(),
				def.getAction(),
				qty,
				def.getPrice(),
				def.getComment()
			);
		setResultData(order);
		triggers.add(newTriggerOnEvent(order.onDone(), inFinish));
		triggers.add(newTriggerOnTimer(term, term.getCurrentTime().plusMillis(def.getMaxExecutionTime()), inTimeout));

		try {
			term.placeOrder(order);
		} catch ( OrderException e ) {
			logger.error("Order failed: ", e);
			return getExit(E_ERROR);
		}
		
		return null;
	}

	@Override
	public void exit() {
		if ( stateListener != null && getOrder() != null ) {
			stateListener.orderFinished(getOrder());
		}
	}

}
