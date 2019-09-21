package xx.mix.bootes.kinako.robot;

import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.Robot;

public class KinakoRobotBuilder {
	public static final String S_WAIT_SIGNAL = "WAIT_SIGNAL";
	public static final String S_EXEC_SIGNAL = "EXEC_SIGNAL";
	
	private final AppServiceLocator serviceLocator;
	private final KinakoRobotServiceLocator kinakoServiceLocator;
	
	public KinakoRobotBuilder(
			AppServiceLocator service_locator,
			KinakoRobotServiceLocator kinako_service_locator
		)
	{
		this.serviceLocator = service_locator;
		this.kinakoServiceLocator = kinako_service_locator;
	}

	public Robot<KinakoRobotData> build() {
		KinakoRobotData data = new KinakoRobotData();
		SMBuilder builder = new SMBuilder()
				.addState(new KinakoWaitForSignal(kinakoServiceLocator, data), S_WAIT_SIGNAL)
				.addState(new KinakoExecuteSignal(serviceLocator, kinakoServiceLocator, data), S_EXEC_SIGNAL)
				.setInitialState(S_WAIT_SIGNAL)
				
				.addTrans(S_WAIT_SIGNAL, KinakoWaitForSignal.E_SIGNAL_DETECTED, S_EXEC_SIGNAL)
				.addTrans(S_WAIT_SIGNAL, KinakoWaitForSignal.E_SIGNAL_REJECTED, S_WAIT_SIGNAL)
				.addFinal(S_WAIT_SIGNAL, KinakoWaitForSignal.E_ERROR)
				.addFinal(S_WAIT_SIGNAL, KinakoWaitForSignal.E_INTERRUPT)
				
				.addTrans(S_EXEC_SIGNAL, KinakoExecuteSignal.E_OK, S_WAIT_SIGNAL)
				.addFinal(S_EXEC_SIGNAL, KinakoWaitForSignal.E_ERROR)
				.addFinal(S_EXEC_SIGNAL, KinakoWaitForSignal.E_INTERRUPT);
		
		SMStateMachine automat = builder.build();
		automat.setDebug(true);
		automat.setId("KINAKO");
		return new Robot<>(data, automat);
	}
	
}
