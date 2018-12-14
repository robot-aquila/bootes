package ru.prolib.bootes.tsgr001a.robot.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class StrategyConfigMsg {
	static final String SECTION_ID = "StrategyConfig";
	
	static {
		Messages.registerLoader(SECTION_ID, StrategyConfigMsg.class.getClassLoader());
	}
	
	static MsgID newMsgID(String messageID) {
		return new MsgID(SECTION_ID, messageID);
	}
	
	public static final MsgID SECTION_TITLE = newMsgID("SECTION_TITLE");
	public static final MsgID ACCOUNT = newMsgID("ACCOUNT");
	public static final MsgID CONTRACT_NAME = newMsgID("CONTRACT_NAME");
	public static final MsgID CONTRACT_SYMBOL = newMsgID("CONTRACT_SYMBOL");
	public static final MsgID TRADING_PERIOD = newMsgID("TRADING_PERIOD");
	public static final MsgID TRADE_GOAL_CAP = newMsgID("TRADE_GOAL_CAP");
	public static final MsgID TRADE_LOSS_CAP = newMsgID("TRADE_LOSS_CAP");
	public static final MsgID EXP_DAILY_PRICE_MOVE = newMsgID("EXP_DAILY_PRICE_MOVE");
	public static final MsgID EXP_LOCAL_PRICE_MOVE = newMsgID("EXP_LOCAL_PRICE_MOVE");
	public static final MsgID SLIPPAGE = newMsgID("SLIPPAGE");
	public static final MsgID AVG_DAILY_PRICE_MOVE = newMsgID("AVG_DAILY_PRICE_MOVE");
	public static final MsgID AVG_LOCAL_PRICE_MOVE = newMsgID("AVG_LOCAL_PRICE_MOVE");
	public static final MsgID NUMBER_OF_CONTRACTS = newMsgID("NUMBER_OF_CONTRACTS");
	public static final MsgID TAKE_PROFIT = newMsgID("TAKE_PROFIT");
	public static final MsgID STOP_LOSS = newMsgID("STOP_LOSS");
	public static final MsgID NOT_AVAILABLE_SHORT = newMsgID("NOT_AVAILABLE_SHORT");
	public static final MsgID VAL_PERCENTS = newMsgID("VAL_PERCENTS");
	public static final MsgID VAL_PERCENTS_MONEY = newMsgID("VAL_PERCENTS_MONEY");
	public static final MsgID VAL_PERCENTS_POINTS = newMsgID("VAL_PERCENTS_POINTS");
	public static final MsgID VAL_POINTS = newMsgID("VAL_POINTS");
	public static final MsgID VAL_STEPS = newMsgID("VAL_STEPS");
	public static final MsgID VAL_STEPS_POINTS = newMsgID("VAL_STEPS_POINTS");
}
