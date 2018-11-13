package ru.prolib.bootes.tsgr001a.robot;

public interface RobotStateListener {
	void robotStarted();
	void contractSelected();
	void sessionDataAvailable();
	void sessionDataCleanup();
	void robotStopped();
}
