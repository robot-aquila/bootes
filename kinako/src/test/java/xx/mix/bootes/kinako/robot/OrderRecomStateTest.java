package xx.mix.bootes.kinako.robot;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import xx.mix.bootes.kinako.service.VVOrderType;

public class OrderRecomStateTest {
	private OrderRecomState service;

	@Before
	public void setUp() throws Exception {
		service = new OrderRecomState(
				VVOrderType.COVER_SHORT,
				"AAPL",
				of(100L),
				new Symbol("S:AAPL@NASDAQ:USD")
			);
	}
	
	@Test
	public void testCtor() {
		assertEquals(VVOrderType.COVER_SHORT, service.getType());
		assertEquals("AAPL", service.getSymbol());
		assertEquals(of(100L), service.getVolume());
		assertEquals(new Symbol("AAPL", "NASDAQ", "USD", SymbolType.STOCK), service.getLocalSymbol());
		assertEquals(OrderRecomStatus.NEW, service.getStatus());
		assertEquals(of(0L), service.getExecutedVolume());
		assertEquals("", service.getComment());
	}
	
	@Test
	public void testSetters() {
		assertSame(service, service.setStatus(OrderRecomStatus.ERROR));
		assertSame(service, service.setExecutedVolume(of(25L)));
		assertSame(service, service.setComment("Order cancelled by broker"));
		
		assertEquals(OrderRecomStatus.ERROR, service.getStatus());
		assertEquals(of(25L), service.getExecutedVolume());
		assertEquals("Order cancelled by broker", service.getComment());
	}

}
