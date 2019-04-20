package ru.prolib.bootes.lib.rm;

import java.time.Instant;

import ru.prolib.aquila.core.utils.LocalTimeTable;

public interface IRMContractStrategy {

	LocalTimeTable getTradingTimetable();

	/**
	 * Get position parameters for time.
	 * <p>
	 * @param time - time to determine parameters for 
	 * @return position parameters
	 */
	RMContractStrategyPositionParams getPositionParams(Instant time);

}