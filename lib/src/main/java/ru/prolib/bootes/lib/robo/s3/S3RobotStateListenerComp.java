package ru.prolib.bootes.lib.robo.s3;

import java.util.LinkedHashSet;
import java.util.Set;

import ru.prolib.bootes.lib.robo.RobotStateListener;

public class S3RobotStateListenerComp implements S3RobotStateListener {
	private Set<S3RobotStateListener> listeners;
	
	public S3RobotStateListenerComp(Set<S3RobotStateListener> listeners) {
		this.listeners = listeners;
	}
	
	public S3RobotStateListenerComp() {
		this(new LinkedHashSet<>());
	}
	
	public S3RobotStateListenerComp addListener(S3RobotStateListener listener) {
		listeners.add(listener);
		return this;
	}
	
	public S3RobotStateListenerComp removeListener(RobotStateListener listener) {
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
		for ( S3RobotStateListener listener : listeners ) {
			listener.speculationOpened();
		}
	}
	
	@Override
	public void speculationUpdate() {
		for ( S3RobotStateListener listener : listeners ) {
			listener.speculationUpdate();
		}
	}

	@Override
	public void speculationClosed() {
		for ( S3RobotStateListener listener : listeners ) {
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
