package ru.prolib.bootes.tsgr001a.robot;

import java.util.LinkedHashSet;
import java.util.Set;

public class RobotStateListenerComp implements RobotStateListener {
	private Set<RobotStateListener> listeners;
	
	public RobotStateListenerComp(Set<RobotStateListener> listeners) {
		this.listeners = listeners;
	}
	
	public RobotStateListenerComp() {
		this(new LinkedHashSet<>());
	}
	
	public RobotStateListenerComp addListener(RobotStateListener listener) {
		listeners.add(listener);
		return this;
	}
	
	public RobotStateListenerComp removeListener(RobotStateListener listener) {
		listeners.remove(listener);
		return this;
	}

	@Override
	public void robotStarted() {
		for ( RobotStateListener listener : listeners ) {
			listener.robotStarted();
		}
	}

	@Override
	public void accountSelected() {
		for ( RobotStateListener listener : listeners ) {
			listener.accountSelected();
		}
	}

	@Override
	public void contractSelected() {
		for ( RobotStateListener listener : listeners ) {
			listener.contractSelected();
		}
	}

	@Override
	public void sessionDataAvailable() {
		for ( RobotStateListener listener : listeners ) {
			listener.sessionDataAvailable();
		}
	}

	@Override
	public void riskManagementUpdate() {
		for ( RobotStateListener listener : listeners ) {
			listener.riskManagementUpdate();
		}
	}

	@Override
	public void speculationOpened() {
		for ( RobotStateListener listener : listeners ) {
			listener.speculationOpened();
		}
	}

	@Override
	public void speculationClosed() {
		for ( RobotStateListener listener : listeners ) {
			listener.speculationClosed();
		}
	}

	@Override
	public void sessionDataCleanup() {
		for ( RobotStateListener listener : listeners ) {
			listener.sessionDataCleanup();
		}
	}

	@Override
	public void robotStopped() {
		for ( RobotStateListener listener : listeners ) {
			listener.robotStopped();
		}
	}

}
