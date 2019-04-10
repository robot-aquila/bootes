package ru.prolib.bootes.lib.rm;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.bootes.lib.rm.RMContractStrategyPositionParams;

public class RMContractStrategyPositionParamsTest {
	private RMContractStrategyPositionParams service;

	@Before
	public void setUp() throws Exception {
		service = new RMContractStrategyPositionParams(
				52,
				of(2200L),
				of(250L),
				of(30L),
				ofRUB2("12350.04"),
				ofRUB2("3002.19"),
				of("2271.976112"),
				of("130.927541"),
				ofRUB5("1100000"));
	}
	
	@Test
	public void testCtor() {
		assertEquals(52, service.getNumberOfContracts());
		assertEquals(of(2200L), service.getTakeProfitPts());
		assertEquals(of(250L), service.getStopLossPts());
		assertEquals(of(30L), service.getSlippagePts());
		assertEquals(ofRUB2("12350.04"), service.getTradeGoalCap());
		assertEquals(ofRUB2("3002.19"), service.getTradeLossCap());
		assertEquals(of("2271.976112"), service.getAvgDailyPriceMove());
		assertEquals(of("130.927541"), service.getAvgLocalPriceMove());
		assertEquals(ofRUB5("1100000"), service.getBaseCap());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("RMContractStrategyPositionParams[")
				.append("numContracts=52,takeProfit=2200,stopLoss=250,slippage=30,")
				.append("tradeGoalCap=12350.04 RUB,tradeLossCap=3002.19 RUB,")
				.append("avgDailyPriceMove=2271.976112,")
				.append("avgLocalPriceMove=130.927541,")
				.append("baseCap=1100000.00000 RUB")
				.append("]")
				.toString(); ;
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(900127, 91)
				.append(52)
				.append(of(2200L))
				.append(of(250L))
				.append(of(30L))
				.append(ofRUB2("12350.04"))
				.append(ofRUB2("3002.19"))
				.append(of("2271.976112"))
				.append(of("130.927541"))
				.append(ofRUB5("1100000"))
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
		Variant<Integer> vCN = new Variant<>(52, 107);
		Variant<CDecimal>
			vTP = new Variant<>(vCN, of(2200L), of(150L)),
			vSL = new Variant<>(vTP, of(250L), of(500L)),
			vSLP = new Variant<>(vSL, of(30L), of(50L)),
			vTGC = new Variant<>(vSLP, ofRUB2("12350.04"), ofRUB2("500000.00")),
			vTLC = new Variant<>(vTGC, ofRUB2("3002.19"), ofRUB2("1472.02")),
			vADPM = new Variant<>(vTLC, of("2271.976112"), of("1029.657812")),
			vALPM = new Variant<>(vADPM, of("130.927541"), of("105.964101")),
			vBCAP = new Variant<>(vALPM, ofRUB5("1100000"), ofUSD2("75000"));
		Variant<?> iterator = vALPM;
		int foundCnt = 0;
		RMContractStrategyPositionParams x, found = null;
		do {
			x = new RMContractStrategyPositionParams(
					vCN.get(),
					vTP.get(),
					vSL.get(),
					vSLP.get(),
					vTGC.get(),
					vTLC.get(),
					vADPM.get(),
					vALPM.get(),
					vBCAP.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(52, found.getNumberOfContracts());
		assertEquals(of(2200L), found.getTakeProfitPts());
		assertEquals(of(250L), found.getStopLossPts());
		assertEquals(of(30L), found.getSlippagePts());
		assertEquals(ofRUB2("12350.04"), found.getTradeGoalCap());
		assertEquals(ofRUB2("3002.19"), found.getTradeLossCap());
		assertEquals(of("2271.976112"), found.getAvgDailyPriceMove());
		assertEquals(of("130.927541"), found.getAvgLocalPriceMove());
		assertEquals(ofRUB5("1100000"), found.getBaseCap());
	}

}
