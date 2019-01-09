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
	 * Exit ID to indicate that data tracking must be stopped.
	 */
	public static final String E_STOP_DATA_TRACKING = "STOP_DATA_TRACKING";
	
	/**
	 * Exit ID to indicate that trading period is ended.
	 */
	public static final String E_STOP_TRADING = "STOP_TRADING";
	
	/**
	 * Exit ID to indicate that trading must be continued.
	 */
	public static final String E_NEXT_TRADING = "NEXT_TRADING";
	
	public static final String E_BUY = "BUY";
	public static final String E_SELL = "SELL";
	public static final String E_TAKE_PROFIT = "TAKE_PROFIT";
	public static final String E_STOP_LOSS = "STOP_LOSS";
	
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
	public static final String S_WAIT_MARKET_SIGNAL = "WAIT_MARKET_SIGNAL";
	public static final String S_OPEN_LONG = "OPEN_LONG";
	public static final String S_OPEN_SHORT = "OPEN_SHORT";
	public static final String S_TRACK_LONG = "TRACK_LONG";
	public static final String S_TRACK_SHORT = "TRACK_SHORT";
	public static final String S_WAIT_MARKET_SIGNAL_RESULT = "WAIT_MARKET_SIGNAL_RESULT";
	public static final String S_CLEAN_SESSION_DATA = "CLEAN_SESSION_DATA";

}
