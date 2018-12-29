package ru.prolib.bootes.tsgr001a.mscan;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class MSCANLogEntryImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private MSCANLogEntryImpl service;

	@Before
	public void setUp() throws Exception {
		service = new MSCANLogEntryImpl(
				true,
				"BUY",
				T("2018-12-27T08:57:22Z"),
				of("153.912"),
				"XXX"
			);
	}
	
	@Test
	public void testGetters() {
		assertEquals(true, service.isTrigger());
		assertEquals("BUY", service.getTypeID());
		assertEquals(T("2018-12-27T08:57:22Z"), service.getTime());
		assertEquals(of("153.912"), service.getValue());
		assertEquals("XXX", service.getText());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("MSCANLogEntryImpl[")
				.append("trigger=true")
				.append(",")
				.append("typeID=BUY")
				.append(",")
				.append("time=2018-12-27T08:57:22Z")
				.append(",")
				.append("value=153.912")
				.append(",")
				.append("text=XXX")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(9005441, 725)
				.append(true)
				.append("BUY")
				.append(T("2018-12-27T08:57:22Z"))
				.append(of("153.912"))
				.append("XXX")
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
		Variant<Boolean> vTrig = new Variant<>(true, false);
		Variant<String> vType = new Variant<>(vTrig, "BUY", "SELL");
		Variant<Instant> vTime = new Variant<>(vType, T("2018-12-27T08:57:22Z"), T("2005-01-15T10:05:19Z"));
		Variant<CDecimal> vVal = new Variant<>(vTime, of("153.912"), of("110.240"));
		Variant<String> vText = new Variant<>(vVal, "XXX", "YYY");
		Variant<?> iterator = vText;
		int foundCnt = 0;
		MSCANLogEntryImpl x, found = null;
		do {
			x = new MSCANLogEntryImpl(vTrig.get(), vType.get(), vTime.get(), vVal.get(), vText.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(true, found.isTrigger());
		assertEquals("BUY", found.getTypeID());
		assertEquals(T("2018-12-27T08:57:22Z"), found.getTime());
		assertEquals(of("153.912"), found.getValue());
		assertEquals("XXX", found.getText());
	}

}
