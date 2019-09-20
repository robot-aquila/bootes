package xx.mix.bootes.kinako.service;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.utils.Variant;

public class VVSignalTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private List<VVOrderRecom> recoms;
	private VVSignal service;
	private VVOrderRecom rec1, rec2, rec3;

	@Before
	public void setUp() throws Exception {
		recoms = new ArrayList<>();
		rec1 = new VVOrderRecom(VVOrderType.BUY_LONG, of(12L), "AAPL");
		rec2 = new VVOrderRecom(VVOrderType.COVER_SHORT, of(100L), "MSFT");
		rec3 = new VVOrderRecom(VVOrderType.SELL_LONG, of(500L), "SPDR");
		service = new VVSignal(T("2019-09-17T21:47:00Z"), recoms);
	}
	
	@Test
	public void testCtor() {
		recoms.add(rec1);
		recoms.add(rec2);
		recoms.add(rec3);
		
		assertEquals(T("2019-09-17T21:47:00Z"), service.getTime());
		List<VVOrderRecom> expected = new ArrayList<>(), actual;
		expected.add(rec1);
		expected.add(rec2);
		expected.add(rec3);
		actual = service.getRecommendations();
		assertEquals(expected, actual);
		assertNotSame(recoms, actual);
		assertNotSame(actual, service.getRecommendations());
	}
	
	@Test
	public void testGetRecommendations_ReturnImmutable() {
		eex.expect(UnsupportedOperationException.class);
		
		service.getRecommendations().add(rec2);
	}
	
	@Test
	public void testGetRecommendation_ThrowsIfNotFound() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Symbol not found: KAPA");
		recoms.add(rec1);
		recoms.add(rec2);
		recoms.add(rec3);

		service.getRecommendation("KAPA");
	}
	
	@Test
	public void testGetRecommendation() {
		recoms.add(rec1);
		recoms.add(rec2);
		recoms.add(rec3);

		assertSame(rec2, service.getRecommendation("MSFT"));
	}
	
	@Test
	public void testToString() {
		recoms.add(rec1);
		recoms.add(rec2);
		recoms.add(rec3);
		
		String expected = new StringBuilder()
				.append("VVSignal[time=2019-09-17T21:47:00Z,recommendations=[")
					.append("VVOrderRecom[BUY_LONG 12 of AAPL], ")
					.append("VVOrderRecom[COVER_SHORT 100 of MSFT], ")
					.append("VVOrderRecom[SELL_LONG 500 of SPDR]")
					.append("]")
				.append("]")
				.toString();
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		recoms.add(rec1);
		recoms.add(rec2);
		recoms.add(rec3);
		int expected = new HashCodeBuilder(917265413, 99127)
				.append(T("2019-09-17T21:47:00Z"))
				.append(recoms)
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
		recoms.add(rec2);
		recoms.add(rec3);
		recoms.add(rec1);
		List<VVOrderRecom> list1 = new ArrayList<>(), list2 = new ArrayList<>();
		list1.add(rec2);
		list1.add(rec3);
		list1.add(rec1);
		list2.add(rec3);
		list2.add(rec2);
		Variant<Instant> vTime = new Variant<>(T("2019-09-17T21:47:00Z"), T("1998-05-14T00:00:12Z"));
		Variant<List<VVOrderRecom>> vRecs = new Variant<>(vTime, list1, list2);
		Variant<?> iterator = vRecs;
		int found_cnt = 0;
		VVSignal x, found = null;
		do {
			x = new VVSignal(vTime.get(), vRecs.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(T("2019-09-17T21:47:00Z"), found.getTime());
		assertEquals(list1, found.getRecommendations());
	}

}
