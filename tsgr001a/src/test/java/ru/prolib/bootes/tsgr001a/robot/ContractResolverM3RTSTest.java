package ru.prolib.bootes.tsgr001a.robot;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class ContractResolverM3RTSTest {
	private static ZoneId ZONE;
	
	protected Instant ZD(String date) {
		return LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT).atZone(ZONE).toInstant();
	}
	
	protected Instant ZDT(String time) {
		return LocalDateTime.parse(time).atZone(ZONE).toInstant();
	}
	
	protected Interval INT(Instant from, Instant to) {
		return Interval.of(from, to);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		ZONE = ZoneId.of("Europe/Moscow");
	}
	
	private ContractResolverM3RTS service;

	@Before
	public void setUp() throws Exception {
		service = new ContractResolverM3RTS(ZONE, "RTS", 3, 15);
	}
	
	@Test
	public void testCtor() {
		assertEquals(ZONE, service.getZoneID());
		assertEquals("RTS", service.getSymbolName());
		assertEquals(3, service.getFirstMonth());
		assertEquals(15, service.getDayOfMonth());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfFirstMonthLessThan1() {
		new ContractResolverM3RTS(ZONE, "RTS", 0, 15);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfFirstMonthGreaterThan3() {
		new ContractResolverM3RTS(ZONE, "RTS", 4, 15);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfDayOfMonthLessThan1() {
		new ContractResolverM3RTS(ZONE, "RTS", 3, 0);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfDayOfMonthGreaterThan28() {
		new ContractResolverM3RTS(ZONE, "RTS", 3, 29);
	}
	
	@Test
	public void testDetermineContract() {
		ContractParams expected = null;
		Symbol sym = null;
		
		sym = new Symbol("RTS-3.05");
		expected = new ContractParams(sym, INT(ZDT("2005-01-01T10:00:00"), ZDT("2005-01-01T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("1991-01-01T00:00:00")));
		assertEquals(expected, service.determineContract(ZDT("2003-12-31T23:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2005-01-01T00:00:00")));

		expected = new ContractParams(sym, INT(ZDT("2005-03-01T10:00:00"), ZDT("2005-03-01T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-01T00:00:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-01T08:30:15")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-01T12:55:05")));
		
		expected = new ContractParams(sym, INT(ZDT("2005-03-02T10:00:00"), ZDT("2005-03-02T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-01T20:59:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-01T23:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-02T01:00:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-02T09:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-02T10:15:00")));
		
		expected = new ContractParams(sym, INT(ZDT("2005-03-14T10:00:00"), ZDT("2005-03-14T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-14T00:00:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-14T09:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-14T10:00:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-14T18:29:59")));
		
		sym = new Symbol("RTS-6.05");
		expected = new ContractParams(sym, INT(ZDT("2005-03-15T10:00:00"), ZDT("2005-03-15T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-14T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-03-15T00:00:00")));
		
		expected = new ContractParams(sym, INT(ZDT("2005-04-01T10:00:00"), ZDT("2005-04-01T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-04-01T00:00:00")));
		
		expected = new ContractParams(sym, INT(ZDT("2005-05-31T10:00:00"), ZDT("2005-05-31T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-05-30T20:45:00")));
		
		expected = new ContractParams(sym, INT(ZDT("2005-06-01T10:00:00"), ZDT("2005-06-01T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-05-31T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-06-01T00:00:00")));
		
		sym = new Symbol("RTS-9.05");
		expected = new ContractParams(sym, INT(ZDT("2005-06-15T10:00:00"), ZDT("2005-06-15T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-06-14T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-06-14T23:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2005-06-15T00:00:00")));
		
		sym = new Symbol("RTS-12.05");
		expected = new ContractParams(sym, INT(ZDT("2005-09-15T10:00:00"), ZDT("2005-09-15T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-09-14T23:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2005-09-15T00:00:00")));
		
		expected = new ContractParams(sym, INT(ZDT("2005-12-01T10:00:00"), ZDT("2005-12-01T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-01T00:00:00")));
		
		expected = new ContractParams(sym, INT(ZDT("2005-12-14T10:00:00"), ZDT("2005-12-14T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-14T00:00:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-14T08:00:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-14T09:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-14T12:15:47")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-14T18:00:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-14T18:29:59.999")));
		
		sym = new Symbol("RTS-3.06");
		expected = new ContractParams(sym, INT(ZDT("2005-12-15T10:00:00"), ZDT("2005-12-15T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-14T23:59:59.999")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-14T23:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-15T00:00:00")));
		
		expected = new ContractParams(sym, INT(ZDT("2006-01-01T10:00:00"), ZDT("2006-01-01T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2005-12-31T23:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2006-01-01T00:00:00")));
		
		sym = new Symbol("RTS-12.18");
		expected = new ContractParams(sym, INT(ZDT("2018-10-31T10:00:00"), ZDT("2018-10-31T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2018-10-30T23:59:59")));
		
		sym = new Symbol("RTS-3.19");
		expected = new ContractParams(sym, INT(ZDT("2018-12-15T10:00:00"), ZDT("2018-12-15T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2018-12-14T23:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2018-12-15T00:00:00")));
		
		expected = new ContractParams(sym, INT(ZDT("2019-01-01T10:00:00"), ZDT("2019-01-01T18:30:00")));
		assertEquals(expected, service.determineContract(ZDT("2018-12-31T23:59:59")));
		assertEquals(expected, service.determineContract(ZDT("2019-01-01T00:00:00")));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<ZoneId> vZID = new Variant<>(ZONE, ZoneId.of("Europe/London"));
		Variant<String> vNAME = new Variant<>(vZID, "RTS", "Si");
		Variant<Integer> vFM = new Variant<>(vNAME, 3, 1);
		Variant<Integer> vDoF = new Variant<>(vFM, 15, 20);
		Variant<?> iterator = vDoF;
		int foundCnt = 0;
		ContractResolverM3RTS x, found = null;
		do {
			x = new ContractResolverM3RTS(vZID.get(), vNAME.get(), vFM.get(), vDoF.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(ZONE, found.getZoneID());
		assertEquals("RTS", found.getSymbolName());
		assertEquals(3, found.getFirstMonth());
		assertEquals(15, found.getDayOfMonth());
	}

}
