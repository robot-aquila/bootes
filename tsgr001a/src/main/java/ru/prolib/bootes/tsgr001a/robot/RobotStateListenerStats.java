package ru.prolib.bootes.tsgr001a.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.bootes.lib.report.ITradingStatistics;
import ru.prolib.bootes.lib.report.TradeResult;
import ru.prolib.bootes.lib.report.TradingStatisticsTracker;
import ru.prolib.bootes.tsgr001a.mscan.sensors.SignalType;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;

public class RobotStateListenerStats implements RobotStateListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(RobotStateListenerStats.class);
	}
	
	private final RobotState state;
	private final TradingStatisticsTracker tracker;
	
	public RobotStateListenerStats(RobotState state) {
		this.state = state;
		this.tracker = new TradingStatisticsTracker();
	}

	@Override
	public void robotStarted() {

	}

	@Override
	public void accountSelected() {

	}

	@Override
	public void contractSelected() {

	}

	@Override
	public void sessionDataAvailable() {

	}

	@Override
	public void riskManagementUpdate() {

	}

	@Override
	public void speculationOpened() {

	}

	@Override
	public void speculationClosed() {
		Speculation spec = null;
		synchronized ( state ) {
			spec = state.getActiveSpeculation();
		}
		TradeResult tr = null;
		synchronized ( spec ) {
			tr = new TradeResult(
					spec.getTradeSignal().getTime(),
					spec.getExitPoint().getTime(),
					spec.getSignalType() == SignalType.BUY,
					spec.getResult(),
					spec.getExitPoint().getSize()
				);
		}
		synchronized ( tracker ) {
			tracker.add(tr);
		}
	}

	@Override
	public void sessionDataCleanup() {

	}

	@Override
	public void robotStopped() {
		logger.debug("Stopping...");
		ITradingStatistics stats = null;
		synchronized ( tracker ) {
			stats= tracker.getCurrentStats();
		}
		String ls = System.lineSeparator();
		String report = new StringBuilder()
			.append("------- Trading statistics -------").append(ls)
			.append("    Total net profit: ").append(stats.getTotalNetProfit()).append(ls)
			.append("        Gross profit: ").append(stats.getGrossProfit()).append(ls)
			.append("          Gross loss: ").append(stats.getGrossLoss()).append(ls)
			.append("       Profit factor: ").append(stats.getProfitFactor()).append(ls)
			.append("     Expected payoff: ").append(stats.getExpectedPayoff()).append(ls)
			.append("   Absolute drawdown: ").append(stats.getAbsoluteDrawdown()).append(ls)
			.append("    Maximal drawdown: ").append(stats.getMaximalDrawdown()).append(ls)
			.append("        Total trades: ").append(stats.getTotalTrades()).append(ls)
			.append("     Short positions: ").append(stats.getShortPositions()).append(ls)
			.append("       Short winners: ").append(stats.getWinningShortPositions()).append(ls)
			.append("      Long positions: ").append(stats.getLongPositions()).append(ls)
			.append("        Long winners: ").append(stats.getWinningLongPositions()).append(ls)
			.append("       Profit trades: ").append(stats.getProfitTrades()).append(ls)
			.append("         Lost trades: ").append(stats.getLossTrades()).append(ls)
			.append("Largest profit trade: ").append(stats.getLargestProfitTrade()).append(ls)
			.append("  Largest loss trade: ").append(stats.getLargestLossTrade()).append(ls)
			.append("Average profit trade: ").append(stats.getAverageProfitTrade()).append(ls)
			.append("  Average loss trade: ").append(stats.getAverageLossTrade()).append(ls)
			.append("  Maximum consecutive wins (count): ")
				.append(stats.getMaximumConsecutiveWins().getPnL())
				.append(" (")
				.append(stats.getMaximumConsecutiveWins().getCount())
				.append(")")
				.append(ls)
			.append("Maximum consecutive losses (count): ")
				.append(stats.getMaximumConsecutiveLosses().getPnL())
				.append(" (")
				.append(stats.getMaximumConsecutiveLosses().getCount())
				.append(")")
				.append(ls)
			.append("Maximal consecutive profit (count): ")
				.append(stats.getMaximalConsecutiveProfit().getPnL())
				.append(" (")
				.append(stats.getMaximalConsecutiveProfit().getCount())
				.append(")")
				.append(ls)
			.append("  Maximal consecutive loss (count): ")
				.append(stats.getMaximalConsecutiveLoss().getPnL())
				.append(" (")
				.append(stats.getMaximalConsecutiveLoss().getCount())
				.append(")")
				.append(ls)
			.append("  Average consecutive wins: ").append(stats.getAverageConsecutiveWins()).append(ls)
			.append("Average consecutive losses: ").append(stats.getAverageConsecutiveLosses()).append(ls)
			.toString();
		logger.debug(report);
	}

}
