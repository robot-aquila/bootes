package ru.prolib.bootes.lib.report;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class SummaryReport implements ISummaryReport {
	private final CDecimal
		grossProfit, grossLoss,
		absoluteDrawdown, maximalDrawdown,
		largestProfitTrade, largestLossTrade;
	private final int
		shortPositions, winningShortPositions,
		longPositions, winningLongPositions,
		averageConsecutiveWins, averageConsecutiveLosses;
	private final ConsecutiveTrades
		maximumConsecutiveWins, maximumConsecutiveLosses,
		maximalConsecutiveProfit, maximalConsecutiveLoss;
	
	public SummaryReport(
			CDecimal grossProfit,
			CDecimal grossLoss,
			CDecimal absoluteDrawdown,
			CDecimal maximalDrawdown,
			CDecimal largestProfitTrade,
			CDecimal largestLossTrade,
			int shortPositions,
			int winningShortPositions,
			int longPositions,
			int winningLongPositions,
			int averageConsecutiveWins,
			int averageConsecutiveLosses,
			ConsecutiveTrades maximumConsecutiveWins,
			ConsecutiveTrades maximumConsecutiveLosses,
			ConsecutiveTrades maximalConsecutiveProfit,
			ConsecutiveTrades maximalConsecutiveLoss)
	{
		this.grossProfit = grossProfit;
		this.grossLoss = grossLoss;
		this.absoluteDrawdown = absoluteDrawdown;
		this.maximalDrawdown = maximalDrawdown;
		this.largestProfitTrade = largestProfitTrade;
		this.largestLossTrade = largestLossTrade;
		this.shortPositions = shortPositions;
		this.winningShortPositions = winningShortPositions;
		this.longPositions = longPositions;
		this.winningLongPositions = winningLongPositions;
		this.averageConsecutiveWins = averageConsecutiveWins;
		this.averageConsecutiveLosses = averageConsecutiveLosses;
		this.maximumConsecutiveWins = maximumConsecutiveWins;
		this.maximumConsecutiveLosses = maximumConsecutiveLosses;
		this.maximalConsecutiveProfit = maximalConsecutiveProfit;
		this.maximalConsecutiveLoss = maximalConsecutiveLoss;
	}

	@Override
	public CDecimal getTotalNetProfit() {
		return grossProfit.add(grossLoss);
	}

	@Override
	public CDecimal getGrossProfit() {
		return grossProfit;
	}

	@Override
	public CDecimal getGrossLoss() {
		return grossLoss;
	}

	@Override
	public CDecimal getProfitFactor() {
		if ( grossLoss.equals(grossLoss.withZero()) ) {
			return grossProfit.withUnit(null);
		} else {
			return grossProfit.divide(grossLoss).abs();
		}
	}

	@Override
	public CDecimal getExpectedPayoff() {
		long total_trades = getTotalTrades();
		return total_trades == 0L ?
			getTotalNetProfit().withZero() :
			getTotalNetProfit().divide(total_trades);
	}

	@Override
	public CDecimal getAbsoluteDrawdown() {
		return absoluteDrawdown;
	}

	@Override
	public CDecimal getMaximalDrawdown() {
		return maximalDrawdown;
	}

	@Override
	public int getTotalTrades() {
		return shortPositions + longPositions;
	}

	@Override
	public int getShortPositions() {
		return shortPositions;
	}

	@Override
	public int getWinningShortPositions() {
		return winningShortPositions;
	}

	@Override
	public int getLongPositions() {
		return longPositions;
	}

	@Override
	public int getWinningLongPositions() {
		return winningLongPositions;
	}

	@Override
	public int getProfitTrades() {
		return getWinningShortPositions() + getWinningLongPositions();
	}

	@Override
	public int getLossTrades() {
		return getTotalTrades() - getProfitTrades();
	}

	@Override
	public CDecimal getLargestProfitTrade() {
		return largestProfitTrade;
	}

	@Override
	public CDecimal getLargestLossTrade() {
		return largestLossTrade;
	}

	@Override
	public CDecimal getAverageProfitTrade() {
		long profit_trades = getProfitTrades();
		return profit_trades == 0L ?
				getGrossProfit().withZero() :
				getGrossProfit().divide(profit_trades);
	}

	@Override
	public CDecimal getAverageLossTrade() {
		long loss_trades = getLossTrades();
		return loss_trades == 0L ?
				getGrossLoss().withZero() :
				getGrossLoss().divide(loss_trades);
	}

	@Override
	public ConsecutiveTrades getMaximumConsecutiveWins() {
		return maximumConsecutiveWins;
	}

	@Override
	public ConsecutiveTrades getMaximumConsecutiveLosses() {
		return maximumConsecutiveLosses;
	}

	@Override
	public ConsecutiveTrades getMaximalConsecutiveProfit() {
		return maximalConsecutiveProfit;
	}

	@Override
	public ConsecutiveTrades getMaximalConsecutiveLoss() {
		return maximalConsecutiveLoss;
	}

	@Override
	public int getAverageConsecutiveWins() {
		return averageConsecutiveWins;
	}

	@Override
	public int getAverageConsecutiveLosses() {
		return averageConsecutiveLosses;
	}

}
