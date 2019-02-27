package ru.prolib.bootes.lib.report.s3rep.filter;

import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.lib.report.s3rep.S3RRecord;

public class S3RShortDurationRecords extends AbstractFilter<S3RRecord> {
	private static final String DEFAULT_ID = "SHORT_DURATION";
	private final long duration;
	
	public S3RShortDurationRecords(String filterID, long maxDurationMinutes) {
		super(filterID);
		this.duration = maxDurationMinutes;
	}
	
	public S3RShortDurationRecords(long maxDurationMinutes) {
		this(DEFAULT_ID, maxDurationMinutes);
	}
	
	public long getDuration() {
		return duration;
	}

	@Override
	public boolean approve(S3RRecord rec) {
		Long actual = rec.getDurationMinutes();
		return actual == null ? true : actual <= duration;
	}

}
