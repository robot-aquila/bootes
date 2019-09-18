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

public class VVSignalSetBuilderTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	
	private VVSignalSetBuilder service;

	@Before
	public void setUp() throws Exception {
		service = new VVSignalSetBuilder();
	}
	
	@Test
	public void testBuild_ThrowsIfNoSignals() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("No signals");
		
		service.build();
	}
	
	@Test
	public void testBuild_UsesCurrentTimeByDefault() throws Exception {
		Thread.sleep(1L);
		Instant now = Instant.now();
		VVSignalSet actual = new VVSignalSetBuilder()
				.addSignal(VVSignalType.COVER_SHORT, 20L, "FOOD")
				.build();
		
		assertTrue(ChronoUnit.MILLIS.between(now, actual.getTime()) <= 51);
	}

	@Test
	public void testBuild_() {
		VVSignalSet actual = service.withTime("2019-09-18T00:09:00Z")
				.addSignal(VVSignalType.BUY_LONG, 10L, "BISO")
				.addSignal(VVSignalType.COVER_SHORT, 20L, "FOOD")
				.build();
		
		List<VVSignal> expected_signals = new ArrayList<>();
		expected_signals.add(new VVSignal(VVSignalType.BUY_LONG, of(10L), "BISO"));
		expected_signals.add(new VVSignal(VVSignalType.COVER_SHORT, of(20L), "FOOD"));
		VVSignalSet expected = new VVSignalSet(T("2019-09-18T00:09:00Z"), expected_signals);
		assertEquals(expected, actual);
	}

}
