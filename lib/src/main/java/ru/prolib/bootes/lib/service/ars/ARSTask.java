package ru.prolib.bootes.lib.service.ars;

import java.util.concurrent.TimeoutException;

public interface ARSTask {
	String getTaskID();
	void cancel();
	ARSTaskState getCurrentState();
	Throwable getThrownException();
	ARSTaskState waitForStateChange() throws InterruptedException;
	ARSTaskState waitForStateChange(long millis) throws InterruptedException, TimeoutException;
	ARSTaskState waitForCompletion() throws InterruptedException;
	ARSTaskState waitForCompletion(long millis) throws InterruptedException, TimeoutException;
}
