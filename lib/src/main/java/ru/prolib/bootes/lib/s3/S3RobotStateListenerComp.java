package ru.prolib.bootes.lib.s3;

import java.util.LinkedHashSet;
import java.util.Set;

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
	
	public S3RobotStateListenerComp removeListener(S3RobotStateListener listener) {
		listeners.remove(listener);
		return this;
	}

	@Override
	public void robotStarted() {
		for ( S3RobotStateListener listener : listeners ) {
			listener.robotStarted();
		}
	}

	@Override
	public void accountSelected() {
		for ( S3RobotStateListener listener : listeners ) {
			listener.accountSelected();
		}
	}

	@Override
	public void contractSelected() {
		for ( S3RobotStateListener listener : listeners ) {
			listener.contractSelected();
		}
	}

	@Override
	public void sessionDataAvailable() {
		for ( S3RobotStateListener listener : listeners ) {
			listener.sessionDataAvailable();
		}
	}

	@Override
	public void riskManagementUpdate() {
		for ( S3RobotStateListener listener : listeners ) {
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
		for ( S3RobotStateListener listener : listeners ) {
			listener.sessionDataCleanup();
		}
	}

	@Override
	public void robotStopped() {
		for ( S3RobotStateListener listener : listeners ) {
			listener.robotStopped();
		}
	}

}
