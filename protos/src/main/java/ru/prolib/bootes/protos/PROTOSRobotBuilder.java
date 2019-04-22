package ru.prolib.bootes.protos;

import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.Robot;
import ru.prolib.bootes.lib.robo.s3.S3ClosePosition;
import ru.prolib.bootes.lib.robo.s3.S3OpenPosition;
import ru.prolib.bootes.lib.robo.s3.S3TrackPosition;
import ru.prolib.bootes.lib.robo.sh.BOOTESCleanup;
import ru.prolib.bootes.lib.robo.sh.BOOTESWaitForAccount;
import ru.prolib.bootes.lib.robo.sh.BOOTESWaitForContract;
import ru.prolib.bootes.lib.robo.sh.BOOTESCleanSessionData;
import ru.prolib.bootes.lib.robo.sh.BOOTESInitSessionData;

public class PROTOSRobotBuilder {
	public static final String S_INIT			= "INIT";
	public static final String S_WAIT_ACCOUNT	= "WAIT_ACCOUNT";
	public static final String S_WAIT_CONTRACT	= "WAIT_CONTRACT";
	public static final String S_INIT_SESSION	= "INIT_SESSION";
	public static final String S_WAIT_SIGNAL	= "WAIT_SIGNAL";
	public static final String S_OPEN_LONG		= "OPEN_LONG";
	public static final String S_OPEN_SHORT		= "OPEN_SHORT";
	public static final String S_TRACK_LONG		= "TRACK_LONG";
	public static final String S_TRACK_SHORT	= "TRACK_SHORT";
	public static final String S_CLOSE_LONG		= "CLOSE_LONG";
	public static final String S_CLOSE_SHORT	= "CLOSE_SHORT";
	public static final String S_CLEAN_SESSION	= "CLEAN_SESSION";
	public static final String S_CLEANUP		= "CLEANUP";

	private final AppServiceLocator serviceLocator;
	
	public PROTOSRobotBuilder(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
	
	public Robot<PROTOSRobotState> build() {
		PROTOSRobotState state = new PROTOSRobotState();
		SMBuilder builder = new SMBuilder()
			.addState(new PROTOSInit(serviceLocator, state), S_INIT)
			.addState(new BOOTESWaitForAccount(serviceLocator, state), S_WAIT_ACCOUNT)
			.addState(new BOOTESWaitForContract(serviceLocator, state), S_WAIT_CONTRACT)
			.addState(new BOOTESInitSessionData(state), S_INIT_SESSION)
			.addState(new PROTOSWaitForMarketSignal(serviceLocator, state), S_WAIT_SIGNAL)
			.addState(new S3OpenPosition(serviceLocator, state), S_OPEN_LONG)
			.addState(new S3TrackPosition(serviceLocator, state), S_TRACK_LONG)
			.addState(new S3ClosePosition(serviceLocator, state), S_CLOSE_LONG)
			.addState(new S3OpenPosition(serviceLocator, state), S_OPEN_SHORT)
			.addState(new S3TrackPosition(serviceLocator, state), S_TRACK_SHORT)
			.addState(new S3ClosePosition(serviceLocator, state), S_CLOSE_SHORT)
			.addState(new BOOTESCleanSessionData(state), S_CLEAN_SESSION)
			.addState(new BOOTESCleanup(serviceLocator, state), S_CLEANUP)
			.setInitialState(S_INIT)
		
			.addTrans(S_INIT, PROTOSInit.E_OK, S_WAIT_ACCOUNT)
			.addFinal(S_INIT, PROTOSInit.E_ERROR)
			.addFinal(S_INIT, PROTOSInit.E_INTERRUPT)
			
			.addTrans(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_OK, S_WAIT_CONTRACT)
			.addFinal(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_ERROR)
			.addFinal(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_INTERRUPT)
			
			.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_NEW_SESSION, S_WAIT_CONTRACT)
			.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_OK, S_INIT_SESSION)
			.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_ERROR, S_CLEANUP)
			.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_INTERRUPT, S_CLEANUP)
		
			.addTrans(S_INIT_SESSION, BOOTESInitSessionData.E_OK, S_WAIT_SIGNAL)
			.addTrans(S_INIT_SESSION, BOOTESInitSessionData.E_ERROR, S_CLEANUP)
			.addTrans(S_INIT_SESSION, BOOTESInitSessionData.E_INTERRUPT, S_CLEANUP)
			
			.addTrans(S_WAIT_SIGNAL, PROTOSWaitForMarketSignal.E_SESSION_END, S_CLEAN_SESSION)
			.addTrans(S_WAIT_SIGNAL, PROTOSWaitForMarketSignal.E_TRADING_END, S_WAIT_SIGNAL)
			.addTrans(S_WAIT_SIGNAL, PROTOSWaitForMarketSignal.E_BUY, S_OPEN_LONG)
			.addTrans(S_WAIT_SIGNAL, PROTOSWaitForMarketSignal.E_SELL, S_OPEN_SHORT)
			.addTrans(S_WAIT_SIGNAL, PROTOSWaitForMarketSignal.E_ERROR, S_CLEANUP)
			.addTrans(S_WAIT_SIGNAL, PROTOSWaitForMarketSignal.E_INTERRUPT, S_CLEANUP)
			
			.addTrans(S_OPEN_LONG, S3OpenPosition.E_OPEN,		S_TRACK_LONG)
			.addTrans(S_OPEN_LONG, S3OpenPosition.E_SKIPPED,	S_WAIT_SIGNAL)
			.addTrans(S_OPEN_LONG, S3OpenPosition.E_ERROR,		S_CLEANUP)
			.addTrans(S_OPEN_LONG, S3OpenPosition.E_INTERRUPT,	S_CLEANUP)
			
			.addTrans(S_TRACK_LONG, S3TrackPosition.E_CLOSE_POSITION,	S_CLOSE_LONG)
			.addTrans(S_TRACK_LONG, S3TrackPosition.E_ERROR,			S_CLEANUP)
			.addTrans(S_TRACK_LONG, S3TrackPosition.E_INTERRUPT,		S_CLEANUP)
			
			.addTrans(S_CLOSE_LONG, S3ClosePosition.E_CLOSED,		S_WAIT_SIGNAL)
			.addTrans(S_CLOSE_LONG, S3ClosePosition.E_NEED_CLOSE,	S_CLOSE_LONG)
			.addTrans(S_CLOSE_LONG, S3ClosePosition.E_ERROR,		S_CLEANUP)
			.addTrans(S_CLOSE_LONG, S3ClosePosition.E_INTERRUPT,	S_CLEANUP)
			
			.addTrans(S_OPEN_SHORT, S3OpenPosition.E_OPEN,		S_TRACK_LONG)
			.addTrans(S_OPEN_SHORT, S3OpenPosition.E_SKIPPED,	S_WAIT_SIGNAL)
			.addTrans(S_OPEN_SHORT, S3OpenPosition.E_ERROR,		S_CLEANUP)
			.addTrans(S_OPEN_SHORT, S3OpenPosition.E_INTERRUPT,	S_CLEANUP)
			
			.addTrans(S_TRACK_SHORT, S3TrackPosition.E_CLOSE_POSITION,	S_CLOSE_SHORT)
			.addTrans(S_TRACK_SHORT, S3TrackPosition.E_ERROR,			S_CLEANUP)
			.addTrans(S_TRACK_SHORT, S3TrackPosition.E_INTERRUPT,		S_CLEANUP)
			
			.addTrans(S_CLOSE_SHORT, S3ClosePosition.E_CLOSED,		S_WAIT_SIGNAL)
			.addTrans(S_CLOSE_SHORT, S3ClosePosition.E_NEED_CLOSE,	S_CLOSE_SHORT)
			.addTrans(S_CLOSE_SHORT, S3ClosePosition.E_ERROR,		S_CLEANUP)
			.addTrans(S_CLOSE_SHORT, S3ClosePosition.E_INTERRUPT,	S_CLEANUP)
			
			.addTrans(S_CLEAN_SESSION, BOOTESCleanSessionData.E_OK,			S_WAIT_CONTRACT)
			.addTrans(S_CLEAN_SESSION, BOOTESCleanSessionData.E_ERROR,		S_CLEANUP)
			.addTrans(S_CLEAN_SESSION, BOOTESCleanSessionData.E_INTERRUPT,	S_CLEANUP)
			
			.addFinal(S_CLEANUP, BOOTESCleanup.E_OK)
			.addFinal(S_CLEANUP, BOOTESCleanup.E_ERROR)
			.addFinal(S_CLEANUP, BOOTESCleanup.E_INTERRUPT);
		
		return new Robot<>(state, builder.build());
	}
	
}
