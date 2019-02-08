package ru.prolib.bootes.lib.report.ui.swing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.text.MsgID;

public class TradingStatisticsMsgTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConstants() {
		assertEquals(new MsgID("TradingStatistics", "SECTION_TITLE"), TradingStatisticsMsg.SECTION_TITLE);
		assertEquals(new MsgID("TradingStatistics", "TOTAL_NET_PROFIT"), TradingStatisticsMsg.TOTAL_NET_PROFIT);
		assertEquals(new MsgID("TradingStatistics", "GROSS_PROFIT"), TradingStatisticsMsg.GROSS_PROFIT);
		assertEquals(new MsgID("TradingStatistics", "GROSS_LOSS"), TradingStatisticsMsg.GROSS_LOSS);
		assertEquals(new MsgID("TradingStatistics", "PROFIT_FACTOR"), TradingStatisticsMsg.PROFIT_FACTOR);
		assertEquals(new MsgID("TradingStatistics", "EXPECTED_PAYOFF"), TradingStatisticsMsg.EXPECTED_PAYOFF);
		assertEquals(new MsgID("TradingStatistics", "ABSOLUTE_DRAWDOWN"), TradingStatisticsMsg.ABSOLUTE_DRAWDOWN);
		assertEquals(new MsgID("TradingStatistics", "MAXIMAL_DRAWDOWN"), TradingStatisticsMsg.MAXIMAL_DRAWDOWN);
		assertEquals(new MsgID("TradingStatistics", "TOTAL_TRADES"), TradingStatisticsMsg.TOTAL_TRADES);
		assertEquals(new MsgID("TradingStatistics", "SHORT_POSITIONS"), TradingStatisticsMsg.SHORT_POSITIONS);
		assertEquals(new MsgID("TradingStatistics", "WINNING_SHORT_POSITIONS"), TradingStatisticsMsg.WINNING_SHORT_POSITIONS);
		assertEquals(new MsgID("TradingStatistics", "LONG_POSITIONS"), TradingStatisticsMsg.LONG_POSITIONS);
		assertEquals(new MsgID("TradingStatistics", "WINNING_LONG_POSITIONS"), TradingStatisticsMsg.WINNING_LONG_POSITIONS);
		assertEquals(new MsgID("TradingStatistics", "PROFIT_TRADES"), TradingStatisticsMsg.PROFIT_TRADES);
		assertEquals(new MsgID("TradingStatistics", "LOSS_TRADES"), TradingStatisticsMsg.LOSS_TRADES);
		assertEquals(new MsgID("TradingStatistics", "LARGEST_PROFIT_TRADE"), TradingStatisticsMsg.LARGEST_PROFIT_TRADE);
		assertEquals(new MsgID("TradingStatistics", "LARGEST_LOSS_TRADE"), TradingStatisticsMsg.LARGEST_LOSS_TRADE);
		assertEquals(new MsgID("TradingStatistics", "AVERAGE_PROFIT_TRADE"), TradingStatisticsMsg.AVERAGE_PROFIT_TRADE);
		assertEquals(new MsgID("TradingStatistics", "AVERAGE_LOSS_TRADE"), TradingStatisticsMsg.AVERAGE_LOSS_TRADE);
		assertEquals(new MsgID("TradingStatistics", "MAXIMUM_CONSECUTIVE_WINS"), TradingStatisticsMsg.MAXIMUM_CONSECUTIVE_WINS);
		assertEquals(new MsgID("TradingStatistics", "MAXIMUM_CONSECUTIVE_LOSSES"), TradingStatisticsMsg.MAXIMUM_CONSECUTIVE_LOSSES);
		assertEquals(new MsgID("TradingStatistics", "MAXIMAL_CONSECUTIVE_PROFIT"), TradingStatisticsMsg.MAXIMAL_CONSECUTIVE_PROFIT);
		assertEquals(new MsgID("TradingStatistics", "MAXIMAL_CONSECUTIVE_LOSS"), TradingStatisticsMsg.MAXIMAL_CONSECUTIVE_LOSS);
		assertEquals(new MsgID("TradingStatistics", "AVERAGE_CONSECUTIVE_WINS"), TradingStatisticsMsg.AVERAGE_CONSECUTIVE_WINS);
		assertEquals(new MsgID("TradingStatistics", "AVERAGE_CONSECUTIVE_LOSSES"), TradingStatisticsMsg.AVERAGE_CONSECUTIVE_LOSSES);
		assertEquals(new MsgID("TradingStatistics", "CONSECUTIVE_TRADES_TPL"), TradingStatisticsMsg.CONSECUTIVE_TRADES_TPL);
	}

}
