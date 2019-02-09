package ru.prolib.bootes.lib.report.blockrep;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.bootes.lib.report.blockrep.Block;

public class BlockTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private Block service;

	@Before
	public void setUp() throws Exception {
		service = new Block("foo", of("123.456"), T("2019-01-31T03:50:00Z"));
	}
	
	@Test
	public void testCtor() {
		assertEquals("foo", service.getTypeID());
		assertEquals(of("123.456"), service.getPrice());
		assertEquals(T("2019-01-31T03:50:00Z"), service.getTime());
	}
	
	@Test
	public void testCtor_NullsAreOK() {
		service = new Block("foo", null, null);
		assertEquals("foo", service.getTypeID());
		assertNull(service.getPrice());
		assertNull(service.getTime());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("Block[")
				.append("typeID=foo,")
				.append("price=123.456,")
				.append("time=2019-01-31T03:50:00Z")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<String> vTID = new Variant<>("foo", "bar");
		Variant<CDecimal> vPR = new Variant<>(vTID, of("123.456"), of("12.34"), null);
		Variant<Instant> vTM = new Variant<>(vPR, T("2019-01-31T03:50:00Z"), T("2019-01-01T00:00:00Z"), null);
		Variant<?> iterator = vTM;
		int foundCnt = 0;
		Block x, found = null;
		do {
			x = new Block(vTID.get(), vPR.get(), vTM.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getTypeID());
		assertEquals(of("123.456"), found.getPrice());
		assertEquals(T("2019-01-31T03:50:00Z"), found.getTime());
	}

	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(86900129, 667127)
				.append("foo")
				.append(of("123.456"))
				.append(T("2019-01-31T03:50:00Z"))
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testCompareTo() {
		assertEquals( 0, service.compareTo(service));
		assertEquals( 1, service.compareTo(new Block("bar", null, T("2019-01-31T01:00:00Z"))));
		assertEquals(-1, service.compareTo(new Block("buz", null, T("2019-01-31T05:00:00Z"))));
	}

}
