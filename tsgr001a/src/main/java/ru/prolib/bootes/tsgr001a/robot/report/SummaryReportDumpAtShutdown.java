package ru.prolib.bootes.tsgr001a.robot.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.bootes.lib.report.summarep.ISummaryReport;
import ru.prolib.bootes.lib.report.summarep.ISummaryReportTracker;
import ru.prolib.bootes.lib.robo.s3.S3RobotStateListenerStub;

public class SummaryReportDumpAtShutdown extends S3RobotStateListenerStub {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SummaryReportDumpAtShutdown.class);
	}
	
	private final ISummaryReportTracker tracker;
	
	public SummaryReportDumpAtShutdown(ISummaryReportTracker tracker) {
		this.tracker = tracker;
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
