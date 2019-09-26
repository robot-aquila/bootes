package xx.mix.bootes.kinako.robot;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
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

public class KinakoSubscribeSymbols extends SMStateHandlerEx
	implements SMExitAction, OnTimeoutAction.Handler
{
	private static final Logger logger;
	public static final String E_OK = "OK";
	public static final String E_NOT_FOUND = "NOT_FOUND";
	
	static {
		logger = LoggerFactory.getLogger(KinakoSubscribeSymbols.class);
	}
	
	static class OnSecurityAvailable implements SMInputAction {
		private final KinakoSubscribeSymbols owner;
		
		OnSecurityAvailable(KinakoSubscribeSymbols owner) {
			this.owner = owner;
		}

		@Override
		public SMExit input(Object data) {
			return owner.onSecurityAvailable((SecurityEvent) data);
		}
		
	}

	protected final AppServiceLocator serviceLocator;
	protected final KinakoRobotData data;
	protected final SMInput inTimeout, inSecurityAvailable;
	private SymbolAliases selected_symbols;
	
	public KinakoSubscribeSymbols(
			AppServiceLocator service_locator,
			KinakoRobotData robot_data
		)
	{
		this.serviceLocator = service_locator;
		this.data = robot_data;
		registerExit(E_OK);
		registerExit(E_NOT_FOUND);
		setExitAction(this);
		inTimeout = registerInput(new OnTimeoutAction(this));
		inSecurityAvailable = registerInput(new OnSecurityAvailable(this));
	}
	
	private long getTimeoutSeconds() {
		return 30L;
	}
	
	private int getRecommendationsCount() {
		return data.getCurrentSignal().getRecommendations().size();
	}
	
	private int getSelectedSymbolsCount() {
		return selected_symbols.getAliases().size();
	}
	
	private SMExit checkExitState() {
		return getSelectedSymbolsCount() == getRecommendationsCount() ? getExit(E_OK) : null;
	}
	
	@Override
	public SMExit onTimeout(Object time) {
		return getExit(getSelectedSymbolsCount() > 0 ? E_OK : E_NOT_FOUND);
	}
	
	private SMExit onSecurityAvailable(SecurityEvent event) {
		Symbol symbol = event.getSecurity().getSymbol();
		SymbolAliases involved_symbols = data.getInvolvedSymbols();
		if ( ! involved_symbols.isKnownSymbol(symbol) ) {
			return null;
		}
		String alias = involved_symbols.getAlias(symbol);
		if ( selected_symbols.isKnownAlias(alias) ) {
			return null;
		}
		selected_symbols.addAlias(alias, symbol);
		logger.debug("Symbol selected (available): {} -> {}", alias, symbol);
		return checkExitState();
	}
	
	private boolean isSecurityAvailable(Symbol symbol) {
		Terminal terminal = serviceLocator.getTerminal();
		if ( ! terminal.isSecurityExists(symbol) ) {
			return false;
		}
		try {
			return terminal.getSecurity(symbol).isAvailable();
		} catch ( SecurityException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		}
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Terminal terminal = serviceLocator.getTerminal();
		triggers.add(newTriggerOnEvent(terminal.onSecurityAvailable(), inSecurityAvailable));
		triggers.add(newTriggerOnTimer(terminal, terminal.getCurrentTime().plusSeconds(getTimeoutSeconds()), inTimeout));
		
		SymbolAliases involved_symbols = data.getInvolvedSymbols();
		selected_symbols = new SymbolAliases();
		Set<Symbol> symbols_to_subscribe = new LinkedHashSet<>();
		for ( String alias : involved_symbols.getAliases() ) {
			Symbol found_symbol = null;
			for ( Symbol symbol : involved_symbols.getSymbols(alias) ) {
				if ( isSecurityAvailable(symbol) ) {
					found_symbol = symbol;
					break;
				}
			}
			if ( found_symbol != null ) {
				logger.debug("Symbol selected (exists): {} -> {}", alias, found_symbol);
				selected_symbols.addAlias(alias, found_symbol);
			}
			symbols_to_subscribe.addAll(involved_symbols.getSymbols(alias));
		}
		
		for ( Symbol symbol : symbols_to_subscribe ) {
			logger.debug("Subscribe for symbol: {}", symbol);
			terminal.subscribe(symbol);
		}
		data.setSubscribedSymbols(symbols_to_subscribe);
		
		return checkExitState();
	}

	@Override
	public void exit() {
		data.setSelectedSymbols(selected_symbols);
	}
	
}
