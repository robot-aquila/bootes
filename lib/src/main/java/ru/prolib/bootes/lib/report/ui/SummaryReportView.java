package ru.prolib.bootes.lib.report.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.bootes.lib.report.ConsecutiveTrades;
import ru.prolib.bootes.lib.report.ISummaryReport;

public class SummaryReportView extends JPanel implements ISummaryReportView {
	private static final long serialVersionUID = 1L;
	
	private final IMessages messages;
	private final JLabel
		labTotalNetProfit = new JLabel(),
		valTotalNetProfit = new JLabel(),
		labGrossProfit = new JLabel(),
		valGrossProfit = new JLabel(),
		labGrossLoss = new JLabel(),
		valGrossLoss = new JLabel(),
		labProfitFactor = new JLabel(),
		valProfitFactor = new JLabel(),
		labExpectedPayoff = new JLabel(),
		valExpectedPayoff = new JLabel(),
		labAbsoluteDrawdown = new JLabel(),
		valAbsoluteDrawdown = new JLabel(),
		labMaximalDrawdown = new JLabel(),
		valMaximalDrawdown = new JLabel(),
		labTotalTrades = new JLabel(),
		valTotalTrades = new JLabel(),
		labShortPositions = new JLabel(),
		valShortPositions = new JLabel(),
		labWinningShortPositions = new JLabel(),
		valWinningShortPositions = new JLabel(),
		labLongPositions = new JLabel(),
		valLongPositions = new JLabel(),
		labWinningLongPositions = new JLabel(),
		valWinningLongPositions = new JLabel(),
		labProfitTrades = new JLabel(),
		valProfitTrades = new JLabel(),
		labLossTrades = new JLabel(),
		valLossTrades = new JLabel(),
		labLargestProfitTrade = new JLabel(),
		valLargestProfitTrade = new JLabel(),
		labLargestLossTrade = new JLabel(),
		valLargestLossTrade = new JLabel(),
		labAverageProfitTrade = new JLabel(),
		valAverageProfitTrade = new JLabel(),
		labAverageLossTrade = new JLabel(),
		valAverageLossTrade = new JLabel(),
		labMaximumConsecutiveWins = new JLabel(),
		valMaximumConsecutiveWins = new JLabel(),
		labMaximumConsecutiveLosses = new JLabel(),
		valMaximumConsecutiveLosses = new JLabel(),
		labMaximalConsecutiveProfit = new JLabel(),
		valMaximalConsecutiveProfit = new JLabel(),
		labMaximalConsecutiveLoss = new JLabel(),
		valMaximalConsecutiveLoss = new JLabel(),
		labAverageConsecutiveWins = new JLabel(),
		valAverageConsecutiveWins = new JLabel(),
		labAverageConsecutiveLosses = new JLabel(),
		valAverageConsecutiveLosses = new JLabel();
	
	public SummaryReportView(IMessages messages) {
		super(new MigLayout());
		this.messages = messages;
		add(SummaryReportMsg.TOTAL_NET_PROFIT, labTotalNetProfit, valTotalNetProfit);
		add(SummaryReportMsg.GROSS_PROFIT, labGrossProfit, valGrossProfit);
		add(SummaryReportMsg.GROSS_LOSS, labGrossLoss, valGrossLoss);
		add(SummaryReportMsg.PROFIT_FACTOR, labProfitFactor, valProfitFactor);
		add(SummaryReportMsg.EXPECTED_PAYOFF, labExpectedPayoff, valExpectedPayoff);
		add(SummaryReportMsg.ABSOLUTE_DRAWDOWN, labAbsoluteDrawdown, valAbsoluteDrawdown);
		add(SummaryReportMsg.MAXIMAL_DRAWDOWN, labMaximalDrawdown, valMaximalDrawdown);
		add(SummaryReportMsg.TOTAL_TRADES, labTotalTrades, valTotalTrades);
		add(SummaryReportMsg.SHORT_POSITIONS, labShortPositions, valShortPositions);
		add(SummaryReportMsg.WINNING_SHORT_POSITIONS, labWinningShortPositions, valWinningShortPositions);
		add(SummaryReportMsg.LONG_POSITIONS, labLongPositions, valLongPositions);
		add(SummaryReportMsg.WINNING_LONG_POSITIONS, labWinningLongPositions, valWinningLongPositions);
		add(SummaryReportMsg.PROFIT_TRADES, labProfitTrades, valProfitTrades);
		add(SummaryReportMsg.LOSS_TRADES, labLossTrades, valLossTrades);
		add(SummaryReportMsg.LARGEST_PROFIT_TRADE, labLargestProfitTrade, valLargestProfitTrade);
		add(SummaryReportMsg.LARGEST_LOSS_TRADE, labLargestLossTrade, valLargestLossTrade);
		add(SummaryReportMsg.AVERAGE_PROFIT_TRADE, labAverageProfitTrade, valAverageProfitTrade);
		add(SummaryReportMsg.AVERAGE_LOSS_TRADE, labAverageLossTrade, valAverageLossTrade);
		add(SummaryReportMsg.MAXIMUM_CONSECUTIVE_WINS, labMaximumConsecutiveWins, valMaximumConsecutiveWins);
		add(SummaryReportMsg.MAXIMUM_CONSECUTIVE_LOSSES, labMaximumConsecutiveLosses, valMaximumConsecutiveLosses);
		add(SummaryReportMsg.MAXIMAL_CONSECUTIVE_PROFIT, labMaximalConsecutiveProfit, valMaximalConsecutiveProfit);
		add(SummaryReportMsg.MAXIMAL_CONSECUTIVE_LOSS, labMaximalConsecutiveLoss, valMaximalConsecutiveLoss);
		add(SummaryReportMsg.AVERAGE_CONSECUTIVE_WINS, labAverageConsecutiveWins, valAverageConsecutiveWins);
		add(SummaryReportMsg.AVERAGE_CONSECUTIVE_LOSSES, labAverageConsecutiveLosses, valAverageConsecutiveLosses);
	}
	
	private void add(MsgID msgID, JLabel label, JLabel value) {
		label.setText(messages.get(msgID));
		label.setLabelFor(valTotalNetProfit);
		add(label, "align right");
		add(value, "wrap");		
	}
	
	private String decimal(CDecimal value) {
		return value.toString();
	}
	
	private String integer(int value) {
		return Integer.toString(value);
	}
	
	private String consecutive_trades(ConsecutiveTrades value) {
		return messages.format(SummaryReportMsg.CONSECUTIVE_TRADES_TPL, value.getPnL(), value.getCount());
	}
	
	@Override
	public void update(ISummaryReport report) {
		valTotalNetProfit.setText(decimal(report.getTotalNetProfit()));
		valGrossProfit.setText(decimal(report.getGrossProfit()));
		valGrossLoss.setText(decimal(report.getGrossLoss()));
		valProfitFactor.setText(decimal(report.getProfitFactor()));
		valExpectedPayoff.setText(decimal(report.getExpectedPayoff()));
		valAbsoluteDrawdown.setText(decimal(report.getAbsoluteDrawdown()));
		valMaximalDrawdown.setText(decimal(report.getMaximalDrawdown()));
		valTotalTrades.setText(integer(report.getTotalTrades()));
		valShortPositions.setText(integer(report.getShortPositions()));
		valWinningShortPositions.setText(integer(report.getWinningShortPositions()));
		valLongPositions.setText(integer(report.getLongPositions()));
		valWinningLongPositions.setText(integer(report.getWinningLongPositions()));
		valProfitTrades.setText(integer(report.getProfitTrades()));
		valLossTrades.setText(integer(report.getLossTrades()));
		valLargestProfitTrade.setText(decimal(report.getLargestProfitTrade()));
		valLargestLossTrade.setText(decimal(report.getLargestLossTrade()));
		valAverageProfitTrade.setText(decimal(report.getAverageProfitTrade()));
		valAverageLossTrade.setText(decimal(report.getAverageLossTrade()));
		valMaximumConsecutiveWins.setText(consecutive_trades(report.getMaximumConsecutiveWins()));
		valMaximumConsecutiveLosses.setText(consecutive_trades(report.getMaximumConsecutiveLosses()));
		valMaximalConsecutiveProfit.setText(consecutive_trades(report.getMaximalConsecutiveProfit()));
		valMaximalConsecutiveLoss.setText(consecutive_trades(report.getMaximalConsecutiveLoss()));
		valAverageConsecutiveWins.setText(integer(report.getAverageConsecutiveWins()));
		valAverageConsecutiveLosses.setText(integer(report.getAverageConsecutiveLosses()));
	}

}
