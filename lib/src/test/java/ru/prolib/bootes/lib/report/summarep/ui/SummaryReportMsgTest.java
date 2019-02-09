package ru.prolib.bootes.lib.report.summarep.ui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.bootes.lib.report.summarep.ui.SummaryReportMsg;

public class SummaryReportMsgTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConstants() {
		assertEquals(new MsgID("SummaryReport", "SECTION_TITLE"), SummaryReportMsg.SECTION_TITLE);
		assertEquals(new MsgID("SummaryReport", "TOTAL_NET_PROFIT"), SummaryReportMsg.TOTAL_NET_PROFIT);
		assertEquals(new MsgID("SummaryReport", "GROSS_PROFIT"), SummaryReportMsg.GROSS_PROFIT);
		assertEquals(new MsgID("SummaryReport", "GROSS_LOSS"), SummaryReportMsg.GROSS_LOSS);
		assertEquals(new MsgID("SummaryReport", "PROFIT_FACTOR"), SummaryReportMsg.PROFIT_FACTOR);
		assertEquals(new MsgID("SummaryReport", "EXPECTED_PAYOFF"), SummaryReportMsg.EXPECTED_PAYOFF);
		assertEquals(new MsgID("SummaryReport", "ABSOLUTE_DRAWDOWN"), SummaryReportMsg.ABSOLUTE_DRAWDOWN);
		assertEquals(new MsgID("SummaryReport", "MAXIMAL_DRAWDOWN"), SummaryReportMsg.MAXIMAL_DRAWDOWN);
		assertEquals(new MsgID("SummaryReport", "TOTAL_TRADES"), SummaryReportMsg.TOTAL_TRADES);
		assertEquals(new MsgID("SummaryReport", "SHORT_POSITIONS"), SummaryReportMsg.SHORT_POSITIONS);
		assertEquals(new MsgID("SummaryReport", "WINNING_SHORT_POSITIONS"), SummaryReportMsg.WINNING_SHORT_POSITIONS);
		assertEquals(new MsgID("SummaryReport", "LONG_POSITIONS"), SummaryReportMsg.LONG_POSITIONS);
		assertEquals(new MsgID("SummaryReport", "WINNING_LONG_POSITIONS"), SummaryReportMsg.WINNING_LONG_POSITIONS);
		assertEquals(new MsgID("SummaryReport", "PROFIT_TRADES"), SummaryReportMsg.PROFIT_TRADES);
		assertEquals(new MsgID("SummaryReport", "LOSS_TRADES"), SummaryReportMsg.LOSS_TRADES);
		assertEquals(new MsgID("SummaryReport", "LARGEST_PROFIT_TRADE"), SummaryReportMsg.LARGEST_PROFIT_TRADE);
		assertEquals(new MsgID("SummaryReport", "LARGEST_LOSS_TRADE"), SummaryReportMsg.LARGEST_LOSS_TRADE);
		assertEquals(new MsgID("SummaryReport", "AVERAGE_PROFIT_TRADE"), SummaryReportMsg.AVERAGE_PROFIT_TRADE);
		assertEquals(new MsgID("SummaryReport", "AVERAGE_LOSS_TRADE"), SummaryReportMsg.AVERAGE_LOSS_TRADE);
		assertEquals(new MsgID("SummaryReport", "MAXIMUM_CONSECUTIVE_WINS"), SummaryReportMsg.MAXIMUM_CONSECUTIVE_WINS);
		assertEquals(new MsgID("SummaryReport", "MAXIMUM_CONSECUTIVE_LOSSES"), SummaryReportMsg.MAXIMUM_CONSECUTIVE_LOSSES);
		assertEquals(new MsgID("SummaryReport", "MAXIMAL_CONSECUTIVE_PROFIT"), SummaryReportMsg.MAXIMAL_CONSECUTIVE_PROFIT);
		assertEquals(new MsgID("SummaryReport", "MAXIMAL_CONSECUTIVE_LOSS"), SummaryReportMsg.MAXIMAL_CONSECUTIVE_LOSS);
		assertEquals(new MsgID("SummaryReport", "AVERAGE_CONSECUTIVE_WINS"), SummaryReportMsg.AVERAGE_CONSECUTIVE_WINS);
		assertEquals(new MsgID("SummaryReport", "AVERAGE_CONSECUTIVE_LOSSES"), SummaryReportMsg.AVERAGE_CONSECUTIVE_LOSSES);
		assertEquals(new MsgID("SummaryReport", "CONSECUTIVE_TRADES_TPL"), SummaryReportMsg.CONSECUTIVE_TRADES_TPL);
	}

}
