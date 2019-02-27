package ru.prolib.bootes.lib.report.s3rep.filter;

import java.time.Instant;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.utils.LocalTimePeriod;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.lib.report.s3rep.S3RRecord;

public class S3RCrossingIntradayPeriod extends AbstractFilter<S3RRecord> {
	private static final String DEFAULT_ID = "CROSS_INTRADAY_PERIOD";
	private final LocalTimePeriod period;

	public S3RCrossingIntradayPeriod(String id, LocalTimePeriod period) {
		super(id);
		this.period = period;
	}
	
	public S3RCrossingIntradayPeriod(LocalTimePeriod period) {
		this(DEFAULT_ID, period);
	}
	
	public LocalTimePeriod getPeriod() {
		return period;
	}

	@Override
	public boolean approve(S3RRecord rec) {
		Instant start = rec.getEntryTime(), end = rec.getExitTime();
		if ( end == null ) {
			return true;
		}
		return period.overlappedBy(Interval.of(start, end));
	}

}
