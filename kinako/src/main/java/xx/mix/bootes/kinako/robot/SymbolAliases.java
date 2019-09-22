package xx.mix.bootes.kinako.robot;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolAliases {
	private final Map<String, Set<Symbol>> mapAliasToOptions;
	private final Map<Symbol, String> mapSymbolToAlias;
	
	SymbolAliases(Map<String, Set<Symbol>> alias_map, Map<Symbol, String> symbol_map) {
		this.mapAliasToOptions = alias_map;
		this.mapSymbolToAlias = symbol_map;
	}
	
	public SymbolAliases() {
		this(new LinkedHashMap<>(), new LinkedHashMap<>());
	}
	
	public synchronized SymbolAliases addAlias(String alias, Symbol symbol) {
		String dummy_alias = mapSymbolToAlias.get(symbol);
		if ( dummy_alias != null && ! dummy_alias.equals(alias) ) {
			throw new IllegalArgumentException("Symbol already exists: " + symbol + "->" + dummy_alias);
		}
		Set<Symbol> options = mapAliasToOptions.get(alias);
		if ( options == null ) {
			options = new LinkedHashSet<>();
			mapAliasToOptions.put(alias, options);
		}
		options.add(symbol);
		mapSymbolToAlias.put(symbol, alias);
		return this;
	}
	
	public synchronized Set<String> getAliases() {
		return new LinkedHashSet<>(mapAliasToOptions.keySet());
	}
	
	public synchronized Set<Symbol> getSymbols() {
		return new LinkedHashSet<>(mapSymbolToAlias.keySet());
	}
	
	public synchronized Set<Symbol> getSymbols(String alias) {
		Set<Symbol> options = mapAliasToOptions.get(alias);
		return options != null ? new LinkedHashSet<>(options) : new LinkedHashSet<>();
	}
	
	public synchronized String getAlias(Symbol symbol) {
		String alias = mapSymbolToAlias.get(symbol);
		if ( alias == null ) {
			throw new IllegalArgumentException("Symbol not found: " + symbol);
		}
		return alias;
	}

}
