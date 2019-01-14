package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.sh.ChooseContract;
import ru.prolib.bootes.tsgr001a.robot.sh.CleanSessionData;
import ru.prolib.bootes.tsgr001a.robot.sh.CommonActions;
import ru.prolib.bootes.tsgr001a.robot.sh.Init;
import ru.prolib.bootes.tsgr001a.robot.sh.InitSessionData;
import ru.prolib.bootes.tsgr001a.robot.sh.ShowStats;
import ru.prolib.bootes.tsgr001a.robot.sh.SimCloseLongCtrl;
import ru.prolib.bootes.tsgr001a.robot.sh.SimClosePosition;
import ru.prolib.bootes.tsgr001a.robot.sh.SimCloseShortCtrl;
import ru.prolib.bootes.tsgr001a.robot.sh.SimOpenLongCtrl;
import ru.prolib.bootes.tsgr001a.robot.sh.SimOpenPosition;
import ru.prolib.bootes.tsgr001a.robot.sh.SimOpenShortCtrl;
import ru.prolib.bootes.tsgr001a.robot.sh.SimTrackLongCtrl;
import ru.prolib.bootes.tsgr001a.robot.sh.SimTrackPosition;
import ru.prolib.bootes.tsgr001a.robot.sh.SimTrackShortCtrl;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForAccount;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForMarketSignal;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForSessionEnd;

public class TSGR001ARobotBuilder {
	public static final String S_INIT = "INIT";
	public static final String S_WAIT_ACCOUNT = "WAIT_ACCOUNT";
	public static final String S_CHOOSE_CONTRACT = "CHOOSE_CONTR";
	public static final String S_INIT_SESSION_DATA = "INIT_SESSION_DATA";
	@Deprecated // Refactor
	public static final String S_WAIT_SESSION_END = "WAIT_SESSION_END";
	public static final String S_WAIT_MARKET_SIGNAL = "WAIT_MARKET_SIGNAL";
	public static final String S_OPEN_LONG = "OPEN_LONG";
	public static final String S_OPEN_SHORT = "OPEN_SHORT";
	public static final String S_TRACK_LONG = "TRACK_LONG";
	public static final String S_TRACK_SHORT = "TRACK_SHORT";
	public static final String S_CLOSE_LONG = "CLOSE_LONG";
	public static final String S_CLOSE_SHORT = "CLOSE_SHORT";
	public static final String S_CLEAN_SESSION_DATA = "CLEAN_SESSION_DATA";
	public static final String S_SHOW_STATS = "SHOW_STATS";
	
	private final AppServiceLocator serviceLocator;

	public TSGR001ARobotBuilder(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public Robot build(RobotStateListener stateListener) {
		CommonActions ca = new CommonActions();
		RobotState state = new RobotState(stateListener);
		SMStateMachine automat = new SMBuilder()
				.addState(new Init(serviceLocator, state), S_INIT)
				.addState(new WaitForAccount(serviceLocator, state), S_WAIT_ACCOUNT)
				.addState(new ChooseContract(serviceLocator, state), S_CHOOSE_CONTRACT)
				.addState(new InitSessionData(serviceLocator, state, ca), S_INIT_SESSION_DATA)
				.addState(new WaitForMarketSignal(serviceLocator, state, ca), S_WAIT_MARKET_SIGNAL)
				.addState(new SimOpenPosition(serviceLocator, state, new SimOpenLongCtrl()), S_OPEN_LONG)
				.addState(new SimOpenPosition(serviceLocator, state, new SimOpenShortCtrl()), S_OPEN_SHORT)
				.addState(new SimTrackPosition(serviceLocator, state, new SimTrackLongCtrl()), S_TRACK_LONG)
				.addState(new SimTrackPosition(serviceLocator, state, new SimTrackShortCtrl()), S_TRACK_SHORT)
				.addState(new SimClosePosition(serviceLocator, state, new SimCloseLongCtrl()), S_CLOSE_LONG)
				.addState(new SimClosePosition(serviceLocator, state, new SimCloseShortCtrl()), S_CLOSE_SHORT)
				.addState(new WaitForSessionEnd(serviceLocator, state, ca), S_WAIT_SESSION_END)
				.addState(new CleanSessionData(serviceLocator, state, ca), S_CLEAN_SESSION_DATA)
				.addState(new ShowStats(serviceLocator, state), S_SHOW_STATS)
				
				.setInitialState(S_INIT)
				
				.addTrans(S_INIT, Init.E_OK, S_WAIT_ACCOUNT)
				.addFinal(S_INIT, Init.E_ERROR)
				.addFinal(S_INIT, Init.E_INTERRUPT)
				
				.addTrans(S_WAIT_ACCOUNT, WaitForAccount.E_OK, S_CHOOSE_CONTRACT)
				.addFinal(S_WAIT_ACCOUNT, WaitForAccount.E_ERROR)
				.addFinal(S_WAIT_ACCOUNT, WaitForAccount.E_INTERRUPT)
				
				.addTrans(S_CHOOSE_CONTRACT, ChooseContract.E_NEW_SESSION,	S_CHOOSE_CONTRACT)
				.addTrans(S_CHOOSE_CONTRACT, ChooseContract.E_OK,			S_INIT_SESSION_DATA)
				.addTrans(S_CHOOSE_CONTRACT, ChooseContract.E_ERROR,		S_SHOW_STATS)
				.addTrans(S_CHOOSE_CONTRACT, ChooseContract.E_INTERRUPT,	S_SHOW_STATS)
				
				.addTrans(S_INIT_SESSION_DATA, InitSessionData.E_OK,		S_WAIT_MARKET_SIGNAL)
				.addTrans(S_INIT_SESSION_DATA, InitSessionData.E_ERROR,		S_SHOW_STATS)
				.addTrans(S_INIT_SESSION_DATA, InitSessionData.E_INTERRUPT,	S_SHOW_STATS)
				
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_STOP_TRADING,	S_WAIT_SESSION_END)
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_BUY,			S_OPEN_LONG)
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_SELL,			S_OPEN_SHORT)
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_ERROR,		S_SHOW_STATS)
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_INTERRUPT,	S_SHOW_STATS)
				
				.addTrans(S_OPEN_LONG, SimOpenPosition.E_OPENED,	S_TRACK_LONG)
				.addTrans(S_OPEN_LONG, SimOpenPosition.E_ERROR,		S_SHOW_STATS)
				.addTrans(S_OPEN_LONG, SimOpenPosition.E_INTERRUPT,	S_SHOW_STATS)
				
				.addTrans(S_OPEN_SHORT, SimOpenPosition.E_OPENED,		S_TRACK_SHORT)
				.addTrans(S_OPEN_SHORT, SimOpenPosition.E_ERROR,		S_SHOW_STATS)
				.addTrans(S_OPEN_SHORT, SimOpenPosition.E_INTERRUPT,	S_SHOW_STATS)
				
				.addTrans(S_TRACK_LONG, SimTrackPosition.E_CLOSE_POSITION,	S_CLOSE_LONG)
				.addTrans(S_TRACK_LONG, SimTrackPosition.E_ERROR,			S_SHOW_STATS)
				.addTrans(S_TRACK_LONG, SimTrackPosition.E_INTERRUPT,		S_SHOW_STATS)

				.addTrans(S_TRACK_SHORT, SimTrackPosition.E_CLOSE_POSITION, S_CLOSE_SHORT)
				.addTrans(S_TRACK_SHORT, SimTrackPosition.E_ERROR,			S_SHOW_STATS)
				.addTrans(S_TRACK_SHORT, SimTrackPosition.E_INTERRUPT,		S_SHOW_STATS)
				
				.addTrans(S_CLOSE_LONG, SimClosePosition.E_CLOSED,		S_WAIT_MARKET_SIGNAL)
				.addTrans(S_CLOSE_LONG, SimClosePosition.E_ERROR,		S_SHOW_STATS)
				.addTrans(S_CLOSE_LONG, SimClosePosition.E_INTERRUPT,	S_SHOW_STATS)
				
				.addTrans(S_CLOSE_SHORT, SimClosePosition.E_CLOSED,		S_WAIT_MARKET_SIGNAL)
				.addTrans(S_CLOSE_SHORT, SimClosePosition.E_ERROR,		S_SHOW_STATS)
				.addTrans(S_CLOSE_SHORT, SimClosePosition.E_INTERRUPT,	S_SHOW_STATS)
				
				.addTrans(S_WAIT_SESSION_END, WaitForSessionEnd.E_STOP_DATA_TRACKING,	S_CLEAN_SESSION_DATA)
				.addTrans(S_WAIT_SESSION_END, WaitForSessionEnd.E_ERROR,				S_SHOW_STATS)
				.addTrans(S_WAIT_SESSION_END, WaitForSessionEnd.E_INTERRUPT,			S_SHOW_STATS)
				
				.addTrans(S_CLEAN_SESSION_DATA, CleanSessionData.E_OK,			S_CHOOSE_CONTRACT)
				.addTrans(S_CLEAN_SESSION_DATA, CleanSessionData.E_ERROR,		S_SHOW_STATS)
				.addTrans(S_CLEAN_SESSION_DATA, CleanSessionData.E_INTERRUPT,	S_SHOW_STATS)
				
				.addFinal(S_SHOW_STATS, ShowStats.E_OK)
				.addFinal(S_SHOW_STATS, ShowStats.E_ERROR)
				.addFinal(S_SHOW_STATS, ShowStats.E_INTERRUPT)
				
				.build();
		automat.setDebug(true);
		automat.setId("TSGR001A"); // TODO: custom ID
		return new Robot(state, automat);
	}
}
