package ru.prolib.bootes.lib.report;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class SummaryReportTrackerTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private SummaryReportTracker service;

	@Before
	public void setUp() throws Exception {
		service = new SummaryReportTracker();
	}
	
	@Test
	public void testDefaults() {
		ISummaryReport stats = service.getCurrentStats();
		
		assertNotNull(stats);
		assertEquals(ZERO_RUB5, stats.getTotalNetProfit());
		assertEquals(ZERO_RUB5, stats.getGrossProfit());
		assertEquals(ZERO_RUB5, stats.getGrossLoss());
		assertEquals(of("0.00000"), stats.getProfitFactor());
		assertEquals(ZERO_RUB5, stats.getExpectedPayoff());
		assertEquals(ZERO_RUB5, stats.getAbsoluteDrawdown());
		assertEquals(ZERO_RUB5, stats.getMaximalDrawdown());
		assertEquals(0, stats.getTotalTrades());
		assertEquals(0, stats.getShortPositions());
		assertEquals(0, stats.getWinningShortPositions());
		assertEquals(0, stats.getLongPositions());
		assertEquals(0, stats.getWinningLongPositions());
		assertEquals(0, stats.getProfitTrades());
		assertEquals(0, stats.getLossTrades());
		assertEquals(ZERO_RUB5, stats.getLargestProfitTrade());
		assertEquals(ZERO_RUB5, stats.getLargestLossTrade());
		assertEquals(ZERO_RUB5, stats.getAverageProfitTrade());
		assertEquals(ZERO_RUB5, stats.getAverageLossTrade());
		assertEquals(new ConsecutiveTrades(ZERO_RUB5, 0), stats.getMaximumConsecutiveWins());
		assertEquals(new ConsecutiveTrades(ZERO_RUB5, 0), stats.getMaximumConsecutiveLosses());
		assertEquals(new ConsecutiveTrades(ZERO_RUB5, 0), stats.getMaximalConsecutiveProfit());
		assertEquals(new ConsecutiveTrades(ZERO_RUB5, 0), stats.getMaximalConsecutiveLoss());
		assertEquals(0, stats.getAverageConsecutiveWins());
		assertEquals(0, stats.getAverageConsecutiveLosses());
	}
	
	@Test
	public void testAdd_SimpleCase() {
		service.add(new TradeResult(		// long +
				T("2019-01-14T13:25:11Z"),
				T("2019-01-14T13:27:34Z"),
				true,
				ofRUB5("125.96"),
				of(10L)
			));
		service.add(new TradeResult(		// long -
				T("2019-01-14T13:41:56Z"),
				T("2019-01-14T13:42:15Z"),
				true,
				ofRUB5("-140.12"),
				of(9L)
			));
		service.add(new TradeResult(		// short -
				T("2019-01-14T13:46:28Z"),
				T("2019-01-14T13:49:19Z"),
				false,
				ofRUB5("-143.08"),
				of(7L)
			));
		service.add(new TradeResult(		// long +
				T("2019-01-14T13:51:51Z"),
				T("2019-01-14T13:55:22Z"),
				true,
				ofRUB5("68.14"),
				of(5L)
			));
		service.add(new TradeResult(		// long +
				T("2019-01-14T14:12:05Z"),
				T("2019-01-14T14:16:27Z"),
				true,
				ofRUB5("18.09"),
				of(2L)
			));
		service.add(new TradeResult(		// long +
				T("2019-01-14T14:16:29Z"),
				T("2019-01-14T14:16:35Z"),
				true,
				ofRUB5("52.13"),
				of(4L)
			));
		service.add(new TradeResult(		// short +
				T("2019-01-14T14:27:19Z"),
				T("2019-01-14T15:31:20Z"),
				false,
				ofRUB5("250.43"),
				of(17L)
			));
		service.add(new TradeResult(		// short +
				T("2019-01-14T16:00:05Z"),
				T("2019-01-14T16:12:47Z"),
				false,
				ofRUB5("85.43"),
				of(8L)
			));
		ISummaryReport stats = service.getCurrentStats();
		
		assertNotNull(stats);
		assertEquals(ofRUB5("316.98"), stats.getTotalNetProfit());
		assertEquals(ofRUB5("600.18"), stats.getGrossProfit());
		assertEquals(ofRUB5("-283.20"), stats.getGrossLoss());
		assertEquals(of("2.11928"), stats.getProfitFactor());
		assertEquals(ofRUB5("39.62250"), stats.getExpectedPayoff());
		assertEquals(ZERO_RUB5, stats.getAbsoluteDrawdown());	// TODO:
		assertEquals(ZERO_RUB5, stats.getMaximalDrawdown());	// TODO:
		assertEquals(8, stats.getTotalTrades());
		assertEquals(3, stats.getShortPositions());
		assertEquals(2, stats.getWinningShortPositions());
		assertEquals(5, stats.getLongPositions());
		assertEquals(4, stats.getWinningLongPositions());
		assertEquals(6, stats.getProfitTrades());
		assertEquals(2, stats.getLossTrades());
		assertEquals(ofRUB5("250.43"), stats.getLargestProfitTrade());
		assertEquals(ofRUB5("-143.08"), stats.getLargestLossTrade());
		assertEquals(ofRUB5("100.03"), stats.getAverageProfitTrade());
		assertEquals(ofRUB5("-141.6"), stats.getAverageLossTrade());
		assertEquals(new ConsecutiveTrades(ofRUB5("474.22"), 5), stats.getMaximumConsecutiveWins());
		assertEquals(new ConsecutiveTrades(ofRUB5("-283.2"), 2), stats.getMaximumConsecutiveLosses());
		// this case they are same
		assertEquals(new ConsecutiveTrades(ofRUB5("474.22"), 5), stats.getMaximalConsecutiveProfit());
		assertEquals(new ConsecutiveTrades(ofRUB5("-283.2"), 2), stats.getMaximalConsecutiveLoss());
		assertEquals(3, stats.getAverageConsecutiveWins());
		assertEquals(2, stats.getAverageConsecutiveLosses());
	}

}
