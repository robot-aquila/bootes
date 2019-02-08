package ru.prolib.bootes.tsgr001a.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.bootes.lib.report.ISummaryReport;
import ru.prolib.bootes.lib.report.TradeResult;
import ru.prolib.bootes.lib.report.SummaryReportTracker;
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
	private final SummaryReportTracker tracker;
	private IReport currSpecReport;
	
	public RobotStateListenerStats(RobotState state) {
		this.state = state;
		this.tracker = new SummaryReportTracker();
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
		ISummaryReport report = null;
		synchronized ( tracker ) {
			report= tracker.getCurrentStats();
		}
		String ls = System.lineSeparator();
		String str_report = new StringBuilder()
			.append("------- Trading statistics -------").append(ls)
			.append("    Total net profit: ").append(report.getTotalNetProfit()).append(ls)
			.append("        Gross profit: ").append(report.getGrossProfit()).append(ls)
			.append("          Gross loss: ").append(report.getGrossLoss()).append(ls)
			.append("       Profit factor: ").append(report.getProfitFactor()).append(ls)
			.append("     Expected payoff: ").append(report.getExpectedPayoff()).append(ls)
			.append("   Absolute drawdown: ").append(report.getAbsoluteDrawdown()).append(ls)
			.append("    Maximal drawdown: ").append(report.getMaximalDrawdown()).append(ls)
			.append("        Total trades: ").append(report.getTotalTrades()).append(ls)
			.append("     Short positions: ").append(report.getShortPositions()).append(ls)
			.append("       Short winners: ").append(report.getWinningShortPositions()).append(ls)
			.append("      Long positions: ").append(report.getLongPositions()).append(ls)
			.append("        Long winners: ").append(report.getWinningLongPositions()).append(ls)
			.append("       Profit trades: ").append(report.getProfitTrades()).append(ls)
			.append("         Lost trades: ").append(report.getLossTrades()).append(ls)
			.append("Largest profit trade: ").append(report.getLargestProfitTrade()).append(ls)
			.append("  Largest loss trade: ").append(report.getLargestLossTrade()).append(ls)
			.append("Average profit trade: ").append(report.getAverageProfitTrade()).append(ls)
			.append("  Average loss trade: ").append(report.getAverageLossTrade()).append(ls)
			.append("  Maximum consecutive wins (count): ")
				.append(report.getMaximumConsecutiveWins().getPnL())
				.append(" (")
				.append(report.getMaximumConsecutiveWins().getCount())
				.append(")")
				.append(ls)
			.append("Maximum consecutive losses (count): ")
				.append(report.getMaximumConsecutiveLosses().getPnL())
				.append(" (")
				.append(report.getMaximumConsecutiveLosses().getCount())
				.append(")")
				.append(ls)
			.append("Maximal consecutive profit (count): ")
				.append(report.getMaximalConsecutiveProfit().getPnL())
				.append(" (")
				.append(report.getMaximalConsecutiveProfit().getCount())
				.append(")")
				.append(ls)
			.append("  Maximal consecutive loss (count): ")
				.append(report.getMaximalConsecutiveLoss().getPnL())
				.append(" (")
				.append(report.getMaximalConsecutiveLoss().getCount())
				.append(")")
				.append(ls)
			.append("  Average consecutive wins: ").append(report.getAverageConsecutiveWins()).append(ls)
			.append("Average consecutive losses: ").append(report.getAverageConsecutiveLosses()).append(ls)
			.toString();
		logger.debug(str_report);
	}

}
