package ru.prolib.bootes.tsgr001a.robot.filter;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.IFilter;

public interface IS3TSFilterFactory {

	/**
	 * Produce filter using a code.
	 * <p>
	 * @param code - code of filter
	 * @return filter instance
	 */
	IFilter<S3TradeSignal> produce(String code);

}