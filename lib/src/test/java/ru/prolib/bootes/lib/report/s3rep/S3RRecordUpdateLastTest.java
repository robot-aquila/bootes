package ru.prolib.bootes.lib.report.s3rep;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class S3RRecordUpdateLastTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private S3RRecordUpdateLast service;

	@Before
	public void setUp() throws Exception {
		service = new S3RRecordUpdateLast(
				T("2019-02-11T22:03:00Z"),
				of("1528.982"),
				ofRUB5("12.57800")
			);
	}
	
	@Test
	public void testCtor3() {
		assertEquals(T("2019-02-11T22:03:00Z"), service.getExitTime());
		assertEquals(of("1528.982"), service.getExitPrice());
		assertEquals(ofRUB5("12.57800"), service.getProfitAndLoss());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("S3RRecordUpdateLast[")
				.append("exitTime=2019-02-11T22:03:00Z,")
				.append("exitPrice=1528.982,")
				.append("pl=12.57800 RUB")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(3357189, 745)
				.append(T("2019-02-11T22:03:00Z"))
				.append(of("1528.982"))
				.append(ofRUB5("12.57800"))
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
		Variant<Instant> vTM = new Variant<>(T("2019-02-11T22:03:00Z"), T("1998-01-01T00:00:00Z"));
		Variant<CDecimal>
			vPR = new Variant<>(vTM, of("1528.982"), of("1.12")),
			vPL = new Variant<>(vPR, ofRUB5("12.57800"), ofUSD5("700.01"));
		Variant<?> iterator = vPL;
		int foundCnt = 0;
		S3RRecordUpdateLast x, found = null;
		do {
			x = new S3RRecordUpdateLast(
					vTM.get(),
					vPR.get(),
					vPL.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(T("2019-02-11T22:03:00Z"), found.getExitTime());
		assertEquals(of("1528.982"), found.getExitPrice());
		assertEquals(ofRUB5("12.57800"), found.getProfitAndLoss());
	}

}
