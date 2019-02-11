package ru.prolib.bootes.lib.report.s3rep;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class S3RRecordCreateTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	private S3RRecordCreate service;

	@Before
	public void setUp() throws Exception {
		service = new S3RRecordCreate(
				S3RType.LONG,
				T("2019-02-11T11:55:00Z"),
				of("120.96"),
				of("100"),
				of("135.02"),
				of("115.06"),
				of("121.54")
			);
	}
	
	@Test
	public void testCtor8() {
		assertEquals(S3RType.LONG, service.getType());
		assertEquals(T("2019-02-11T11:55:00Z"), service.getEntryTime());
		assertEquals(of("120.96"), service.getEntryPrice());
		assertEquals(of("100"), service.getQty());
		assertEquals(of("135.02"), service.getTakeProfit());
		assertEquals(of("115.06"), service.getStopLoss());
		assertEquals(of("121.54"), service.getBreakEven());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("S3RRecordCreate[")
				.append("type=LONG,")
				.append("entryTime=2019-02-11T11:55:00Z,")
				.append("entryPrice=120.96,")
				.append("qty=100,")
				.append("tp=135.02,")
				.append("sl=115.06,")
				.append("be=121.54")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(7009715, 19)
				.append(S3RType.LONG)
				.append(T("2019-02-11T11:55:00Z"))
				.append(of("120.96"))
				.append(of("100"))
				.append(of("135.02"))
				.append(of("115.06"))
				.append(of("121.54"))
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
		Variant<S3RType> vType = new Variant<>(S3RType.LONG, S3RType.SHORT);
		Variant<Instant> vEnTM = new Variant<>(vType, T("2019-02-11T11:55:00Z"), T("2019-05-01T00:00:00Z"));
		Variant<CDecimal>
			vEnPR = new Variant<>(vEnTM, of("120.96"), of("159.12")),
			vQTY  = new Variant<>(vEnPR, of("100"), of("500")),
			vTP   = new Variant<>(vQTY, of("135.02"), of("200.61")),
			vSL   = new Variant<>(vTP, of("115.06"), of("502.9998782")),
			vBE   = new Variant<>(vSL, of("121.54"), of("1.02"));
		Variant<?> iterator = vBE;
		int foundCnt = 0;
		S3RRecordCreate x, found = null;
		do {
			x = new S3RRecordCreate(
					vType.get(),
					vEnTM.get(),
					vEnPR.get(),
					vQTY.get(),
					vTP.get(),
					vSL.get(),
					vBE.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(S3RType.LONG, found.getType());
		assertEquals(T("2019-02-11T11:55:00Z"), found.getEntryTime());
		assertEquals(of("120.96"), found.getEntryPrice());
		assertEquals(of("100"), found.getQty());
		assertEquals(of("135.02"), found.getTakeProfit());
		assertEquals(of("115.06"), found.getStopLoss());
		assertEquals(of("121.54"), found.getBreakEven());
	}

}
