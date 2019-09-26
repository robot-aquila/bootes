package xx.mix.bootes.kinako.robot;

import ru.prolib.aquila.core.sm.SMBuilder;
import ru.prolib.aquila.core.sm.SMStateMachine;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.Robot;

public class KinakoRobotBuilder {
	public static final String S_WAIT_SIGNAL = "WAIT_SIGNAL";
	public static final String S_SET_INVOLVED_SYMBOLS = "SET_INVOLVED_SYMBOLS";
	public static final String S_SUBSCRIBE_SYMBOLS = "SUBSCRIBE_SYMBOLS";
	public static final String S_EXEC_SIGNAL = "EXEC_SIGNAL";
	public static final String S_UNSUBSCRIBE_SYMBOLS = "UNSUBSCRIBE_SYMBOLS";
	
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
				.addState(new KinakoSetInvolvedSymbols(data), S_SET_INVOLVED_SYMBOLS)
				.addState(new KinakoSubscribeSymbols(serviceLocator, data), S_SUBSCRIBE_SYMBOLS)
				.addState(new KinakoExecuteSignal(serviceLocator, kinakoServiceLocator, data), S_EXEC_SIGNAL)
				.addState(new KinakoUnsubscribeSymbols(serviceLocator, data), S_UNSUBSCRIBE_SYMBOLS)
				.setInitialState(S_WAIT_SIGNAL)
				
				.addTrans(S_WAIT_SIGNAL, KinakoWaitForSignal.E_SIGNAL_DETECTED, S_SET_INVOLVED_SYMBOLS)
				.addTrans(S_WAIT_SIGNAL, KinakoWaitForSignal.E_SIGNAL_REJECTED, S_WAIT_SIGNAL)
				.addFinal(S_WAIT_SIGNAL, KinakoWaitForSignal.E_ERROR)
				.addFinal(S_WAIT_SIGNAL, KinakoWaitForSignal.E_INTERRUPT)
				
				.addTrans(S_SET_INVOLVED_SYMBOLS, KinakoSetInvolvedSymbols.E_OK, S_SUBSCRIBE_SYMBOLS)
				.addFinal(S_SET_INVOLVED_SYMBOLS, KinakoSetInvolvedSymbols.E_ERROR)
				.addFinal(S_SET_INVOLVED_SYMBOLS, KinakoSetInvolvedSymbols.E_INTERRUPT)
				
				.addTrans(S_SUBSCRIBE_SYMBOLS, KinakoSubscribeSymbols.E_OK, S_EXEC_SIGNAL)
				.addTrans(S_SUBSCRIBE_SYMBOLS, KinakoSubscribeSymbols.E_NOT_FOUND, S_UNSUBSCRIBE_SYMBOLS)
				.addFinal(S_SUBSCRIBE_SYMBOLS, KinakoSubscribeSymbols.E_ERROR)
				.addFinal(S_SUBSCRIBE_SYMBOLS, KinakoSubscribeSymbols.E_INTERRUPT)
				
				.addTrans(S_EXEC_SIGNAL, KinakoExecuteSignal.E_OK, S_UNSUBSCRIBE_SYMBOLS)
				.addFinal(S_EXEC_SIGNAL, KinakoExecuteSignal.E_ERROR)
				.addFinal(S_EXEC_SIGNAL, KinakoExecuteSignal.E_INTERRUPT)
				
				.addTrans(S_UNSUBSCRIBE_SYMBOLS, KinakoUnsubscribeSymbols.E_OK, S_WAIT_SIGNAL)
				.addFinal(S_UNSUBSCRIBE_SYMBOLS, KinakoUnsubscribeSymbols.E_ERROR)
				.addFinal(S_UNSUBSCRIBE_SYMBOLS, KinakoUnsubscribeSymbols.E_INTERRUPT);
		
		SMStateMachine automat = builder.build();
		automat.setDebug(true);
		automat.setId("KINAKO");
		return new Robot<>(data, automat);
	}
	
}
