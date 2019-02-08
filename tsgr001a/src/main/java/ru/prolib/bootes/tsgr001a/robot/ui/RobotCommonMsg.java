package ru.prolib.bootes.tsgr001a.robot.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class RobotCommonMsg {
	static final String SECTION_ID = "RobotCommon";
	
	static {
		Messages.registerLoader(SECTION_ID, RobotCommonMsg.class.getClassLoader());
		Messages.setDefaultMsgIDs(SECTION_ID, RobotCommonMsg.class);
	}
	
	public static MsgID
		CHARTS,
		REPORTS,
		TEST_STRATEGY;
	
}
