package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

public class TradingStatisticsImplTest {
	private static ConsecutiveTrades zero_ct = new ConsecutiveTrades(ZERO_RUB5, 0);
	private TradingStatisticsImpl service;

	@Before
	public void setUp() throws Exception {
		service = new TradingStatisticsImpl(
				ofRUB5("1734.24"),		// gross profit
				ofRUB5("-1352.93"),		// gross loss
				ofRUB5("143.06"),		// absolute drawdown
				ofRUB5("194.58"),		// maximal drawdown			
				ofRUB5("207.36"),		// largest profit trade
				ofRUB5("-188.20"),		// largest loss trade
				150,					// short positions
				117,					// winning short positions
				154,					// long positions
				123,					// winning long positions
				5,						// average consecutive wins
				7,						// average consecutive losses
				new ConsecutiveTrades(ofRUB5("46.36"), 19),
				new ConsecutiveTrades(ofRUB5("-114.36"), 3),
				new ConsecutiveTrades(ofRUB5("123.45"), 10),
				new ConsecutiveTrades(ofRUB5("543.21"), 15)
			);
	}

	@Test
	public void testGetters() {
		assertEquals(ofRUB5("381.31"), service.getTotalNetProfit());
		assertEquals(ofRUB5("1734.24"), service.getGrossProfit());
		assertEquals(ofRUB5("-1352.93"), service.getGrossLoss());
		assertEquals(of("1.28184"), service.getProfitFactor());
		assertEquals(ofRUB5("1.25431"), service.getExpectedPayoff());
		assertEquals(ofRUB5("143.06"), service.getAbsoluteDrawdown());
		assertEquals(ofRUB5("194.58"), service.getMaximalDrawdown());
		assertEquals(304, service.getTotalTrades());
		assertEquals(150, service.getShortPositions());
		assertEquals(117, service.getWinningShortPositions());
		assertEquals(154, service.getLongPositions());
		assertEquals(123, service.getWinningLongPositions());
		assertEquals(240, service.getProfitTrades());
		assertEquals(64, service.getLossTrades());
		assertEquals(ofRUB5("207.36"), service.getLargestProfitTrade());
		assertEquals(ofRUB5("-188.20"), service.getLargestLossTrade());
		assertEquals(ofRUB5("7.226"), service.getAverageProfitTrade());
		assertEquals(ofRUB5("-21.13953"), service.getAverageLossTrade());
		assertEquals(new ConsecutiveTrades(ofRUB5("46.36"), 19), service.getMaximumConsecutiveWins());
		assertEquals(new ConsecutiveTrades(ofRUB5("-114.36"), 3), service.getMaximumConsecutiveLosses());
		assertEquals(new ConsecutiveTrades(ofRUB5("123.45"), 10), service.getMaximalConsecutiveProfit());
		assertEquals(new ConsecutiveTrades(ofRUB5("543.21"), 15), service.getMaximalConsecutiveLoss());
		assertEquals(5, service.getAverageConsecutiveWins());
		assertEquals(7, service.getAverageConsecutiveLosses());
	}
	
	@Test
	public void testGetProfitFactor_IfZeroGrossLoss() {
		service = new TradingStatisticsImpl(
				ofRUB5("1734.24"),		// gross profit
				ZERO_RUB5,				// gross loss
				ZERO_RUB5,
				ZERO_RUB5,			
				ZERO_RUB5,
				ZERO_RUB5,
				1,
				1,
				1,
				1,
				1,
				1,
				zero_ct,
				zero_ct,
				zero_ct,
				zero_ct
			);
		
		assertEquals(of("1734.24000"), service.getProfitFactor());
	}
	
	@Test
	public void testGetExpectedPayoff_IfZeroTotalTrades() {
		service = new TradingStatisticsImpl(
				ofRUB5("1734.24"),		// gross profit
				ZERO_RUB5,				// gross loss
				ZERO_RUB5,
				ZERO_RUB5,			
				ZERO_RUB5,
				ZERO_RUB5,
				0,
				0,
				0,
				0,
				0,
				0,
				zero_ct,
				zero_ct,
				zero_ct,
				zero_ct
			);
		
		assertEquals(ZERO_RUB5, service.getExpectedPayoff());
	}
	
	@Test
	public void testGetAverageProfitTrade_IfNoProfitTrades() {
		service = new TradingStatisticsImpl(
				ofRUB5("1734.24"),		// gross profit
				ofRUB5("-1352.93"),		// gross loss
				ZERO_RUB5,
				ZERO_RUB5,			
				ZERO_RUB5,
				ZERO_RUB5,
				0,
				0,
				0,
				0,
				0,
				0,
				zero_ct,
				zero_ct,
				zero_ct,
				zero_ct
			);
		
		assertEquals(ZERO_RUB5, service.getAverageProfitTrade());
	}
	
	@Test
	public void testGetAverageLossTrade_IfNoLossTrades() {
		service = new TradingStatisticsImpl(
				ofRUB5("1734.24"),		// gross profit
				ofRUB5("-1352.93"),		// gross loss
				ZERO_RUB5,
				ZERO_RUB5,			
				ZERO_RUB5,
				ZERO_RUB5,
				0,
				0,
				0,
				0,
				0,
				0,
				zero_ct,
				zero_ct,
				zero_ct,
				zero_ct
			);
		
		assertEquals(ZERO_RUB5, service.getAverageLossTrade());
	}

}
