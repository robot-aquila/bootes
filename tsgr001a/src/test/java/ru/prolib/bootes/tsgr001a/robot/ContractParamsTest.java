package ru.prolib.bootes.tsgr001a.robot;

import static org.junit.Assert.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class ContractParamsTest {
	
	static Instant T(String time) {
		return Instant.parse(time);
	}
	
	private static Symbol symbol1, symbol2;
	private static Instant time1, time2, time3, time4;
	private static Interval int1, int2;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		symbol1 = new Symbol("AAPL@NASDAQ");
		symbol2 = new Symbol("RTS-12.18@MOEX");
		time1 = T("1996-01-01T00:00:00Z");
		time2 = T("2017-05-14T20:00:00Z");
		time3 = T("1991-06-06T12:00:00Z");
		time4 = T("2000-12-31T23:59:59Z");
		int1 = Interval.of(time1, time2);
		int2 = Interval.of(time3, time4);
	}

	private ContractParams service;
	
	@Before
	public void setUp() throws Exception {
		service = new ContractParams(symbol1, int1);
	}
	
	@Test
	public void testCtor() {
		assertEquals(symbol1, service.getSymbol());
		assertEquals(int1, service.getTradingPeriod());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(1097221, 505)
				.append(symbol1)
				.append(int1)
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
		Variant<Symbol> vSym = new Variant<>(symbol1, symbol2);
		Variant<Interval> vTP = new Variant<>(vSym, int1, int2);
		Variant<?> iterator = vTP;
		int foundCnt = 0;
		ContractParams x, found = null;
		do {
			x = new ContractParams(vSym.get(), vTP.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(symbol1, found.getSymbol());
		assertEquals(int1, found.getTradingPeriod());
	}

}
