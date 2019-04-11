package ru.prolib.bootes.lib.robo;

public interface RobotStateListener {

	void robotStarted();

	void accountSelected();

	void contractSelected();

	void sessionDataAvailable();

	void riskManagementUpdate();

	void sessionDataCleanup();

	void robotStopped();

}