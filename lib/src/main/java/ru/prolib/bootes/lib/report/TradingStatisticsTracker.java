package ru.prolib.bootes.lib.report;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class TradingStatisticsTracker {
	private CDecimal
		grossProfit, grossLoss, 
		absoluteDrawdown, maximalDrawdown,
		largestProfitTrade, largestLossTrade;
	private ConsecutiveTrades
		maximumConsecutiveWins, maximumConsecutiveLosses,
		maximalConsecutiveProfit, maximalConsecutiveLoss;
	private int
		shortPositions, winningShortPositions,
		longPositions, winningLongPositions,
		averageConsecutiveWins, averageConsecutiveLosses;
	
	/**
	 * Financial result of currently open series of wins or losses.
	 * If this value is not defined then series is not open (work just started). 
	 */
	private CDecimal seriesResult;
	
	/**
	 * Number of trades in currently open series of wins or losses.
	 */
	private int seriesTrades;
	
	/**
	 * Total number series of wins. 
	 */
	private int countSeriesOfWins;
	
	/**
	 * Total number series of losses.
	 */
	private int countSeriesOfLosses;
	
	public TradingStatisticsTracker(CDecimal zeroMoney) {
		grossProfit = zeroMoney;
		grossLoss = zeroMoney;
		absoluteDrawdown = zeroMoney;
		maximalDrawdown = zeroMoney;
		largestProfitTrade = zeroMoney;
		largestLossTrade = zeroMoney;
		maximumConsecutiveWins = new ConsecutiveTrades(zeroMoney, 0);
		maximumConsecutiveLosses = new ConsecutiveTrades(zeroMoney, 0);
		maximalConsecutiveProfit = new ConsecutiveTrades(zeroMoney, 0);
		maximalConsecutiveLoss = new ConsecutiveTrades(zeroMoney, 0);
	}
	
	public TradingStatisticsTracker() {
		this(CDecimalBD.ZERO_RUB5);
	}
	
	private boolean isSeriesOfWinsOpen() {
		return seriesResult != null
			&& seriesResult.withUnit(null).compareTo(ZERO) > 0;
	}
	
	private boolean isSeriesOfLossesOpen() {
		return seriesResult != null
			&& seriesResult.withUnit(null).compareTo(ZERO) <= 0;
	}
	
	private ConsecutiveTrades getSeriesStats() {
		return new ConsecutiveTrades(seriesResult, seriesTrades);
	}
	
	private int getWinners() {
		return winningShortPositions + winningLongPositions;
	}
	
	private int getLosers() {
		return shortPositions + longPositions - getWinners();
	}
	
	private void refreshSeriesStats() {
		if ( isSeriesOfWinsOpen() ) {
			if ( maximumConsecutiveWins.getCount() < seriesTrades ) {
				maximumConsecutiveWins = getSeriesStats();
			}
			if ( maximalConsecutiveProfit.getPnL().compareTo(seriesResult) < 0 ) {
				maximalConsecutiveProfit = getSeriesStats();
			}
		} else if ( isSeriesOfLossesOpen() ) {
			if ( maximumConsecutiveLosses.getCount() < seriesTrades ) {
				maximumConsecutiveLosses = getSeriesStats();
			}
			if ( maximalConsecutiveLoss.getPnL().compareTo(seriesResult) > 0 ) {
				maximalConsecutiveLoss = getSeriesStats();
			}
		}
		if ( countSeriesOfWins > 0 ) {
			averageConsecutiveWins = getWinners() / countSeriesOfWins;
		}
		if ( countSeriesOfLosses > 0 ) {
			averageConsecutiveLosses = getLosers() / countSeriesOfLosses;
		}
	}
	
	public void add(TradeResult sr) {
		CDecimal pnl = sr.getPnL(), pnl_abstract = pnl.withUnit(null);
		boolean isProfit = pnl_abstract.compareTo(ZERO) > 0;
		
		if ( isProfit ) {
			grossProfit = grossProfit.add(pnl);
			if ( pnl.compareTo(largestProfitTrade) > 0 ) {
				largestProfitTrade = pnl;
			}
			if ( ! isSeriesOfWinsOpen() ) {
				seriesResult = pnl;
				seriesTrades = 1;
				countSeriesOfWins ++;
			} else {
				seriesResult = seriesResult.add(pnl);
				seriesTrades ++;
			}
			
		} else {
			grossLoss = grossLoss.add(pnl);
			if ( pnl.compareTo(largestLossTrade) < 0 ) {
				largestLossTrade = pnl;
			}
			if ( ! isSeriesOfLossesOpen() ) {
				seriesResult = pnl;
				seriesTrades = 1;
				countSeriesOfLosses ++;
			} else {
				seriesResult = seriesResult.add(pnl);
				seriesTrades ++;
			}
		}
		
		if ( sr.isLong() ) {
			longPositions ++;
			if ( isProfit ) {
				winningLongPositions ++;
			}
			
		} else {
			shortPositions ++;
			if ( isProfit ) {
				winningShortPositions ++;
			}
		}
		
		refreshSeriesStats();
	}
	
	public ITradingStatistics getCurrentStats() {
		return new TradingStatisticsImpl(
				grossProfit,
				grossLoss,
				absoluteDrawdown,
				maximalDrawdown,
				largestProfitTrade,
				largestLossTrade,
				shortPositions,
				winningShortPositions,
				longPositions,
				winningLongPositions,
				averageConsecutiveWins,
				averageConsecutiveLosses,
				maximumConsecutiveWins,
				maximumConsecutiveLosses,
				maximalConsecutiveProfit,
				maximalConsecutiveLoss
			);
	}

}
