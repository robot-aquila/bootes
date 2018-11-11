package ru.prolib.bootes.tsgr001a.robot;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;
import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.sh.ChooseContract;
import ru.prolib.bootes.tsgr001a.robot.sh.Init;
import ru.prolib.bootes.tsgr001a.robot.sh.State;
import ru.prolib.bootes.tsgr001a.robot.sh.WaitForSessionEnd;

public class TSGR001ARobotBuilder {
	private final AppServiceLocator serviceLocator;

	public TSGR001ARobotBuilder(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public SMStateMachine build() {
		State state = new State();
		SMStateMachine automat = new SMBuilder()
				.addState(new Init(serviceLocator, state), S_INIT)
				.addState(new ChooseContract(serviceLocator, state), S_CHOOSE_CONTRACT)
				.addState(new WaitForSessionEnd(serviceLocator, state), S_WAIT)
				
				.setInitialState(S_INIT)
				
				.addTrans(S_INIT, E_OK, S_CHOOSE_CONTRACT)
				.addFinal(S_INIT, E_ERROR)
				.addFinal(S_INIT, E_INTERRUPT)
				
				.addTrans(S_CHOOSE_CONTRACT, E_NEW_SESSION, S_CHOOSE_CONTRACT)
				.addTrans(S_CHOOSE_CONTRACT, E_OK,			S_WAIT)
				.addFinal(S_CHOOSE_CONTRACT, E_ERROR)
				.addFinal(S_CHOOSE_CONTRACT, E_INTERRUPT)
				
				.addTrans(S_WAIT, E_NEW_SESSION, S_CHOOSE_CONTRACT)
				.addFinal(S_WAIT, E_ERROR)
				.addFinal(S_WAIT, E_INTERRUPT)
				
				.build();
		automat.setDebug(true);
		automat.setId("TSGR001A");
		return automat;
	}
}
