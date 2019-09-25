package xx.mix.bootes.kinako.robot;

import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import xx.mix.bootes.kinako.service.VVSignal;

public class KinakoRobotData {
	private VVSignal currentSignal;
	private SymbolAliases involvedSymbols, selectedSymbols;
	private Set<Symbol> subscribedSymbols;
	
	public synchronized VVSignal getCurrentSignal() {
		if ( currentSignal == null ) {
			throw new NullPointerException("Current signal was not defined");
		}
		return currentSignal;
	}
	
	public synchronized KinakoRobotData setCurrentSignal(VVSignal signal) {
		this.currentSignal = signal;
		return this;
	}
	
	public synchronized SymbolAliases getInvolvedSymbols() {
		if ( involvedSymbols == null ) {
			throw new NullPointerException("No involved symbols defined");
		}
		return involvedSymbols;
	}
	
	public synchronized void setInvolvedSymbols(SymbolAliases symbols) {
		this.involvedSymbols = symbols;
	}
	
	public synchronized Set<Symbol> getSubscribedSymbols() {
		if ( subscribedSymbols == null ) {
			throw new NullPointerException("No subscribed symbols defined");
		}
		return subscribedSymbols;
	}
	
	public synchronized void setSubscribedSymbols(Set<Symbol> symbols) {
		this.subscribedSymbols = symbols;
	}
	
	public synchronized SymbolAliases getSelectedSymbols() {
		if ( selectedSymbols == null ) {
			throw new NullPointerException("No selected symbols defined");
		}
		return selectedSymbols;
	}
	
	public synchronized void setSelectedSymbols(SymbolAliases symbols) {
		this.selectedSymbols = symbols;
	}

}
