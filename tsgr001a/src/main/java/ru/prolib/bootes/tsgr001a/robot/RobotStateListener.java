package ru.prolib.bootes.tsgr001a.robot;

public interface RobotStateListener {
	void robotStarted();
	void accountSelected();
	void contractSelected();
	void sessionDataAvailable();
	void positionParamsUpdated();
	void speculationOpened();
	void speculationClosed();
	void sessionDataCleanup();
	void robotStopped();
}
