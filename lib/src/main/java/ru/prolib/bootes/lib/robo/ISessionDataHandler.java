package ru.prolib.bootes.lib.robo;

public interface ISessionDataHandler {

	/**
	 * Called when trading session has started and data should be tracked.
	 * <p>
	 * @return true if data tracking started successfully, false otherwise
	 */
	boolean startSession();
	
	/**
	 * Called when trading session has ended and data tracking should be
	 * finished.
	 */
	void cleanSession();

}
