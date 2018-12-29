package ru.prolib.bootes.tsgr001a.mscan;

import java.time.Instant;

/**
 * Market scanner initiates market analysis to detect new events and track
 * existing unfinished events. The stream of event changes are passed to
 * listeners for further analysis.
 */
public interface MSCANScanner {
	
	/**
	 * Add new sensor.
	 * <p>
	 * @param sensor - market sensor
	 */
	void addSensor(MSCANSensor sensor);
	
	/**
	 * Remove sensor.
	 * <p>
	 * @param sensor - market sensor
	 */
	void removeSensor(MSCANSensor sensor);
	
	/**
	 * Execute market analysis.
	 * <p>
	 * @param currentTime - current time
	 */
	void analyze(Instant currentTime);
	
	void addListener(MSCANListener listener);
	void removeListener(MSCANListener listener);

}
