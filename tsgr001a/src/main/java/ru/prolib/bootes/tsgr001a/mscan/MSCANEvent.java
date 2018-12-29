package ru.prolib.bootes.tsgr001a.mscan;

import java.time.Instant;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * The market event.
 * <p>
 * When market scanner detects a trigger state it produces an market event
 * object. Market event is closing by one of closing scanners.
 */
public interface MSCANEvent {
	
	/**
	 * Get entry which started this event.
	 * Every event has it own starting entry.
	 * <p>
	 * @return a starting log entry
	 */
	MSCANLogEntry getStart();
	
	/**
	 * Get type ID of start entry.
	 * <p>
	 * @return type ID
	 */
	String getStartTypeID();
	
	/**
	 * Get time of start entry.
	 * <p>
	 * @return time
	 */
	Instant getStartTime();
	
	/**
	 * Get optional value of start entry.
	 * <p>
	 * @return optional value
	 */
	CDecimal getStartValue();
	
	/**
	 * Get list of all log entries of the event.
	 * <p>
	 * @return list of all log entries of this event
	 */
	List<MSCANLogEntry> getEntries();
	
	/**
	 * Test that event is finished.
	 * <p>
	 * @return true if event is finished, false if still lasts
	 */
	boolean isFinished();
	
	/**
	 * Get entry which closed this event.
	 * Close entry is available only for finished events.
	 * <p>
	 * @return final log entry
	 * @throws IllegalStateException if event is not finished 
	 */
	MSCANLogEntry getClose();
	
	/**
	 * Get type ID of final entry.
	 * <p>
	 * @return type ID
	 * @throws IllegalStateException if event is not finished
	 */
	String getCloseTypeID();
	
	/**
	 * Get time of final entry.
	 * <p>
	 * @return time
	 * @throws IllegalStateException if event is not finished
	 */
	Instant getCloseTime();
	
	/**
	 * Get optional value of final entry.
	 * <p>
	 * @return optional value
	 * @throws IllegalStateException if event is not finished
	 */
	CDecimal getCloseValue();

}
