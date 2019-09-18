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

public class VVSignalSetTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private List<VVSignal> signals;
	private VVSignalSet service;
	private VVSignal sig1, sig2, sig3;

	@Before
	public void setUp() throws Exception {
		signals = new ArrayList<>();
		sig1 = new VVSignal(VVSignalType.BUY_LONG, of(12L), "AAPL");
		sig2 = new VVSignal(VVSignalType.COVER_SHORT, of(100L), "MSFT");
		sig3 = new VVSignal(VVSignalType.SELL_LONG, of(500L), "SPDR");
		service = new VVSignalSet(T("2019-09-17T21:47:00Z"), signals);
	}
	
	@Test
	public void testCtor() {
		signals.add(sig1);
		signals.add(sig2);
		signals.add(sig3);
		
		assertEquals(T("2019-09-17T21:47:00Z"), service.getTime());
		List<VVSignal> expected = new ArrayList<>(), actual;
		expected.add(sig1);
		expected.add(sig2);
		expected.add(sig3);
		actual = service.getSignals();
		assertEquals(expected, actual);
		assertNotSame(signals, actual);
		assertNotSame(actual, service.getSignals());
	}
	
	@Test
	public void testGetSignals_ReturnImmutable() {
		eex.expect(UnsupportedOperationException.class);
		
		service.getSignals().add(sig2);
	}
	
	@Test
	public void testGetSignal_ThrowsIfNotFound() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Symbol not found: KAPA");
		signals.add(sig1);
		signals.add(sig2);
		signals.add(sig3);

		service.getSignal("KAPA");
	}
	
	@Test
	public void testGetSignal() {
		signals.add(sig1);
		signals.add(sig2);
		signals.add(sig3);

		assertSame(sig2, service.getSignal("MSFT"));
	}
	
	@Test
	public void testToString() {
		signals.add(sig1);
		signals.add(sig2);
		signals.add(sig3);
		
		String expected = new StringBuilder()
				.append("VVSignalSet[time=2019-09-17T21:47:00Z,signals=[")
					.append("VVSignal[BUY_LONG 12 of AAPL], ")
					.append("VVSignal[COVER_SHORT 100 of MSFT], ")
					.append("VVSignal[SELL_LONG 500 of SPDR]")
					.append("]")
				.append("]")
				.toString();
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		signals.add(sig1);
		signals.add(sig2);
		signals.add(sig3);
		int expected = new HashCodeBuilder(917265413, 99127)
				.append(T("2019-09-17T21:47:00Z"))
				.append(signals)
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
		signals.add(sig2);
		signals.add(sig3);
		signals.add(sig1);
		List<VVSignal> list1 = new ArrayList<>(), list2 = new ArrayList<>();
		list1.add(sig2);
		list1.add(sig3);
		list1.add(sig1);
		list2.add(sig3);
		list2.add(sig2);
		Variant<Instant> vTime = new Variant<>(T("2019-09-17T21:47:00Z"), T("1998-05-14T00:00:12Z"));
		Variant<List<VVSignal>> vSigs = new Variant<>(vTime, list1, list2);
		Variant<?> iterator = vSigs;
		int found_cnt = 0;
		VVSignalSet x, found = null;
		do {
			x = new VVSignalSet(vTime.get(), vSigs.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(T("2019-09-17T21:47:00Z"), found.getTime());
		assertEquals(list1, found.getSignals());
	}

}
