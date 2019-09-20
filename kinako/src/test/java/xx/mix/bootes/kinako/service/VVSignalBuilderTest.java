package xx.mix.bootes.kinako.service;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VVSignalBuilderTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private VVSignalBuilder service;

	@Before
	public void setUp() throws Exception {
		service = new VVSignalBuilder();
	}
	
	@Test
	public void testBuild_ThrowsIfNoSignals() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("No recommendations");
		
		service.build();
	}
	
	@Test
	public void testBuild_UsesCurrentTimeByDefault() throws Exception {
		Thread.sleep(1L);
		Instant now = Instant.now();
		VVSignal actual = new VVSignalBuilder()
				.addOrderRecom(VVOrderType.COVER_SHORT, 20L, "FOOD")
				.build();
		
		assertTrue(ChronoUnit.MILLIS.between(now, actual.getTime()) <= 51);
	}

	@Test
	public void testBuild_() {
		VVSignal actual = service.withTime("2019-09-18T00:09:00Z")
				.addOrderRecom(VVOrderType.BUY_LONG, 10L, "BISO")
				.addOrderRecom(VVOrderType.COVER_SHORT, 20L, "FOOD")
				.build();
		
		List<VVOrderRecom> expected_signals = new ArrayList<>();
		expected_signals.add(new VVOrderRecom(VVOrderType.BUY_LONG, of(10L), "BISO"));
		expected_signals.add(new VVOrderRecom(VVOrderType.COVER_SHORT, of(20L), "FOOD"));
		VVSignal expected = new VVSignal(T("2019-09-18T00:09:00Z"), expected_signals);
		assertEquals(expected, actual);
	}

}
