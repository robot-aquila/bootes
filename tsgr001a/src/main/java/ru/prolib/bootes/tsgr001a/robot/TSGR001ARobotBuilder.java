package ru.prolib.bootes.tsgr001a.robot;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;
import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.sh.ChooseContract;
import ru.prolib.bootes.tsgr001a.robot.sh.CleanSessionData;
import ru.prolib.bootes.tsgr001a.robot.sh.CommonActions;
import ru.prolib.bootes.tsgr001a.robot.sh.Init;
import ru.prolib.bootes.tsgr001a.robot.sh.InitSessionData;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForAccount;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForMarketSignal;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForMarketSignalResult;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForSessionEnd;

public class TSGR001ARobotBuilder {
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
				.addState(new WaitForMarketSignalResult(serviceLocator, state), S_WAIT_MARKET_SIGNAL_RESULT)
				.addState(new WaitForSessionEnd(serviceLocator, state, ca), S_WAIT_SESSION_END)
				.addState(new CleanSessionData(serviceLocator, state, ca), S_CLEAN_SESSION_DATA)
				
				.setInitialState(S_INIT)
				
				.addTrans(S_INIT, E_OK, S_WAIT_ACCOUNT)
				.addFinal(S_INIT, E_ERROR)
				.addFinal(S_INIT, E_INTERRUPT)
				
				.addTrans(S_WAIT_ACCOUNT, E_OK, S_CHOOSE_CONTRACT)
				.addFinal(S_WAIT_ACCOUNT, E_ERROR)
				.addFinal(S_WAIT_ACCOUNT, E_INTERRUPT)
				
				.addTrans(S_CHOOSE_CONTRACT, E_NEW_SESSION, S_CHOOSE_CONTRACT)
				.addTrans(S_CHOOSE_CONTRACT, E_OK,			S_INIT_SESSION_DATA)
				.addFinal(S_CHOOSE_CONTRACT, E_ERROR)
				.addFinal(S_CHOOSE_CONTRACT, E_INTERRUPT)
				
				.addTrans(S_INIT_SESSION_DATA, E_OK, S_WAIT_MARKET_SIGNAL)
				.addFinal(S_INIT_SESSION_DATA, E_ERROR)
				.addFinal(S_INIT_SESSION_DATA, E_INTERRUPT)
				
				.addTrans(S_WAIT_MARKET_SIGNAL, E_STOP_TRADING,	S_WAIT_SESSION_END)
				.addTrans(S_WAIT_MARKET_SIGNAL, E_BUY,			S_WAIT_MARKET_SIGNAL_RESULT)
				.addTrans(S_WAIT_MARKET_SIGNAL, E_SELL,			S_WAIT_MARKET_SIGNAL_RESULT)
				.addFinal(S_WAIT_MARKET_SIGNAL, E_ERROR)
				.addFinal(S_WAIT_MARKET_SIGNAL, E_INTERRUPT)
				
				.addTrans(S_WAIT_MARKET_SIGNAL_RESULT, E_STOP_TRADING,	S_WAIT_SESSION_END)
				.addTrans(S_WAIT_MARKET_SIGNAL_RESULT, E_TAKE_PROFIT,	S_WAIT_MARKET_SIGNAL)
				.addTrans(S_WAIT_MARKET_SIGNAL_RESULT, E_STOP_LOSS,		S_WAIT_MARKET_SIGNAL)
				.addFinal(S_WAIT_MARKET_SIGNAL_RESULT, E_ERROR)
				.addFinal(S_WAIT_MARKET_SIGNAL_RESULT, E_INTERRUPT)
				
				.addTrans(S_WAIT_SESSION_END, E_STOP_DATA_TRACKING, S_CLEAN_SESSION_DATA)
				.addFinal(S_WAIT_SESSION_END, E_ERROR)
				.addFinal(S_WAIT_SESSION_END, E_INTERRUPT)
				
				.addTrans(S_CLEAN_SESSION_DATA, E_OK, S_CHOOSE_CONTRACT)
				.addFinal(S_CLEAN_SESSION_DATA, E_ERROR)
				.addFinal(S_CLEAN_SESSION_DATA, E_INTERRUPT)
				
				.build();
		automat.setDebug(true);
		automat.setId("TSGR001A"); // TODO: custom ID
		return new Robot(state, automat);
	}
}
