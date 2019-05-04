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
	
	public synchronized S3RobotStateListenerComp
		addListener(S3RobotStateListener listener)
	{
		listeners.add(listener);
		return this;
	}
	
	public synchronized S3RobotStateListenerComp
		removeListener(RobotStateListener listener)
	{
		listeners.remove(listener);
		return this;
	}
	
	private synchronized Set<S3RobotStateListener> _getListeners() {
		return new LinkedHashSet<>(listeners);
	}

	@Override
	public void robotStarted() {
		for ( RobotStateListener listener : _getListeners() ) {
			listener.robotStarted();
		}
	}

	@Override
	public void accountSelected() {
		for ( RobotStateListener listener : _getListeners() ) {
			listener.accountSelected();
		}
	}

	@Override
	public void contractSelected() {
		for ( RobotStateListener listener : _getListeners() ) {
			listener.contractSelected();
		}
	}

	@Override
	public void sessionDataAvailable() {
		for ( RobotStateListener listener : _getListeners() ) {
			listener.sessionDataAvailable();
		}
	}

	@Override
	public void riskManagementUpdate() {
		for ( RobotStateListener listener : _getListeners() ) {
			listener.riskManagementUpdate();
		}
	}

	@Override
	public void speculationOpened() {
		for ( S3RobotStateListener listener : _getListeners() ) {
			listener.speculationOpened();
		}
	}
	
	@Override
	public void speculationUpdate() {
		for ( S3RobotStateListener listener : _getListeners() ) {
			listener.speculationUpdate();
		}
	}

	@Override
	public void speculationClosed() {
		for ( S3RobotStateListener listener : _getListeners() ) {
			listener.speculationClosed();
		}
	}

	@Override
	public void sessionDataCleanup() {
		for ( RobotStateListener listener : _getListeners() ) {
			listener.sessionDataCleanup();
		}
	}

	@Override
	public void robotStopped() {
		for ( RobotStateListener listener : _getListeners() ) {
			listener.robotStopped();
		}
	}

}
