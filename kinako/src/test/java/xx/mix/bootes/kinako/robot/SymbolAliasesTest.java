package xx.mix.bootes.kinako.robot;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolAliasesTest {
	private static Symbol symbol1, symbol2, symbol3, symbol4, symbol5, symbol6;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		symbol1 = new Symbol("AAPL");
		symbol2 = new Symbol("MSFT");
		symbol3 = new Symbol("S:XXLA@NASDAQ:USD");
		symbol4 = new Symbol("S:XXLA@NYSE:USD");
		symbol5 = new Symbol("XXLA");
		symbol6 = new Symbol("KKKK");
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private SymbolAliases service;

	@Before
	public void setUp() throws Exception {
		service = new SymbolAliases();
	}
	
	@Test
	public void testAddAlias_ThrowsIfSymbolExists() {
		service.addAlias("Microsoft", symbol2);
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Symbol already exists: MSFT->Microsoft");
		
		service.addAlias("Kukumbersoft", symbol2);
	}
	
	@Test
	public void testAddAlias_OkIfSameAlias() {
		service.addAlias("Microsoft", symbol2);
		service.addAlias("Microsoft", symbol2);
	}
	
	@Test
	public void testGetAliases() {
		service.addAlias("Apple", symbol1)
			.addAlias("Microsoft", symbol2)
			.addAlias("XXLA", symbol3)
			.addAlias("XXLA", symbol4)
			.addAlias("XXLA", symbol5)
			.addAlias("Kukumber", symbol6);
		
		Set<String> actual = service.getAliases();
		
		Set<String> expected = new LinkedHashSet<>();
		expected.add("Apple");
		expected.add("Microsoft");
		expected.add("XXLA");
		expected.add("Kukumber");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSymbols0() {
		service.addAlias("Apple", symbol1)
			.addAlias("Microsoft", symbol2)
			.addAlias("XXLA", symbol3)
			.addAlias("XXLA", symbol4)
			.addAlias("XXLA", symbol5)
			.addAlias("Kukumber", symbol6);
		
		Set<Symbol> actual = service.getSymbols();
	
		Set<Symbol> expected = new LinkedHashSet<>();
		expected.add(symbol1);
		expected.add(symbol2);
		expected.add(symbol3);
		expected.add(symbol4);
		expected.add(symbol5);
		expected.add(symbol6);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSymbols1() {
		service.addAlias("Apple", symbol1)
			.addAlias("Microsoft", symbol2)
			.addAlias("XXLA", symbol3)
			.addAlias("XXLA", symbol4)
			.addAlias("XXLA", symbol5)
			.addAlias("Kukumber", symbol6);
		
		Set<Symbol> actual = service.getSymbols("Apple");
		
		Set<Symbol> expected = new LinkedHashSet<>();
		expected.add(symbol1);
		assertEquals(expected, actual);
		
		actual = service.getSymbols("XXLA");
		
		expected = new LinkedHashSet<>();
		expected.add(symbol3);
		expected.add(symbol4);
		expected.add(symbol5);
		assertEquals(expected, actual);
		
		actual = service.getSymbols("Kukumber");
		
		expected = new LinkedHashSet<>();
		expected.add(symbol6);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSymbols1_EmptyIfNotFound() {
		service.addAlias("Apple", symbol1)
			.addAlias("Microsoft", symbol2)
			.addAlias("XXLA", symbol3)
			.addAlias("XXLA", symbol4)
			.addAlias("XXLA", symbol5)
			.addAlias("Kukumber", symbol6);
		
		Set<Symbol> actual = service.getSymbols("Charlie");
		
		Set<Symbol> expected = new LinkedHashSet<>();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAlias() {
		service.addAlias("Apple", symbol1)
			.addAlias("Microsoft", symbol2)
			.addAlias("XXLA", symbol3)
			.addAlias("XXLA", symbol4)
			.addAlias("XXLA", symbol5)
			.addAlias("Kukumber", symbol6);
	
		assertEquals("Apple", service.getAlias(symbol1));
		assertEquals("Microsoft", service.getAlias(symbol2));
		assertEquals("XXLA", service.getAlias(symbol3));
		assertEquals("XXLA", service.getAlias(symbol4));
		assertEquals("XXLA", service.getAlias(symbol5));
		assertEquals("Kukumber", service.getAlias(symbol6));
	}
	
	@Test
	public void testGetAlias_ThrowsIfNotFound() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Symbol not found: S:XXLA@NASDAQ:USD");
		
		service.getAlias(symbol3);
	}
	
	@Test
	public void testIsKnownSymbol() {
		service.addAlias("Apple", symbol1)
			.addAlias("Microsoft", symbol2)
			.addAlias("XXLA", symbol3)
			.addAlias("XXLA", symbol4)
			.addAlias("XXLA", symbol5)
			.addAlias("Kukumber", symbol6);

		assertTrue(service.isKnownSymbol(symbol1));
		assertTrue(service.isKnownSymbol(symbol2));
		assertTrue(service.isKnownSymbol(symbol3));
		assertTrue(service.isKnownSymbol(symbol4));
		assertTrue(service.isKnownSymbol(symbol5));
		assertTrue(service.isKnownSymbol(symbol6));
		assertFalse(service.isKnownSymbol(new Symbol("SBRF")));
		assertFalse(service.isKnownSymbol(new Symbol("RTS-12.19")));
	}
	
	@Test
	public void testIsKnownAlias() {
		service.addAlias("Apple", symbol1)
			.addAlias("Microsoft", symbol2)
			.addAlias("XXLA", symbol3)
			.addAlias("XXLA", symbol4)
			.addAlias("XXLA", symbol5)
			.addAlias("Kukumber", symbol6);

		assertTrue(service.isKnownAlias("Apple"));
		assertTrue(service.isKnownAlias("Microsoft"));
		assertTrue(service.isKnownAlias("XXLA"));
		assertTrue(service.isKnownAlias("Kukumber"));
		assertFalse(service.isKnownAlias("Tomahawk"));
		assertFalse(service.isKnownAlias("Akatsia"));
	}

}
