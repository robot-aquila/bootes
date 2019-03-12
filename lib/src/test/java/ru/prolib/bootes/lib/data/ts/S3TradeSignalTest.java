package ru.prolib.bootes.lib.data.ts;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.bootes.lib.data.ts.SignalType;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;

public class S3TradeSignalTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private S3TradeSignal service;

	@Before
	public void setUp() throws Exception {
		service = new S3TradeSignal(
				SignalType.SELL,
				T("2019-01-04T04:33:42Z"),
				of("50.34"),
				of("1.0"),
				of("20.00"),
				of("10.00"),
				of("2.00"),
				ofRUB5("10000.00"),
				ofRUB5( "7500.00"),
				ofRUB5( "1200.00")
			);
	}
	
	@Test
	public void testGetters() {
		assertEquals(SignalType.SELL, service.getType());
		assertEquals(T("2019-01-04T04:33:42Z"), service.getTime());
		assertEquals(of("50.34"), service.getExpectedPrice());
		assertEquals(of("1.0"), service.getExpectedQty());
		assertEquals(of("20.00"), service.getTakeProfitPts());
		assertEquals(of("10.00"), service.getStopLossPts());
		assertEquals(of("2.00"), service.getSlippagePts());
		assertEquals(ofRUB5("10000.00"), service.getBaseCap());
		assertEquals(ofRUB5( "7500.00"), service.getGoalCap());
		assertEquals(ofRUB5( "1200.00"), service.getLossCap());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("S3TradeSignal[")
				.append("type=SELL,")
				.append("time=2019-01-04T04:33:42Z,")
				.append("expectedPrice=50.34,")
				.append("expectedQty=1.0,")
				.append("takeProfitPts=20.00,")
				.append("stopLossPts=10.00,")
				.append("slippagePts=2.00,")
				.append("baseCap=10000.00000 RUB,")
				.append("goalCap=7500.00000 RUB,")
				.append("lossCap=1200.00000 RUB")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(9007861, 419)
				.append(SignalType.SELL)
				.append(T("2019-01-04T04:33:42Z"))
				.append(of("50.34"))
				.append(of("1.0"))
				.append(of("20.00"))
				.append(of("10.00"))
				.append(of("2.00"))
				.append(ofRUB5("10000.00"))
				.append(ofRUB5("7500.00"))
				.append(ofRUB5("1200.00"))
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
		Variant<SignalType> vType = new Variant<>(SignalType.SELL, SignalType.BUY);
		Variant<Instant> vTime = new Variant<>(vType, T("2019-01-04T04:33:42Z"), T("1995-01-19T19:48:34Z"));
		Variant<CDecimal>
			vEPR = new Variant<>(vTime, of("50.34"), of("27.19")),
			vEQT = new Variant<>(vEPR, of("1.0"), of("2.0")),
			vTPP = new Variant<>(vEQT, of("20.00"), of("21.43")),
			vSLP = new Variant<>(vTPP, of("10.00"), of("10.52")),
			vSLI = new Variant<>(vSLP, of("2.00"), of("0.50")),
			vBCap = new Variant<>(vSLI, ofRUB5("10000"), ofUSD5("7000")),
			vGCap = new Variant<>(vBCap, ofRUB5("7500"), ofUSD2("400")),
			vLCap = new Variant<>(vGCap, ofRUB5("1200"), ofUSD5("200"));
		Variant<?> iterator = vSLP;
		int foundCnt = 0;
		S3TradeSignal x, found = null;
		do {
			x = new S3TradeSignal(
					vType.get(),
					vTime.get(),
					vEPR.get(),
					vEQT.get(),
					vTPP.get(),
					vSLP.get(),
					vSLI.get(),
					vBCap.get(),
					vGCap.get(),
					vLCap.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(SignalType.SELL, found.getType());
		assertEquals(T("2019-01-04T04:33:42Z"), found.getTime());
		assertEquals(of("50.34"), found.getExpectedPrice());
		assertEquals(of("1.0"), found.getExpectedQty());
		assertEquals(of("20.00"), found.getTakeProfitPts());
		assertEquals(of("10.00"), found.getStopLossPts());
		assertEquals(of("2.00"), found.getSlippagePts());
		assertEquals(ofRUB5("10000.00"), found.getBaseCap());
		assertEquals(ofRUB5("7500.00"), found.getGoalCap());
		assertEquals(ofRUB5("1200.00"), found.getLossCap());
	}

}
