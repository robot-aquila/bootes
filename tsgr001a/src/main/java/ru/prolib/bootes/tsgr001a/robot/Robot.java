package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.sm.SMStateMachine;

public class Robot {
	private final RobotState state;
	private final SMStateMachine automat;
	
	public Robot(RobotState state, SMStateMachine automat) {
		this.state = state;
		this.automat = automat;
	}
	
	public RobotState getState() {
		return state;
	}
	
	public SMStateMachine getAutomat() {
		return automat;
	}

}
