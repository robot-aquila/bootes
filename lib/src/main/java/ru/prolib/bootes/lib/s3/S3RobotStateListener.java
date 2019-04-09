package ru.prolib.bootes.lib.s3;

public interface S3RobotStateListener {
	void robotStarted();
	void accountSelected();
	void contractSelected();
	void sessionDataAvailable();
	void riskManagementUpdate();
	void speculationOpened();
	void speculationUpdate();
	void speculationClosed();
	void sessionDataCleanup();
	void robotStopped();
}
