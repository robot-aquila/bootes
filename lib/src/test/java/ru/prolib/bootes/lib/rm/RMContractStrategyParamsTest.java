package ru.prolib.bootes.lib.rm;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.bootes.lib.rm.RMContractStrategyParams;

public class RMContractStrategyParamsTest {
	private RMContractStrategyParams service;

	@Before
	public void setUp() throws Exception {
		service = new RMContractStrategyParams(
				of("0.075"),
				of("0.012"),
				of("0.6"),
				of("1.1"),
				5,
				of("0.45")
			);
	}
	
	@Test
	public void testCtor() {
		assertEquals(of("0.075"), service.getTradeGoalCapPer());
		assertEquals(of("0.012"), service.getTradeLossCapPer());
		assertEquals(of("0.6"), service.getExpDailyPriceMovePer());
		assertEquals(of("1.1"), service.getExpLocalPriceMovePer());
		assertEquals(5, service.getSlippageStp());
		assertEquals(of("0.45"), service.getStrategyCapSharePer());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder().append("RMContractStrategyParams[")
				.append("tradeGoalCapPer=0.075,tradeLossCapPer=0.012,")
				.append("expDailyPriceMovePer=0.6,expLocalPriceMovePer=1.1,")
				.append("strategyCapSharePer=0.45,")
				.append("slippageStp=5]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(113003, 805)
				.append(of("0.075"))
				.append(of("0.012"))
				.append(of("0.6"))
				.append(of("1.1"))
				.append(5)
				.append(of("0.45"))
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<CDecimal>
			vTGCP = new Variant<>(of("0.075"), of("0.020")),
			vTLCP = new Variant<>(vTGCP, of("0.012"), of("0.050")),
			vEDPM = new Variant<>(vTLCP, of("0.6"), of("0.75")),
			vELPM = new Variant<>(vEDPM, of("1.1"), of("0.9")),
			vSCSP = new Variant<>(vELPM, of("0.45"), of("0.6"));
		Variant<Integer> vSLP = new Variant<>(vSCSP, 5, 3);
		Variant<?> iterator = vSLP;
		int foundCnt = 0;
		RMContractStrategyParams x, found = null;
		do {
			x = new RMContractStrategyParams(
					vTGCP.get(),
					vTLCP.get(),
					vEDPM.get(),
					vELPM.get(),
					vSLP.get(),
					vSCSP.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(of("0.075"), found.getTradeGoalCapPer());
		assertEquals(of("0.012"), found.getTradeLossCapPer());
		assertEquals(of("0.6"), found.getExpDailyPriceMovePer());
		assertEquals(of("1.1"), found.getExpLocalPriceMovePer());
		assertEquals(5, found.getSlippageStp());
		assertEquals(of("0.45"), found.getStrategyCapSharePer());
	}

}
