package ru.prolib.bootes.lib.report;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public interface ISummaryReport {
	CDecimal getTotalNetProfit();
	CDecimal getGrossProfit();
	CDecimal getGrossLoss();
	CDecimal getProfitFactor();
	CDecimal getExpectedPayoff();
	CDecimal getAbsoluteDrawdown();
	CDecimal getMaximalDrawdown();
	int getTotalTrades();
	int getShortPositions();
	int getWinningShortPositions();
	int getLongPositions();
	int getWinningLongPositions();
	int getProfitTrades();
	int getLossTrades();
	CDecimal getLargestProfitTrade();
	CDecimal getLargestLossTrade();
	CDecimal getAverageProfitTrade();
	CDecimal getAverageLossTrade();
	
	ConsecutiveTrades getMaximumConsecutiveWins();
	ConsecutiveTrades getMaximumConsecutiveLosses();
	ConsecutiveTrades getMaximalConsecutiveProfit();
	ConsecutiveTrades getMaximalConsecutiveLoss();
	
	int getAverageConsecutiveWins();
	int getAverageConsecutiveLosses();
	
}
