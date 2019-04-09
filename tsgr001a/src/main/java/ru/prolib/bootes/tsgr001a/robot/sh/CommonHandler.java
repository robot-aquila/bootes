package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMStateHandlerEx;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public abstract class CommonHandler extends SMStateHandlerEx {
	protected final AppServiceLocator serviceLocator;
	protected final RobotState state;

	public CommonHandler(AppServiceLocator serviceLocator, RobotState state) {
		super();
		this.serviceLocator = serviceLocator;
		this.state = state;
	}
	
	/**
	 * Get input of interruption signal.
	 * <p>
	 * @return input
	 */
	public SMInput getInputOfInterruption() {
		return getInterrupt();
	}

}
