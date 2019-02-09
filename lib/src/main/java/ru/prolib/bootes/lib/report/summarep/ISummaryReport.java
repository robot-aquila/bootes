package ru.prolib.bootes.lib.report.summarep;

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
	
	SRTradeSSI getMaximumConsecutiveWins();
	SRTradeSSI getMaximumConsecutiveLosses();
	SRTradeSSI getMaximalConsecutiveProfit();
	SRTradeSSI getMaximalConsecutiveLoss();
	
	int getAverageConsecutiveWins();
	int getAverageConsecutiveLosses();
	
}
