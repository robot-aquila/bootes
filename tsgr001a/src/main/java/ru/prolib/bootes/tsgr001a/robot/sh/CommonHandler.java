package ru.prolib.bootes.tsgr001a.robot.sh;

import java.time.Instant;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.sm.SMEnterAction;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputStub;
import ru.prolib.aquila.core.sm.SMStateHandler;
import ru.prolib.aquila.core.sm.SMTrigger;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerOnTimer;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.sm.OnInterruptAction;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public abstract class CommonHandler extends SMStateHandler implements
	SMEnterAction,
	OnInterruptAction.Handler
{
	public static final String E_ERROR = "ERROR";
	public static final String E_INTERRUPT = "INTERRUPT";
	
	protected final SMInput inInterrupt;
	protected final AppServiceLocator serviceLocator;
	protected final RobotState state;

	public CommonHandler(AppServiceLocator serviceLocator, RobotState state) {
		this.serviceLocator = serviceLocator;
		this.state = state;
		setEnterAction(this);
		registerExit(E_ERROR);
		registerExit(E_INTERRUPT);
		inInterrupt = registerInput(new OnInterruptAction(this));
	}
	
	@Override
	public SMExit onInterrupt(Object data) {
		return getExit(E_INTERRUPT);
	}
	
	/**
	 * Get input of interruption signal.
	 * <p>
	 * @return input
	 */
	public SMInput getInputOfInterruption() {
		return inInterrupt;
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		return null;
	}
	
	protected SMTrigger newTriggerOnEvent(EventType type, SMInput input) {
		return new SMTriggerOnEvent(type, input);
	}
	
	protected SMTrigger newExitOnEvent(EventType type, String exitID) {
		return newTriggerOnEvent(type, registerInput(new SMInputStub(getExit(exitID))));
	}
	
	protected SMTrigger newExitOnTimer(Scheduler scheduler, Instant time, SMInput input) {
		return new SMTriggerOnTimer(scheduler, time, input);
	}
	
	protected SMTrigger newExitOnTimer(Scheduler scheduler, Instant time, String exitID) {
		return newExitOnTimer(scheduler, time, registerInput(new SMInputStub(getExit(exitID))));
	}

}
