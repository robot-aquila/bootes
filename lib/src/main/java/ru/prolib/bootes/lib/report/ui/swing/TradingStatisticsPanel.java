package ru.prolib.bootes.lib.report.ui.swing;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.bootes.lib.report.ConsecutiveTrades;
import ru.prolib.bootes.lib.report.ITradingStatistics;

public class TradingStatisticsPanel extends JPanel {
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
	
	public TradingStatisticsPanel(IMessages messages) {
		super(new MigLayout());
		this.messages = messages;
		add(TradingStatisticsMsg.TOTAL_NET_PROFIT, labTotalNetProfit, valTotalNetProfit);
		add(TradingStatisticsMsg.GROSS_PROFIT, labGrossProfit, valGrossProfit);
		add(TradingStatisticsMsg.GROSS_LOSS, labGrossLoss, valGrossLoss);
		add(TradingStatisticsMsg.PROFIT_FACTOR, labProfitFactor, valProfitFactor);
		add(TradingStatisticsMsg.EXPECTED_PAYOFF, labExpectedPayoff, valExpectedPayoff);
		add(TradingStatisticsMsg.ABSOLUTE_DRAWDOWN, labAbsoluteDrawdown, valAbsoluteDrawdown);
		add(TradingStatisticsMsg.MAXIMAL_DRAWDOWN, labMaximalDrawdown, valMaximalDrawdown);
		add(TradingStatisticsMsg.TOTAL_TRADES, labTotalTrades, valTotalTrades);
		add(TradingStatisticsMsg.SHORT_POSITIONS, labShortPositions, valShortPositions);
		add(TradingStatisticsMsg.WINNING_SHORT_POSITIONS, labWinningShortPositions, valWinningShortPositions);
		add(TradingStatisticsMsg.LONG_POSITIONS, labLongPositions, valLongPositions);
		add(TradingStatisticsMsg.WINNING_LONG_POSITIONS, labWinningLongPositions, valWinningLongPositions);
		add(TradingStatisticsMsg.PROFIT_TRADES, labProfitTrades, valProfitTrades);
		add(TradingStatisticsMsg.LOSS_TRADES, labLossTrades, valLossTrades);
		add(TradingStatisticsMsg.LARGEST_PROFIT_TRADE, labLargestProfitTrade, valLargestProfitTrade);
		add(TradingStatisticsMsg.LARGEST_LOSS_TRADE, labLargestLossTrade, valLargestLossTrade);
		add(TradingStatisticsMsg.AVERAGE_PROFIT_TRADE, labAverageProfitTrade, valAverageProfitTrade);
		add(TradingStatisticsMsg.AVERAGE_LOSS_TRADE, labAverageLossTrade, valAverageLossTrade);
		add(TradingStatisticsMsg.MAXIMUM_CONSECUTIVE_WINS, labMaximumConsecutiveWins, valMaximumConsecutiveWins);
		add(TradingStatisticsMsg.MAXIMUM_CONSECUTIVE_LOSSES, labMaximumConsecutiveLosses, valMaximumConsecutiveLosses);
		add(TradingStatisticsMsg.MAXIMAL_CONSECUTIVE_PROFIT, labMaximalConsecutiveProfit, valMaximalConsecutiveProfit);
		add(TradingStatisticsMsg.MAXIMAL_CONSECUTIVE_LOSS, labMaximalConsecutiveLoss, valMaximalConsecutiveLoss);
		add(TradingStatisticsMsg.AVERAGE_CONSECUTIVE_WINS, labAverageConsecutiveWins, valAverageConsecutiveWins);
		add(TradingStatisticsMsg.AVERAGE_CONSECUTIVE_LOSSES, labAverageConsecutiveLosses, valAverageConsecutiveLosses);
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
		return messages.format(TradingStatisticsMsg.CONSECUTIVE_TRADES_TPL, value.getPnL(), value.getCount());
	}
	
	public void updateView(ITradingStatistics statistics) {
		valTotalNetProfit.setText(decimal(statistics.getTotalNetProfit()));
		valGrossProfit.setText(decimal(statistics.getGrossProfit()));
		valGrossLoss.setText(decimal(statistics.getGrossLoss()));
		valProfitFactor.setText(decimal(statistics.getProfitFactor()));
		valExpectedPayoff.setText(decimal(statistics.getExpectedPayoff()));
		valAbsoluteDrawdown.setText(decimal(statistics.getAbsoluteDrawdown()));
		valMaximalDrawdown.setText(decimal(statistics.getMaximalDrawdown()));
		valTotalTrades.setText(integer(statistics.getTotalTrades()));
		valShortPositions.setText(integer(statistics.getShortPositions()));
		valWinningShortPositions.setText(integer(statistics.getWinningShortPositions()));
		valLongPositions.setText(integer(statistics.getLongPositions()));
		valWinningLongPositions.setText(integer(statistics.getWinningLongPositions()));
		valProfitTrades.setText(integer(statistics.getProfitTrades()));
		valLossTrades.setText(integer(statistics.getLossTrades()));
		valLargestProfitTrade.setText(decimal(statistics.getLargestProfitTrade()));
		valLargestLossTrade.setText(decimal(statistics.getLargestLossTrade()));
		valAverageProfitTrade.setText(decimal(statistics.getAverageProfitTrade()));
		valAverageLossTrade.setText(decimal(statistics.getAverageLossTrade()));
		valMaximumConsecutiveWins.setText(consecutive_trades(statistics.getMaximumConsecutiveWins()));
		valMaximumConsecutiveLosses.setText(consecutive_trades(statistics.getMaximumConsecutiveLosses()));
		valMaximalConsecutiveProfit.setText(consecutive_trades(statistics.getMaximalConsecutiveProfit()));
		valMaximalConsecutiveLoss.setText(consecutive_trades(statistics.getMaximalConsecutiveLoss()));
		valAverageConsecutiveWins.setText(integer(statistics.getAverageConsecutiveWins()));
		valAverageConsecutiveLosses.setText(integer(statistics.getAverageConsecutiveLosses()));
	}

}
