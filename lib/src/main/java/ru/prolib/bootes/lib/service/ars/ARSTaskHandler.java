package ru.prolib.bootes.lib.service.ars;

import java.util.concurrent.TimeoutException;

public class ARSTaskHandler implements ARSTask, Runnable {
	private final String taskID;
	private final ARSAction action;
	private ARSTaskState state;
	private Throwable thrown;
	
	public ARSTaskHandler(ARSAction action, String taskID) {
		this.taskID = taskID;
		this.action = action;
		this.state = ARSTaskState.PENDING;
	}
	
	@Override
	public String getTaskID() {
		return taskID;
	}
	
	public ARSAction getAction() {
		return action;
	}

	@Override
	public synchronized void cancel() {
		if ( state == ARSTaskState.PENDING ) {
			changeStateTo(ARSTaskState.CANCELLED);
		}
	}

	@Override
	public synchronized ARSTaskState getCurrentState() {
		return state;
	}
	
	@Override
	public synchronized Throwable getThrownException() {
		return thrown;
	}

	@Override
	public synchronized ARSTaskState waitForStateChange() throws InterruptedException {
		if ( isFinalState() ) {
			return state;
		}
		// We have to compare with previous state because outside signals are possible 
		ARSTaskState p = state;
		for ( ;; ) {
			wait();
			if ( state != p ) {
				return state;
			}
		}
	}
	
	@Override
	public synchronized ARSTaskState waitForCompletion() throws InterruptedException {
		if ( isFinalState() ) {
			return state;
		}
		while ( ! isFinalState(waitForStateChange()) ) { }
		return state;
	}

	@Override
	public synchronized ARSTaskState waitForStateChange(long millis) throws InterruptedException, TimeoutException {
		if ( isFinalState() ) {
			return state;
		}

		ARSTaskState p = state;
		long end = System.currentTimeMillis() + millis;
		while ( millis > 0 ) {
			wait(millis);
			millis = end - System.currentTimeMillis();
			if ( p != state ) {
				return state;
			}
		}
		throw new TimeoutException();
	}
	
	@Override
	public synchronized ARSTaskState waitForCompletion(long millis) throws InterruptedException, TimeoutException {
		if ( isFinalState() ) {
			return state;
		}
		long end = System.currentTimeMillis() + millis;
		while ( millis > 0 ) {
			wait(millis);
			millis = end - System.currentTimeMillis();
			if ( isFinalState() ) {
				return state;
			}
		}
		throw new TimeoutException();
	}

	@Override
	public void run() {
		synchronized ( this ) {
			if ( state != ARSTaskState.PENDING ) {
				return;
			}
			changeStateTo(ARSTaskState.EXECUTING);
		}
		
		try {
			action.run();
		} catch ( InterruptedException e ) {
			synchronized ( this ) {
				thrown = e;
				changeStateTo(ARSTaskState.TIMEOUT);
				return;
			}
		} catch ( Throwable t ) {
			synchronized ( this ) {
				thrown = t;
				changeStateTo(ARSTaskState.FAILED);
				return;
			}
		}
		changeStateTo(ARSTaskState.EXECUTED);		
	}
	
	private synchronized void changeStateTo(ARSTaskState newState) {
		if ( newState != state ) {
			state = newState;
			notifyAll();
		}
	}
	
	private synchronized boolean isFinalState(ARSTaskState state) {
		switch ( state ) {
		case PENDING:
		case EXECUTING:
			return false;
		default:
			return true;
		}
	}
	
	private synchronized boolean isFinalState() {
		return isFinalState(state);
	}

}
