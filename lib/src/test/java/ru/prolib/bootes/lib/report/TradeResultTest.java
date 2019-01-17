package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class TradeResultTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TradeResult service;

	@Before
	public void setUp() throws Exception {
		service = new TradeResult(
				T("2019-01-16T21:50:03Z"),
				T("2019-01-16T23:40:05Z"),
				false,
				ofRUB5("904.256"),
				of(10L)
			);
	}
	
	@Test
	public void testGetters() {
		assertEquals(T("2019-01-16T21:50:03Z"), service.getStartTime());
		assertEquals(T("2019-01-16T23:40:05Z"), service.getEndTime());
		assertEquals(false, service.isLong());
		assertEquals(true, service.isShort());
		assertEquals(ofRUB5("904.256"), service.getPnL());
		assertEquals(of(10L), service.getQty());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("TradeResult[")
				.append("start=2019-01-16T21:50:03Z,")
				.append("end=2019-01-16T23:40:05Z,")
				.append("isLong=false,")
				.append("pnl=904.25600 RUB,")
				.append("qty=10")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(8163921, 6671)
				.append(T("2019-01-16T21:50:03Z"))
				.append(T("2019-01-16T23:40:05Z"))
				.append(false)
				.append(ofRUB5("904.256"))
				.append(of(10L))
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
	}

	@Test
	public void testEquals() {
		Variant<Instant>
			vStart = new Variant<>(T("2019-01-16T21:50:03Z"), T("2010-05-12T00:00:00Z")),
			vEnd = new Variant<>(vStart, T("2019-01-16T23:40:05Z"), T("2010-12-05T00:00:00Z"));
		Variant<Boolean> vLong = new Variant<>(vEnd, false, true);
		Variant<CDecimal> vPNL = new Variant<>(vLong, ofRUB5("904.256"), ofRUB5("112.404")),
				vQTY = new Variant<>(vPNL, of(10L), of(20L));
		Variant<?> iterator = vQTY;
		int foundCnt = 0;
		TradeResult x, found = null;
		do {
			x = new TradeResult(vStart.get(), vEnd.get(), vLong.get(), vPNL.get(), vQTY.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(T("2019-01-16T21:50:03Z"), found.getStartTime());
		assertEquals(T("2019-01-16T23:40:05Z"), found.getEndTime());
		assertEquals(false, found.isLong());
		assertEquals(true, found.isShort());
		assertEquals(ofRUB5("904.256"), found.getPnL());
		assertEquals(of(10L), found.getQty());
	}

}
