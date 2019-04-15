package ru.prolib.bootes.lib.robo;

import ru.prolib.aquila.core.sm.SMStateMachine;

public class Robot<T> {
	private final T state;
	private final SMStateMachine automat;
	
	public Robot(T state, SMStateMachine automat) {
		this.state = state;
		this.automat = automat;
	}
	
	public T getState() {
		return state;
	}
	
	public SMStateMachine getAutomat() {
		return automat;
	}

}
