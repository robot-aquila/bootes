package ru.prolib.bootes.tsgr001a.robot;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;
import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.sh.ChooseContract;
import ru.prolib.bootes.tsgr001a.robot.sh.CommonActions;
import ru.prolib.bootes.tsgr001a.robot.sh.Init;
import ru.prolib.bootes.tsgr001a.robot.sh.InitSessionData;
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
				.addState(new ChooseContract(serviceLocator, state), S_CHOOSE_CONTRACT)
				.addState(new InitSessionData(serviceLocator, state, ca), S_INIT_SESSION_DATA)
				.addState(new WaitForSessionEnd(serviceLocator, state), S_WAIT_SESSION_END)
				
				.setInitialState(S_INIT)
				
				.addTrans(S_INIT, E_OK, S_CHOOSE_CONTRACT)
				.addFinal(S_INIT, E_ERROR)
				.addFinal(S_INIT, E_INTERRUPT)
				
				.addTrans(S_CHOOSE_CONTRACT, E_NEW_SESSION, S_CHOOSE_CONTRACT)
				.addTrans(S_CHOOSE_CONTRACT, E_OK,			S_INIT_SESSION_DATA)
				.addFinal(S_CHOOSE_CONTRACT, E_ERROR)
				.addFinal(S_CHOOSE_CONTRACT, E_INTERRUPT)
				
				.addTrans(S_INIT_SESSION_DATA, E_OK, S_WAIT_SESSION_END)
				.addFinal(S_INIT_SESSION_DATA, E_ERROR)
				.addFinal(S_INIT_SESSION_DATA, E_INTERRUPT)
				
				.addTrans(S_WAIT_SESSION_END, E_STOP_TRADING, S_CHOOSE_CONTRACT)
				.addFinal(S_WAIT_SESSION_END, E_ERROR)
				.addFinal(S_WAIT_SESSION_END, E_INTERRUPT)
				
				.build();
		automat.setDebug(true);
		automat.setId("TSGR001A"); // TODO: custom ID
		return new Robot(state, automat);
	}
}
