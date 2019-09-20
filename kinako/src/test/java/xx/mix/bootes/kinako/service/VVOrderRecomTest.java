package xx.mix.bootes.kinako.service;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class VVOrderRecomTest {
	private VVOrderRecom service;

	@Before
	public void setUp() throws Exception {
		service = new VVOrderRecom(VVOrderType.COVER_SHORT, of(200L), "VVTI");
	}
	
	@Test
	public void testCtor() {
		assertEquals(VVOrderType.COVER_SHORT, service.getType());
		assertEquals(of(200L), service.getVolume());
		assertEquals("VVTI", service.getSymbol());
	}
	
	@Test
	public void testToString() {
		String expected = "VVOrderRecom[COVER_SHORT 200 of VVTI]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(716721, 8990115)
				.append(VVOrderType.COVER_SHORT)
				.append(of(200L))
				.append("VVTI")
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
		Variant<VVOrderType> vType = new Variant<>(VVOrderType.COVER_SHORT, VVOrderType.SELL_LONG);
		Variant<CDecimal> vVol = new Variant<>(vType, of(200L), of(10L));
		Variant<String> vSym = new Variant<>(vVol, "VVTI", "XMET");
		Variant<?> iterator = vSym;
		int found_cnt = 0;
		VVOrderRecom x, found = null;
		do {
			x = new VVOrderRecom(vType.get(), vVol.get(), vSym.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(VVOrderType.COVER_SHORT, found.getType());
		assertEquals(of(200L), found.getVolume());
		assertEquals("VVTI", found.getSymbol());
	}

}
