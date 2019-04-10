package ru.prolib.bootes.lib.rm;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public interface RMPriceStats {
	
	/**
	 * Get daily average of price movement.
	 * <p>
	 * @param time - time to determine the value 
	 * @return average of price move
	 */
	CDecimal getDailyPriceMove(Instant time);
	
	/**
	 * Get local average of price movement.
	 * <p>
	 * @param time - time to determine the value
	 * @return average of price move
	 */
	CDecimal getLocalPriceMove(Instant time);
	
}
