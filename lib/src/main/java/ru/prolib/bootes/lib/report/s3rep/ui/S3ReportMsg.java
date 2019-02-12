package ru.prolib.bootes.lib.report.s3rep.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class S3ReportMsg {
	static final String SECTION_ID = "S3Report";
	
	static {
		Messages.registerLoader(SECTION_ID, S3ReportMsg.class.getClassLoader());
		Messages.setDefaultMsgIDs(SECTION_ID, S3ReportMsg.class);
	}
	
	public static MsgID
		RECORD_ID,
		RECORD_TYPE,
		DATE,
		ENTRY_TIME,
		ENTRY_PRICE,
		QTY,
		TAKE_PROFIT,
		STOP_LOSS,
		BREAK_EVEN,
		EXIT_TIME,
		EXIT_PRICE,
		PROFIT_AND_LOSS;
	
}
