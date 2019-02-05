package ru.prolib.bootes.tsgr001a.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.bootes.lib.report.ITradingStatistics;
import ru.prolib.bootes.lib.report.TradeResult;
import ru.prolib.bootes.lib.report.TradingStatisticsTracker;
import ru.prolib.bootes.lib.report.msr2.Block;
import ru.prolib.bootes.lib.report.msr2.IReport;
import ru.prolib.bootes.lib.report.msr2.Report;
import ru.prolib.bootes.tsgr001a.mscan.sensors.SignalType;
import ru.prolib.bootes.tsgr001a.mscan.sensors.Speculation;

public class RobotStateListenerStats implements RobotStateListener {
	private static final String ID_OPEN = "OPEN";
	private static final String ID_CLOSE = "CLOSE";
	private static final String ID_TAKE_PROFIT = "TAKE_PROFIT";
	private static final String ID_STOP_LOSS = "STOP_LOSS";
	private static final String ID_BREAK_EVEN = "BREAK_EVEN";
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(RobotStateListenerStats.class);
	}
	
	private final RobotState state;
	private final TradingStatisticsTracker tracker;
	private IReport currSpecReport;
	
	public RobotStateListenerStats(RobotState state) {
		this.state = state;
		this.tracker = new TradingStatisticsTracker();
	}
	
	private Speculation getSpeculation() {
		synchronized ( state ) {
			return state.getActiveSpeculation();
		}
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
		Speculation spec = getSpeculation();
		synchronized ( spec ) {
			Tick en_p = spec.getEntryPoint(); 
			currSpecReport = new Report(new Block(ID_OPEN, en_p.getPrice(), en_p.getTime()));
		}
		synchronized ( state ) {
			state.getReportStorage().addReport(currSpecReport);
		}
	}
	
	@Override
	public void speculationUpdate() {
		Speculation spec = getSpeculation();
		synchronized ( spec ) {
			currSpecReport.setBlock(new Block(ID_TAKE_PROFIT, spec.getTakeProfit(), null));
			currSpecReport.setBlock(new Block(ID_STOP_LOSS, spec.getStopLoss(), null));
			currSpecReport.setBlock(new Block(ID_BREAK_EVEN, spec.getBreakEven(), null));			
		}
	}

	@Override
	public void speculationClosed() {
		Speculation spec = getSpeculation();
		TradeResult tr = null;
		synchronized ( spec ) {
			tr = new TradeResult(
					spec.getTradeSignal().getTime(),
					spec.getExitPoint().getTime(),
					spec.getSignalType() == SignalType.BUY,
					spec.getResult(),
					spec.getExitPoint().getSize()
				);
			Tick ex_p = spec.getExitPoint();
			currSpecReport.setBlock(new Block(ID_CLOSE, ex_p.getPrice(), ex_p.getTime()));
			currSpecReport.setBlock(new Block(ID_TAKE_PROFIT, spec.getTakeProfit(), ex_p.getTime()));
			currSpecReport.setBlock(new Block(ID_STOP_LOSS, spec.getStopLoss(), ex_p.getTime()));
			currSpecReport.setBlock(new Block(ID_BREAK_EVEN, spec.getBreakEven(), ex_p.getTime()));
			currSpecReport = null;
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
