package ru.prolib.bootes.lib.report.order;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class OrderExecInfoTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	OrderExecInfo service;

	@Before
	public void setUp() throws Exception {
		service = new OrderExecInfo(20L, T("2020-03-01T17:41:00Z"), of("12.44"), of(10L), ofRUB2("24.88"), "foo12");
	}
	
	@Test
	public void testGetters() {
		assertEquals(20L, service.getNum());
		assertEquals(T("2020-03-01T17:41:00Z"), service.getTime());
		assertEquals(of("12.44"), service.getPrice());
		assertEquals(of(10L), service.getQty());
		assertEquals(ofRUB2("24.88"), service.getValue());
		assertEquals("foo12", service.getExternalID());
	}
	
	@Test
	public void testToString() {
		String expected = "OrderExecInfo[num=20,time=2020-03-01T17:41:00Z,price=12.44,qty=10,value=24.88 RUB,extID=foo12]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(115243911, 37)
				.append(20L)
				.append(T("2020-03-01T17:41:00Z"))
				.append(of("12.44"))
				.append(of(10L))
				.append(ofRUB2("24.88"))
				.append("foo12")
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
		Variant<Long> vNum = new Variant<>(20L, 159L);
		Variant<Instant> vTm = new Variant<>(vNum, T("2020-03-01T17:41:00Z"), T("2015-07-19T00:00:00Z"));
		Variant<CDecimal> vPr = new Variant<>(vTm, of("12.44"), of("420.97"));
		Variant<CDecimal> vQty = new Variant<>(vPr, of(10L), of(240L));
		Variant<CDecimal> vVal = new Variant<>(vQty, ofRUB2("24.88"), ofUSD5("0.11256"));
		Variant<String> vEID = new Variant<>(vVal, "foo12", "bar21");
		Variant<?> iterator = vEID;
		int found_cnt = 0;
		OrderExecInfo x, found = null;
		do {
			x = new OrderExecInfo(vNum.get(), vTm.get(), vPr.get(), vQty.get(), vVal.get(), vEID.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertNotNull(found);
		assertEquals(20L, found.getNum());
		assertEquals(T("2020-03-01T17:41:00Z"), found.getTime());
		assertEquals(of("12.44"), found.getPrice());
		assertEquals(of(10L), found.getQty());
		assertEquals(ofRUB2("24.88"), found.getValue());
		assertEquals("foo12", found.getExternalID());
	}

}
