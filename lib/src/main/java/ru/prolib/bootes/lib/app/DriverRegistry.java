package ru.prolib.bootes.lib.app;

public interface DriverRegistry {
	
	/**
	 * Register component of specific terminal driver.
	 * <p>
	 * @param driver_id - driver ID
	 * @param component - component which provide terminal creation and management
	 */
	void registerDriver(String driver_id, AppComponent component);

}
