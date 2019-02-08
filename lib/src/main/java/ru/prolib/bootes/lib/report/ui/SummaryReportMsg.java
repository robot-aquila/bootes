package ru.prolib.bootes.lib.report.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class SummaryReportMsg {
	static final String SECTION_ID = "SummaryReport";
	
	static {
		Messages.registerLoader(SECTION_ID, SummaryReportMsg.class.getClassLoader());
		Messages.setDefaultMsgIDs(SECTION_ID, SummaryReportMsg.class);
	}
	
	public static MsgID
		SECTION_TITLE,
		TOTAL_NET_PROFIT,
		GROSS_PROFIT,
		GROSS_LOSS,
		PROFIT_FACTOR,
		EXPECTED_PAYOFF,
		ABSOLUTE_DRAWDOWN,
		MAXIMAL_DRAWDOWN,
		TOTAL_TRADES,
		SHORT_POSITIONS,
		WINNING_SHORT_POSITIONS,
		LONG_POSITIONS,
		WINNING_LONG_POSITIONS,
		PROFIT_TRADES,
		LOSS_TRADES,
		LARGEST_PROFIT_TRADE,
		LARGEST_LOSS_TRADE,
		AVERAGE_PROFIT_TRADE,
		AVERAGE_LOSS_TRADE,
		MAXIMUM_CONSECUTIVE_WINS,
		MAXIMUM_CONSECUTIVE_LOSSES,
		MAXIMAL_CONSECUTIVE_PROFIT,
		MAXIMAL_CONSECUTIVE_LOSS,
		AVERAGE_CONSECUTIVE_WINS,
		AVERAGE_CONSECUTIVE_LOSSES,
		CONSECUTIVE_TRADES_TPL;

}
