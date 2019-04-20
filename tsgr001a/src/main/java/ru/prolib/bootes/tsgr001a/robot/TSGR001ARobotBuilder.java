package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.s3.S3ClosePosition;
import ru.prolib.bootes.lib.robo.s3.S3OpenPosition;
import ru.prolib.bootes.lib.robo.sh.BOOTESWaitForContract;
import ru.prolib.bootes.lib.robo.sh.BOOTESWaitForSessionEnd;
import ru.prolib.bootes.lib.robo.sh.BOOTESCleanSessionData;
import ru.prolib.bootes.lib.robo.sh.BOOTESCleanup;
import ru.prolib.bootes.lib.robo.sh.BOOTESInitSessionData;
import ru.prolib.bootes.lib.robo.sh.BOOTESWaitForAccount;
import ru.prolib.bootes.tsgr001a.robot.sh.TSGR001AInit;
import ru.prolib.bootes.tsgr001a.robot.sh.SimClosePosition;
import ru.prolib.bootes.tsgr001a.robot.sh.SimOpenPosition;
import ru.prolib.bootes.tsgr001a.robot.sh.SimTrackPosition;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForMarketSignal;

public class TSGR001ARobotBuilder {
	public static final String S_INIT = "INIT";
	public static final String S_WAIT_ACCOUNT = "WAIT_ACCOUNT";
	public static final String S_WAIT_CONTRACT = "WAIT_CONTRACT";
	public static final String S_INIT_SESSION_DATA = "INIT_SESSION_DATA";
	public static final String S_WAIT_SESSION_END = "WAIT_SESSION_END";
	public static final String S_WAIT_MARKET_SIGNAL = "WAIT_MARKET_SIGNAL";
	public static final String S_OPEN_LONG = "OPEN_LONG";
	public static final String S_OPEN_SHORT = "OPEN_SHORT";
	public static final String S_TRACK_LONG = "TRACK_LONG";
	public static final String S_TRACK_SHORT = "TRACK_SHORT";
	public static final String S_CLOSE_LONG = "CLOSE_LONG";
	public static final String S_CLOSE_SHORT = "CLOSE_SHORT";
	public static final String S_CLEAN_SESSION_DATA = "CLEAN_SESSION_DATA";
	public static final String S_CLEANUP = "CLEANUP";
	
	private final AppServiceLocator serviceLocator;
	private final RoboServiceLocator roboServices;

	public TSGR001ARobotBuilder(AppServiceLocator serviceLocator, RoboServiceLocator roboServices) {
		this.serviceLocator = serviceLocator;
		this.roboServices = roboServices;
	}

	public Robot build(boolean no_orders) {
		RobotState state = new RobotState();
		SMBuilder builder = new SMBuilder()
				.addState(new TSGR001AInit(serviceLocator, roboServices, state), S_INIT)
				.addState(new BOOTESWaitForAccount(serviceLocator, state), S_WAIT_ACCOUNT)
				.addState(new BOOTESWaitForContract(serviceLocator, state), S_WAIT_CONTRACT)
				.addState(new BOOTESInitSessionData(state), S_INIT_SESSION_DATA)
				.addState(new WaitForMarketSignal(serviceLocator, roboServices, state), S_WAIT_MARKET_SIGNAL)
				.addState(new SimTrackPosition(serviceLocator, state), S_TRACK_LONG)
				.addState(new SimTrackPosition(serviceLocator, state), S_TRACK_SHORT)
				.addState(new BOOTESWaitForSessionEnd(serviceLocator, state), S_WAIT_SESSION_END)
				.addState(new BOOTESCleanSessionData(state), S_CLEAN_SESSION_DATA)
				.addState(new BOOTESCleanup(serviceLocator, state), S_CLEANUP)
				.setInitialState(S_INIT);
		
		if ( no_orders ) {
			builder.addState(new SimOpenPosition(serviceLocator, state), S_OPEN_LONG)
				.addState(new SimOpenPosition(serviceLocator, state), S_OPEN_SHORT)
				.addState(new SimClosePosition(serviceLocator, state), S_CLOSE_LONG)
				.addState(new SimClosePosition(serviceLocator, state), S_CLOSE_SHORT)
				
				.addTrans(S_OPEN_LONG, SimOpenPosition.E_OPENED,	S_TRACK_LONG)
				.addTrans(S_OPEN_LONG, SimOpenPosition.E_ERROR,		S_CLEANUP)
				.addTrans(S_OPEN_LONG, SimOpenPosition.E_INTERRUPT,	S_CLEANUP)
				
				.addTrans(S_OPEN_SHORT, SimOpenPosition.E_OPENED,		S_TRACK_SHORT)
				.addTrans(S_OPEN_SHORT, SimOpenPosition.E_ERROR,		S_CLEANUP)
				.addTrans(S_OPEN_SHORT, SimOpenPosition.E_INTERRUPT,	S_CLEANUP)
				
				.addTrans(S_CLOSE_LONG, SimClosePosition.E_CLOSED,		S_WAIT_MARKET_SIGNAL)
				.addTrans(S_CLOSE_LONG, SimClosePosition.E_ERROR,		S_CLEANUP)
				.addTrans(S_CLOSE_LONG, SimClosePosition.E_INTERRUPT,	S_CLEANUP)
				
				.addTrans(S_CLOSE_SHORT, SimClosePosition.E_CLOSED,		S_WAIT_MARKET_SIGNAL)
				.addTrans(S_CLOSE_SHORT, SimClosePosition.E_ERROR,		S_CLEANUP)
				.addTrans(S_CLOSE_SHORT, SimClosePosition.E_INTERRUPT,	S_CLEANUP);
			
		} else {
			builder.addState(new S3OpenPosition(serviceLocator, state), S_OPEN_LONG)
				.addState(new S3OpenPosition(serviceLocator, state), S_OPEN_SHORT)
				.addState(new S3ClosePosition(serviceLocator, state), S_CLOSE_LONG)
				.addState(new S3ClosePosition(serviceLocator, state), S_CLOSE_SHORT)
			
				.addTrans(S_OPEN_LONG, S3OpenPosition.E_OPEN,		S_TRACK_LONG)
				.addTrans(S_OPEN_LONG, S3OpenPosition.E_SKIPPED,	S_WAIT_MARKET_SIGNAL)
				.addTrans(S_OPEN_LONG, S3OpenPosition.E_ERROR,		S_CLEANUP)
				.addTrans(S_OPEN_LONG, S3OpenPosition.E_INTERRUPT,	S_CLEANUP)
				
				.addTrans(S_OPEN_SHORT, S3OpenPosition.E_OPEN,		S_TRACK_SHORT)
				.addTrans(S_OPEN_SHORT, S3OpenPosition.E_SKIPPED,	S_WAIT_MARKET_SIGNAL)
				.addTrans(S_OPEN_SHORT, S3OpenPosition.E_ERROR,		S_CLEANUP)
				.addTrans(S_OPEN_SHORT, S3OpenPosition.E_INTERRUPT,	S_CLEANUP)
				
				.addTrans(S_CLOSE_LONG, S3ClosePosition.E_CLOSED,		S_WAIT_MARKET_SIGNAL)
				.addTrans(S_CLOSE_LONG, S3ClosePosition.E_NEED_CLOSE,	S_CLOSE_LONG)
				.addTrans(S_CLOSE_LONG, S3ClosePosition.E_ERROR,		S_CLEANUP)
				.addTrans(S_CLOSE_LONG, S3ClosePosition.E_INTERRUPT,	S_CLEANUP)

				.addTrans(S_CLOSE_SHORT, S3ClosePosition.E_CLOSED,		S_WAIT_MARKET_SIGNAL)
				.addTrans(S_CLOSE_SHORT, S3ClosePosition.E_NEED_CLOSE,	S_CLOSE_SHORT)
				.addTrans(S_CLOSE_SHORT, S3ClosePosition.E_ERROR,		S_CLEANUP)
				.addTrans(S_CLOSE_SHORT, S3ClosePosition.E_INTERRUPT,	S_CLEANUP);
			
		}
		
		builder.addTrans(S_INIT, TSGR001AInit.E_OK, S_WAIT_ACCOUNT)
				.addFinal(S_INIT, TSGR001AInit.E_ERROR)
				.addFinal(S_INIT, TSGR001AInit.E_INTERRUPT)
				
				.addTrans(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_OK, S_WAIT_CONTRACT)
				.addFinal(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_ERROR)
				.addFinal(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_INTERRUPT)
				
				.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_NEW_SESSION,	S_WAIT_CONTRACT)
				.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_OK,			S_INIT_SESSION_DATA)
				.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_ERROR,		S_CLEANUP)
				.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_INTERRUPT,	S_CLEANUP)
				
				.addTrans(S_INIT_SESSION_DATA, BOOTESInitSessionData.E_OK,			S_WAIT_MARKET_SIGNAL)
				.addTrans(S_INIT_SESSION_DATA, BOOTESInitSessionData.E_ERROR,		S_CLEANUP)
				.addTrans(S_INIT_SESSION_DATA, BOOTESInitSessionData.E_INTERRUPT,	S_CLEANUP)
				
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_STOP_TRADING,	S_WAIT_SESSION_END)
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_BUY,			S_OPEN_LONG)
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_SELL,			S_OPEN_SHORT)
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_ERROR,		S_CLEANUP)
				.addTrans(S_WAIT_MARKET_SIGNAL, WaitForMarketSignal.E_INTERRUPT,	S_CLEANUP)
				
				.addTrans(S_WAIT_SESSION_END, BOOTESWaitForSessionEnd.E_SESSION_END,	S_CLEAN_SESSION_DATA)
				.addTrans(S_WAIT_SESSION_END, BOOTESWaitForSessionEnd.E_ERROR,			S_CLEANUP)
				.addTrans(S_WAIT_SESSION_END, BOOTESWaitForSessionEnd.E_INTERRUPT,		S_CLEANUP)
				
				.addTrans(S_CLEAN_SESSION_DATA, BOOTESCleanSessionData.E_OK,		S_WAIT_CONTRACT)
				.addTrans(S_CLEAN_SESSION_DATA, BOOTESCleanSessionData.E_ERROR,		S_CLEANUP)
				.addTrans(S_CLEAN_SESSION_DATA, BOOTESCleanSessionData.E_INTERRUPT,	S_CLEANUP)
				
				.addFinal(S_CLEANUP, BOOTESCleanup.E_OK)
				.addFinal(S_CLEANUP, BOOTESCleanup.E_ERROR)
				.addFinal(S_CLEANUP, BOOTESCleanup.E_INTERRUPT)
				
				.addTrans(S_TRACK_LONG, SimTrackPosition.E_CLOSE_POSITION,	S_CLOSE_LONG)
				.addTrans(S_TRACK_LONG, SimTrackPosition.E_ERROR,			S_CLEANUP)
				.addTrans(S_TRACK_LONG, SimTrackPosition.E_INTERRUPT,		S_CLEANUP)
		
				.addTrans(S_TRACK_SHORT, SimTrackPosition.E_CLOSE_POSITION, S_CLOSE_SHORT)
				.addTrans(S_TRACK_SHORT, SimTrackPosition.E_ERROR,			S_CLEANUP)
				.addTrans(S_TRACK_SHORT, SimTrackPosition.E_INTERRUPT,		S_CLEANUP);
				
		SMStateMachine automat = builder.build();
		automat.setDebug(true);
		automat.setId("TSGR001A"); // TODO: custom ID
		return new Robot(state, automat);
	}
}
