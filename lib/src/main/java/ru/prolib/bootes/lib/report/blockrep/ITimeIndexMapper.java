package ru.prolib.bootes.lib.report.blockrep;

import java.time.Instant;

/**
 * Converter from time series indices to times and vice-versa.
 */
public interface ITimeIndexMapper {
	
	/**
	 * Convert time to most closest element index.
	 * <p>
	 * @param time - time to get index of
	 * @return element index
	 * @throws IllegalStateException - the series has no elements
	 */
	int toIndex(Instant time) throws IllegalStateException;
	
	/**
	 * Convert element index to start time of appropriate interval.
	 * <p>
	 * @param index - index of series element
	 * @return start time of interval
	 * @throws IllegalArgumentException - wrong element index
	 */
	Instant toIntervalStart(int index) throws IllegalArgumentException;
	
	/**
	 * Convert element index to end time of appropriate interval.
	 * <p>
	 * @param index - index of series element
	 * @return end time of interval (exclusive)
	 * @throws IllegalArgumentException - wrong element index
	 */
	Instant toIntervalEnd(int index) throws IllegalArgumentException;
	
}
