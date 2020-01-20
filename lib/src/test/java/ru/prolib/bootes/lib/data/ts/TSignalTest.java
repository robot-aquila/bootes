package ru.prolib.bootes.lib.data.ts;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class TSignalTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	private TSignal service;

	@Before
	public void setUp() throws Exception {
		service = new TSignal(
				T("2020-01-20T13:58:00Z"),
				15,
				SignalType.BUY,
				of("215.47")
			);
	}
	
	@Test
	public void testGetters() {
		assertEquals(T("2020-01-20T13:58:00Z"), service.getTime());
		assertEquals(15, service.getIndex());
		assertEquals(SignalType.BUY, service.getType());
		assertEquals(of("215.47"), service.getPrice());
	}
	
	@Test
	public void testToString() {
		String expected = "TSignal[2020-01-20T13:58:00Z i=15 BUY@215.47]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(8865123, 19)
				.append(T("2020-01-20T13:58:00Z"))
				.append(15)
				.append(SignalType.BUY)
				.append(of("215.47"))
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
		Variant<Instant> vTm = new Variant<>(T("2020-01-20T13:58:00Z"), T("1995-12-01T07:34:26Z"));
		Variant<Integer> vIdx = new Variant<>(vTm, 15, -1, 170);
		Variant<SignalType> vTyp = new Variant<>(vIdx, SignalType.BUY, SignalType.NONE);
		Variant<CDecimal> vPr = new Variant<>(vTyp, of("215.47"), of("0.34"));
		Variant<?> iterator = vPr;
		int found_cnt = 0;
		TSignal x, found = null;
		do {
			x = new TSignal(vTm.get(), vIdx.get(), vTyp.get(), vPr.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(T("2020-01-20T13:58:00Z"), found.getTime());
		assertEquals(15, found.getIndex());
		assertEquals(SignalType.BUY, found.getType());
		assertEquals(of("215.47"), found.getPrice());
	}

}
