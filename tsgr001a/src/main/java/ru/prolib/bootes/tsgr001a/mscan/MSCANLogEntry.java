package ru.prolib.bootes.tsgr001a.mscan;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public interface MSCANLogEntry {
	
	/**
	 * Test that is a trigger entry.
	 * See details in {@link MSCANScanner}
	 * <p>
	 * @return true if this is a trigger entry, false otherwise
	 */
	boolean isTrigger();
	
	/**
	 * Get type ID to identify entries by type.
	 * Type ID is used to identify entries by type.
	 * Type is implementation defined custom string.
	 * <p>
	 * @return type ID
	 */
	String getTypeID();
	
	/**
	 * Get time of entry.
	 * <p>
	 * @return time when entry appeared
	 */
	Instant getTime();
	
	/**
	 * Get optional value.
	 * Most entries is related to some value which is important in context of
	 * entry. This may be a price, volume, value of market indicator, etc.
	 * <p> 
	 * @return optional value
	 */
	CDecimal getValue();
	
	/**
	 * Get text of the entry.
	 * Most entries provide commentary to specify what exactly happened and
	 * what exactly this entry about is.
	 * <p>
	 * @return text of log entry
	 */
	String getText();
}
