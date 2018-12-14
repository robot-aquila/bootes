package ru.prolib.bootes.tsgr001a.robot;

public interface RobotStateListener {
	void robotStarted();
	void accountSelected();
	void contractSelected();
	void sessionDataAvailable();
	void limitsUpdated();
	void sessionDataCleanup();
	void robotStopped();
}
