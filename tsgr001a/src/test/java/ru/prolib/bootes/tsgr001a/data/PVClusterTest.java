package ru.prolib.bootes.tsgr001a.data;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

public class PVClusterTest {
	private PVCluster service;

	@Before
	public void setUp() throws Exception {
		service = new PVCluster();
		
	}

	@Test
	public void testGetTotalVolume() {
		service.addTrade(of(125090L), of(1000L));
		service.addTrade(of(140050L), of(2500L));
		service.addTrade(of(120500L), of(1200L));
		service.addTrade(of(125090L), of(5000L));
		
		assertEquals(of(9700L), service.getTotalVolume());
	}
	
	@Test
	public void testGetWeightedAverage() {
		service.addTrade(of(12L), of(100L));
		service.addTrade(of(48L), of( 20L));
		service.addTrade(of(98L), of( 10L));
		service.addTrade(of(58L), of( 12L));
		service.addTrade(of(74L), of(  5L));
		service.addTrade(of(86L), of( 11L));
		service.addTrade(of(50L), of(  6L));
		
		assertEquals(of("33.24390220"), service.getWeightedAverage());
	}

}
