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
import ru.prolib.bootes.lib.data.ts.TradeSignal;

public class TradeSignalTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TradeSignal service;

	@Before
	public void setUp() throws Exception {
		service = new TradeSignal(
				SignalType.SELL,
				T("2019-01-04T04:33:42Z"),
				of("50.34"),
				of("1.0"),
				of("20.00"),
				of("10.00")
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
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("TradeSignal[")
				.append("type=SELL,")
				.append("time=2019-01-04T04:33:42Z,")
				.append("expectedPrice=50.34,")
				.append("expectedQty=1.0,")
				.append("takeProfitPts=20.00,")
				.append("stopLossPts=10.00")
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
			vSLP = new Variant<>(vTPP, of("10.00"), of("10.52"));
		Variant<?> iterator = vSLP;
		int foundCnt = 0;
		TradeSignal x, found = null;
		do {
			x = new TradeSignal(vType.get(), vTime.get(), vEPR.get(), vEQT.get(), vTPP.get(), vSLP.get());
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
	}

}
