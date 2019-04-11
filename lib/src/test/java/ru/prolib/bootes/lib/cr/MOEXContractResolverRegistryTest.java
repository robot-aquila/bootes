package ru.prolib.bootes.lib.cr;

import static org.junit.Assert.*;

import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.bootes.lib.cr.ContractResolver;
import ru.prolib.bootes.lib.cr.ContractResolverM3RTS;
import ru.prolib.bootes.lib.cr.MOEXContractResolverRegistry;

public class MOEXContractResolverRegistryTest {
	private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");
	private ContractResolverRegistry service;

	@Before
	public void setUp() throws Exception {
		service = new MOEXContractResolverRegistry();
	}

	@Test (expected=IllegalArgumentException.class)
	public void testGetResolver_ThrowsIfContractNotSupported() {
		service.getResolver("foobar");
	}
	
	@Test
	public void testGetResolver_RTS() {
		ContractResolver actual = service.getResolver("RTS");
		
		ContractResolver expected = new ContractResolverM3RTS(ZONE, "RTS", 3, 15);
		assertEquals(expected, actual);
		assertSame(actual, service.getResolver("RTS"));
	}
	
	@Test
	public void testGetResolver_Si() {
		ContractResolver actual = service.getResolver("Si");
		
		ContractResolver expected = new ContractResolverM3RTS(ZONE, "Si", 3, 15);
		assertEquals(expected, actual);
		assertSame(actual, service.getResolver("Si"));
	}

}
