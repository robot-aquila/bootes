package ru.prolib.bootes.lib.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class BOOTESCommonMsg {
	static final String SECTION_ID = "BootesCommon";
	
	static {
		Messages.registerLoader(SECTION_ID, BOOTESCommonMsg.class.getClassLoader());
		Messages.setDefaultMsgIDs(SECTION_ID, BOOTESCommonMsg.class);
	}
	
	public static MsgID
		CHARTS,
		REPORTS,
		TEST_STRATEGY,
		REPORT_ALL_TRADES,
		REPORT_SHORT_DURATION_TRADES,
		REPORT_CROSS_MIDCLEARING_TRADES,
		EQUITY_CURVE;
	
}
