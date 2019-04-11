package ru.prolib.bootes.lib.robo.s3.statereq;

import ru.prolib.bootes.lib.robo.s3.S3RobotStateListener;
import ru.prolib.bootes.lib.sm.statereq.IStateObservable;

public interface IS3StateObservable extends IStateObservable {
	S3RobotStateListener getStateListener();
}
