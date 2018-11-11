package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.sm.SMEnterAction;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputStub;
import ru.prolib.aquila.core.sm.SMStateHandler;
import ru.prolib.aquila.core.sm.SMTrigger;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public abstract class CommonHandler extends SMStateHandler implements SMEnterAction {
	protected final SMInput inInterrupt;
	protected final AppServiceLocator serviceLocator;
	protected final State state;

	public CommonHandler(AppServiceLocator serviceLocator, State state) {
		this.serviceLocator = serviceLocator;
		this.state = state;
		setEnterAction(this);
		registerExit(E_ERROR);
		registerExit(E_INTERRUPT);
		inInterrupt = registerInput(new SMInputStub(getExit(E_INTERRUPT)));
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
	
	protected SMTrigger newExitOnEvent(EventType type, String exitID) {
		return new SMTriggerOnEvent(type, registerInput(new SMInputStub(getExit(exitID))));
	}

}
