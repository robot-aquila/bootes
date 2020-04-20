package ru.prolib.bootes.protos.sos;

import ru.prolib.aquila.core.BusinessEntities.OrderDefinitionProvider;
import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.Robot;
import ru.prolib.bootes.lib.robo.sh.BOOTESCleanup;
import ru.prolib.bootes.lib.robo.sh.BOOTESExecuteOrder;
import ru.prolib.bootes.lib.robo.sh.BOOTESWaitForAccount;
import ru.prolib.bootes.lib.robo.sh.BOOTESWaitForContract;
import ru.prolib.bootes.protos.PROTOSRobotState;

public class SOSRobotBuilder {
	public static final String S_INIT			= "INIT";
	public static final String S_WAIT_ACCOUNT	= "WAIT_ACCOUNT";
	public static final String S_WAIT_CONTRACT	= "WAIT_CONTRACT";
	public static final String S_NEXT_SIGNAL	= "NEXT_SIGNAL";
	public static final String S_EXEC_ORDER		= "EXEC_ORDER";
	public static final String S_CLEANUP		= "CLEANUP";

	private final AppServiceLocator serviceLocator;
	
	public SOSRobotBuilder(AppServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
	
	public Robot<PROTOSRobotState> build(String robot_id, OrderDefinitionProvider signal_provider) {
		PROTOSRobotState state = new PROTOSRobotState(robot_id);
		SMBuilder builder = new SMBuilder()
			.addState(new SOSInit(serviceLocator, state), S_INIT)
			.addState(new BOOTESWaitForAccount(serviceLocator, state), S_WAIT_ACCOUNT)
			.addState(new BOOTESWaitForContract(serviceLocator, state), S_WAIT_CONTRACT)
			.addState(new SOSWaitForSignal(serviceLocator, signal_provider), S_NEXT_SIGNAL)
			.addState(new BOOTESExecuteOrder(serviceLocator, state.getStateListener()), S_EXEC_ORDER)
			.addState(new BOOTESCleanup(serviceLocator, state), S_CLEANUP)
			.setInitialState(S_INIT)
			
			.addTrans(S_INIT, SOSInit.E_OK, S_WAIT_ACCOUNT)
			.addFinal(S_INIT, SOSInit.E_ERROR)
			.addFinal(S_INIT, SOSInit.E_INTERRUPT)
			
			.addTrans(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_OK, S_WAIT_CONTRACT)
			.addFinal(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_ERROR)
			.addFinal(S_WAIT_ACCOUNT, BOOTESWaitForAccount.E_INTERRUPT)
			
			.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_NEW_SESSION, S_WAIT_CONTRACT)
			.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_OK, S_NEXT_SIGNAL)
			.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_ERROR, S_CLEANUP)
			.addTrans(S_WAIT_CONTRACT, BOOTESWaitForContract.E_INTERRUPT, S_CLEANUP)
			
			.addTrans(S_NEXT_SIGNAL, SOSWaitForSignal.E_BUY, S_EXEC_ORDER)
			.addTrans(S_NEXT_SIGNAL, SOSWaitForSignal.E_SELL, S_EXEC_ORDER)
			.addTrans(S_NEXT_SIGNAL, SOSWaitForSignal.E_END, S_CLEANUP)
			.addTrans(S_NEXT_SIGNAL, SOSWaitForSignal.E_ERROR, S_CLEANUP)
			.addTrans(S_NEXT_SIGNAL, SOSWaitForSignal.E_INTERRUPT, S_CLEANUP)
			
			.addTrans(S_EXEC_ORDER, BOOTESExecuteOrder.E_EXEC_FULL, S_WAIT_CONTRACT)
			.addTrans(S_EXEC_ORDER, BOOTESExecuteOrder.E_EXEC_PART, S_WAIT_CONTRACT)
			.addTrans(S_EXEC_ORDER, BOOTESExecuteOrder.E_EXEC_NONE, S_WAIT_CONTRACT)
			.addTrans(S_EXEC_ORDER, BOOTESExecuteOrder.E_TIMEOUT, S_WAIT_CONTRACT)
			.addTrans(S_EXEC_ORDER, BOOTESExecuteOrder.E_ERROR, S_CLEANUP)
			.addTrans(S_EXEC_ORDER, BOOTESExecuteOrder.E_INTERRUPT, S_CLEANUP)
			
			.addFinal(S_CLEANUP, BOOTESCleanup.E_OK)
			.addFinal(S_CLEANUP, BOOTESCleanup.E_ERROR)
			.addFinal(S_CLEANUP, BOOTESCleanup.E_INTERRUPT)
			
			;
		
		return new Robot<>(state, builder.build());
	}
}
