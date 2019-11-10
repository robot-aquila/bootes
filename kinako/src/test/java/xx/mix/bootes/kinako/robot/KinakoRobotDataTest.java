package xx.mix.bootes.kinako.robot;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import xx.mix.bootes.kinako.service.VVSignal;

public class KinakoRobotDataTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private IMocksControl control;
	private KinakoRobotData service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new KinakoRobotData();
	}
	
	@Test
	public void testGetCurrentSignal_ThrowsIfNotDefined() {
		eex.expect(NullPointerException.class);
		eex.expectMessage("Current signal was not defined");
		
		service.getCurrentSignal();
	}

	@Test
	public void testGetCurrentSignal() {
		VVSignal signalMock = control.createMock(VVSignal.class);
		assertSame(service, service.setCurrentSignal(signalMock));
		assertSame(signalMock, service.getCurrentSignal());
	}
	
	@Test
	public void testGetInvolvedSymbols_ThrowsIfNotDefined() {
		eex.expect(NullPointerException.class);
		eex.expectMessage("No involved symbols defined");

		service.getInvolvedSymbols();
	}
	
	@Test
	public void testGetInvolvedSymbols() {
		SymbolAliases symbols = new SymbolAliases();
		service.setInvolvedSymbols(symbols);
		
		assertSame(symbols, service.getInvolvedSymbols());
	}
	
	@Test
	public void testGetSubscribedSymbols_ThrowsIfNotDefined() {
		eex.expect(NullPointerException.class);
		eex.expectMessage("No subscribed symbols defined");
		
		service.getSubscribedSymbols();
	}
	
	@Test
	public void testGetSubscribedSymbols() {
		Set<Symbol> symbols = new HashSet<>();
		service.setSubscribedSymbols(symbols);
		
		assertSame(symbols, service.getSubscribedSymbols());
	}
	
	@Test
	public void testGetSelectedSymbols_ThrowsIfNotDefined() {
		eex.expect(NullPointerException.class);
		eex.expectMessage("No selected symbols defined");
		
		service.getSelectedSymbols();
	}
	
	@Test
	public void testGetSelectedSymbols() {
		SymbolAliases symbols = new SymbolAliases();
		service.setSelectedSymbols(symbols);
		
		assertSame(symbols, service.getSelectedSymbols());
	}
	
	@Test
	public void testGetSymbolSubscrHandlers_ThrowsIfNotDefined() {
		eex.expect(NullPointerException.class);
		eex.expectMessage("No symbol subscription handlers defined");
		
		service.getSymbolSubscrHandlers();
	}

}
