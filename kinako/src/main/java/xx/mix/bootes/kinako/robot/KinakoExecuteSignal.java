package xx.mix.bootes.kinako.robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderEvent;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.PortfolioException;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.aquila.core.sm.OnTimeoutAction;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import xx.mix.bootes.kinako.KINAKORobotComp;
import xx.mix.bootes.kinako.KINAKORobotConfig;
import xx.mix.bootes.kinako.service.VVOrderRecom;
import xx.mix.bootes.kinako.service.VVOrderType;

public class KinakoExecuteSignal extends SMStateHandlerEx
	implements SMExitAction, OnTimeoutAction.Handler
{
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(KinakoExecuteSignal.class);
	}
	
	public static final String E_OK = "OK";
	
	static class OnOrderDone implements SMInputAction {
		private final KinakoExecuteSignal owner;
		
		OnOrderDone(KinakoExecuteSignal owner) {
			this.owner = owner;
		}
		
		@Override
		public SMExit input(Object data) {
			return owner.onOrderDone((OrderEvent) data);
		}
		
	}
	
	protected final AppServiceLocator serviceLocator;
	protected final KinakoRobotServiceLocator kinakoServiceLocator;
	protected final KinakoRobotData data;
	protected final SMInput inTimeout, inOrderDone;
	private final List<OrderRecomState> recomStateList = new ArrayList<>();
	private final Map<Symbol, OrderRecomState> recomStateMap = new HashMap<>();
	private final Set<Order> orderList = new HashSet<>();
	
	public KinakoExecuteSignal(
			AppServiceLocator service_locator,
			KinakoRobotServiceLocator kinako_service_locator,
			KinakoRobotData robot_data
		)
	{
		this.serviceLocator = service_locator;
		this.kinakoServiceLocator = kinako_service_locator;
		this.data = robot_data;
		registerExit(E_OK);
		setExitAction(this);
		inTimeout = registerInput(new OnTimeoutAction(this));
		inOrderDone = registerInput(new OnOrderDone(this));
	}
	
	private Account getAccount() {
		try {
			return serviceLocator.getTerminal().getDefaultPortfolio().getAccount();
		} catch ( PortfolioException e ) {
			logger.error("Unexpected exception: ", e);
			throw new IllegalStateException(e);
		}
	}
	
	private OrderAction toOrderAction(VVOrderType type) {
		switch ( type ) {
		case BUY_LONG:
		case COVER_SHORT:
			return OrderAction.BUY;
		case SELL_LONG:
		case SELL_SHORT:
			return OrderAction.SELL;
		default:
			throw new IllegalArgumentException("Unsupported type: " + type);
		}
	}
	
	private boolean allOrdersDone() {
		for ( Order order : orderList ) {
			if ( ! order.getStatus().isFinal() ) {
				return false;
			}
		}
		return true;
	}
	
	private long getTimeoutSeconds() {
		return 300L; // 5 minutes
	}
	
	private boolean isUseLimitOrders() {
		KINAKORobotConfig conf = serviceLocator.getConfig().getSection(KINAKORobotComp.CONFIG_SECTION_ID);
		return conf.isUseLimitOrders();
	}
	
	private void cancelQuietly(Order order) {
		if ( ! order.getStatus().isFinal() ) {
			try {
				order.getTerminal().cancelOrder(order);
			} catch ( OrderException e ) {
				logger.error("Error cancelling order: ", e);
			}
		}
	}
	
	private void cancelQuietlyAll() {
		for ( Order order : orderList ) {
			cancelQuietly(order);
		}
	}
	
	@Override
	public SMExit onTimeout(Object time) {
		cancelQuietlyAll();
		return null;
	}
	
	private SMExit onOrderDone(OrderEvent event) {
		Order order = event.getOrder();
		if ( ! orderList.contains(order) ) {
			return null;
		}
		Symbol symbol = order.getSymbol();
		OrderRecomState recom_state = recomStateMap.get(symbol);
		if ( recom_state == null ) {
			logger.error("State record not found for: {}", symbol);
			return null;
		}
		
		CDecimal init_vol = order.getInitialVolume(), curr_vol = order.getCurrentVolume();
		recom_state.setExecutedVolume(init_vol.subtract(curr_vol));
		recom_state.setStatus(curr_vol.compareTo(CDecimalBD.ZERO) == 0 ? OrderRecomStatus.FULL : OrderRecomStatus.PART);
		if ( order.getStatus().isError() ) {
			recom_state.setComment(order.getSystemMessage());
		}
		return allOrdersDone() ? getExit(E_OK) : null;
	}
	
	/**
	 * Convert VectorVest recommendation symbol to symbol of local security.
	 * <p>
	 * @param recom_symbol - symbol from VectorVest order recommendation
	 * @return symbol of local security or null if no suitable security was found
	 */
	private Symbol findLocalSymbol(String recom_symbol) {
		SymbolAliases selected_symbols = data.getSelectedSymbols();
		if ( ! selected_symbols.isKnownAlias(recom_symbol) ) {
			return null;
		}
		for ( Symbol symbol : selected_symbols.getSymbols(recom_symbol) ) {
			return symbol;
		}
		return null;
	}
	
	private Security getSecurity(Symbol symbol) {
		try {
			return serviceLocator.getTerminal().getSecurity(symbol);
		} catch ( SecurityException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		}
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		triggers.add(newTriggerOnEvent(terminal.onOrderDone(), inOrderDone));
		triggers.add(newTriggerOnTimer(terminal, terminal.getCurrentTime().plusSeconds(getTimeoutSeconds()), inTimeout));
		Account account = getAccount();
		Order order = null;
		boolean use_limit = isUseLimitOrders();
		for ( VVOrderRecom recom : data.getCurrentSignal().getRecommendations() ) {
			Symbol local_symbol = findLocalSymbol(recom.getSymbol());
			OrderRecomState recom_state = new OrderRecomState(
					recom.getType(),
					recom.getSymbol(),
					recom.getVolume(),
					local_symbol
				);
			recomStateList.add(recom_state);
			if ( local_symbol == null ) {
				recom_state.setStatus(OrderRecomStatus.ERROR);
				recom_state.setComment("Suitable symbol not found: " + recom.getSymbol());
				continue;
			}
			recomStateMap.put(local_symbol, recom_state);
			if ( use_limit ) {
				OrderAction action = toOrderAction(recom.getType());
				Security security = getSecurity(local_symbol);
				CDecimal max_price = security.getUpperPriceLimit(), min_price = security.getLowerPriceLimit();
				if ( max_price == null || min_price == null ) {
					recom_state.setStatus(OrderRecomStatus.ERROR);
					recom_state.setComment("Min/max price not defined: " + local_symbol);
					continue;
				}
				order = terminal.createOrder(
						account,
						local_symbol,
						action,
						recom.getVolume(),
						(action == OrderAction.BUY || action == OrderAction.COVER) ? max_price : min_price
					);
			} else {
				order = terminal.createOrder(
						account,
						local_symbol,
						toOrderAction(recom.getType()),
						recom.getVolume()
					);
			}
			try {
				terminal.placeOrder(order);
				recom_state.setStatus(OrderRecomStatus.ACTIVE);
				orderList.add(order);
				Object args[] = { recom.getType(), recom.getVolume(), local_symbol };
				logger.debug("Order placed: {} x{} shares of {}", args);
			} catch ( OrderException e ) {
				recom_state.setStatus(OrderRecomStatus.ERROR);
				recom_state.setComment("Error placing order: " + e.getMessage());
				logger.error("Error placing order: ", e);
			}
		}
		return orderList.size() > 0 ? null : getExit(E_OK);
	}

	@Override
	public void exit() {
		cancelQuietlyAll();
		String LN = System.lineSeparator();
		StringBuilder sb = new StringBuilder().append("Signal execution report: ").append(LN);
		for ( OrderRecomState recom_state : recomStateList ) {
			sb.append("As result of ").append(recom_state.getType())
				.append(" x").append(recom_state.getVolume())
				.append(" of ").append(recom_state.getLocalSymbol())
				.append(" qty executed ").append(recom_state.getExecutedVolume())
				.append(", status ").append(recom_state.getStatus())
				.append(", comment ").append(recom_state.getComment())
				.append(LN);
		}
		kinakoServiceLocator.getBotService().sendNotification(sb.toString());
	}
	
}
