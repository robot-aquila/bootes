package ru.prolib.bootes.lib.robo.s3;

import ru.prolib.bootes.lib.robo.RobotStateListener;

public interface S3RobotStateListener extends RobotStateListener {
	void speculationOpened();
	void speculationUpdate();
	void speculationClosed();
}
