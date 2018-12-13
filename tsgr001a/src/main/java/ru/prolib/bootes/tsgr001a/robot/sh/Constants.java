package ru.prolib.bootes.tsgr001a.robot.sh;

public class Constants {
	
	// Exit identifiers
	
	/**
	 * OK exit ID.
	 */
	public static final String E_OK = "OK";
	
	/**
	 * Error exit ID.
	 */
	public static final String E_ERROR = "ER";
	
	/**
	 * Break exit ID.
	 */
	public static final String E_INTERRUPT = "BR";
	
	/**
	 * New session exit ID.
	 */
	public static final String E_NEW_SESSION = "NS";
	
	/**
	 * Exit ID to indicate that trading must be stopped.
	 */
	public static final String E_STOP_TRADING = "STOP_TRADING";

	
	// State identifiers
	
	/**
	 * Initial state ID.
	 */
	public static final String S_INIT = "INIT";
	
	/**
	 * Wait for account.
	 */
	public static final String S_WAIT_ACCOUNT = "WAIT_ACCOUNT";
	
	/**
	 * Select contract state ID.
	 */
	public static final String S_CHOOSE_CONTRACT = "CCONTR";
	public static final String S_INIT_SESSION_DATA = "INIT_SESSION_DATA";
	public static final String S_WAIT_SESSION_END = "WAIT_SESSION_END";
	public static final String S_CLEAN_SESSION_DATA = "CLEAN_SESSION_DATA";

}
