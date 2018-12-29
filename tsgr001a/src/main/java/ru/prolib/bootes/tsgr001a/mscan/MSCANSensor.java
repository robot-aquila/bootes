package ru.prolib.bootes.tsgr001a.mscan;

import java.time.Instant;

public interface MSCANSensor {
	
	/**
	 * Analyze current situation and produce event if needed. This method
	 * should produce event in cases when sensor detected situation this
	 * sensor is developed to detect for. For example if current market price
	 * crosses some price level, special pattern is detected on price chart,
	 * etc. Starting log entry will be attached to the event.
	 * <p>
	 * @param currentTime - current time
	 * @return market event or null if no event produced
	 */
	MSCANEvent analyze(Instant currentTime);
	
	/**
	 * Analyze current situation considering existing unfinished event. This
	 * method should produce log entry and attach it to the event then return
	 * as result. 
	 * <p>
	 * @param currentTime - current time
	 * @param event - event previously created by calling
	 * {@link #analyze(Instant)} method.
	 * @return log entry or null if no entry produced
	 */
	MSCANLogEntry analyze(Instant currentTime, MSCANEvent event);
	
}
