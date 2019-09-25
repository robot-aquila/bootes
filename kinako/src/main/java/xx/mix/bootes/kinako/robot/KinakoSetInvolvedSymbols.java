package xx.mix.bootes.kinako.robot;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import xx.mix.bootes.kinako.service.VVOrderRecom;

public class KinakoSetInvolvedSymbols extends SMStateHandlerEx {
	public static final String E_OK = "OK";
	private final KinakoRobotData data;
	
	public KinakoSetInvolvedSymbols(KinakoRobotData robot_data) {
		this.data = robot_data;
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		SymbolAliases involved_symbols = new SymbolAliases();
		for ( VVOrderRecom order_recom : data.getCurrentSignal().getRecommendations() ) {
			String alias = order_recom.getSymbol();
			Symbol base_symbol = new Symbol(alias);
			involved_symbols.addAlias(alias, base_symbol);
			if ( base_symbol.getCurrencyCode() == null
			  && base_symbol.getType() == null
			  && base_symbol.getExchangeID() == null )
			{
				involved_symbols.addAlias(alias, new Symbol(alias, "NASDAQ", "USD", SymbolType.STOCK));
				involved_symbols.addAlias(alias, new Symbol(alias, "NYSE",   "USD", SymbolType.STOCK));
			}
		}
		data.setInvolvedSymbols(involved_symbols);
		return getExit(E_OK);
	}

}
