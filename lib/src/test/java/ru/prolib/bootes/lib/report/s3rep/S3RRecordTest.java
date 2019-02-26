package ru.prolib.bootes.lib.report.s3rep;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class S3RRecordTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private S3RRecord service;

	@Before
	public void setUp() throws Exception {
		service = new S3RRecord(
				846,
				S3RType.LONG,
				T("2019-02-11T11:55:00Z"),
				of("120.96"),
				of("100"),
				of("135.02"),
				of("115.06"),
				of("121.54"),
				T("2019-02-11T12:15:00Z"),
				of("127.92"),
				ofRUB5("113.452")
			);
	}
	
	@Test
	public void testCtor11() {
		assertEquals(846, service.getID());
		assertEquals(S3RType.LONG, service.getType());
		assertEquals(T("2019-02-11T11:55:00Z"), service.getEntryTime());
		assertEquals(of("120.96"), service.getEntryPrice());
		assertEquals(of("100"), service.getQty());
		assertEquals(of("135.02"), service.getTakeProfit());
		assertEquals(of("115.06"), service.getStopLoss());
		assertEquals(of("121.54"), service.getBreakEven());
		assertEquals(T("2019-02-11T12:15:00Z"), service.getExitTime());
		assertEquals(of("127.92"), service.getExitPrice());
		assertEquals(ofRUB5("113.452"), service.getProfitAndLoss());
	}
	
	@Test
	public void testCtor8() {
		service = new S3RRecord(
				112,
				S3RType.SHORT,
				T("2019-02-11T12:05:00Z"),
				of("4029.96"),
				of("10"),
				of("3991.07"),
				of("4055.00"),
				of("4003.10")
			);
		assertEquals(112, service.getID());
		assertEquals(S3RType.SHORT, service.getType());
		assertEquals(T("2019-02-11T12:05:00Z"), service.getEntryTime());
		assertEquals(of("4029.96"), service.getEntryPrice());
		assertEquals(of("10"), service.getQty());
		assertEquals(of("3991.07"), service.getTakeProfit());
		assertEquals(of("4055.00"), service.getStopLoss());
		assertEquals(of("4003.10"), service.getBreakEven());
		assertNull(service.getExitTime());
		assertNull(service.getExitPrice());
		assertNull(service.getProfitAndLoss());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("S3RRecord[")
				.append("id=846,")
				.append("exitTime=2019-02-11T12:15:00Z,")
				.append("exitPrice=127.92,")
				.append("pl=113.45200 RUB,")
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
		int expected = new HashCodeBuilder(112591, 403)
				.append(846)
				.append(S3RType.LONG)
				.append(T("2019-02-11T11:55:00Z"))
				.append(of("120.96"))
				.append(of("100"))
				.append(of("135.02"))
				.append(of("115.06"))
				.append(of("121.54"))
				.append(T("2019-02-11T12:15:00Z"))
				.append(of("127.92"))
				.append(ofRUB5("113.452"))
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
		Variant<Integer> vID = new Variant<>(846, 215);
		Variant<S3RType> vType = new Variant<>(vID, S3RType.LONG, S3RType.SHORT);
		Variant<Instant>
			vEnTM = new Variant<>(vType, T("2019-02-11T11:55:00Z"), T("2020-01-01T00:00:00Z")),
			vExTM = new Variant<>(vEnTM, T("2019-02-11T12:15:00Z"), T("2025-12-31T23:59:59Z"));
		Variant<CDecimal>
			vEnPR = new Variant<>(vExTM, of("120.96"), of("13.57")),
			vQTY  = new Variant<>(vEnPR, of("100"), of("1")),
			vTP	  = new Variant<>(vQTY, of("135.02"), of("249.12")),
			vSL   = new Variant<>(vTP, of("115.06"), of("2.49223")),
			vBE   = new Variant<>(vSL, of("121.54"), of("111.222")),
			vExPR = new Variant<>(vBE, of("127.92"), of("429.002")),
			vPL   = new Variant<>(vExPR, ofRUB5("113.452"), ofRUB5("-10000.00"));
		Variant<?> iterator = vPL;
		int foundCnt = 0;
		S3RRecord x, found = null;
		do {
			x = new S3RRecord(
					vID.get(),
					vType.get(),
					vEnTM.get(),
					vEnPR.get(),
					vQTY.get(),
					vTP.get(),
					vSL.get(),
					vBE.get(),
					vExTM.get(),
					vExPR.get(),
					vPL.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(846, found.getID());
		assertEquals(S3RType.LONG, found.getType());
		assertEquals(T("2019-02-11T11:55:00Z"), found.getEntryTime());
		assertEquals(of("120.96"), found.getEntryPrice());
		assertEquals(of("100"), found.getQty());
		assertEquals(of("135.02"), found.getTakeProfit());
		assertEquals(of("115.06"), found.getStopLoss());
		assertEquals(of("121.54"), found.getBreakEven());
		assertEquals(T("2019-02-11T12:15:00Z"), found.getExitTime());
		assertEquals(of("127.92"), found.getExitPrice());
		assertEquals(ofRUB5("113.452"), found.getProfitAndLoss());
	}

	@Test
	public void testGetDurationMinutes() {
		assertEquals(Long.valueOf(20L), service.getDurationMinutes());
	}
	
	@Test
	public void testGetDurationMinutes_NotEnded() {
		service = new S3RRecord(
				112,
				S3RType.SHORT,
				T("2019-02-11T12:05:00Z"),
				of("4029.96"),
				of("10"),
				of("3991.07"),
				of("4055.00"),
				of("4003.10")
			);
		
		assertNull(service.getDurationMinutes());
	}

}
