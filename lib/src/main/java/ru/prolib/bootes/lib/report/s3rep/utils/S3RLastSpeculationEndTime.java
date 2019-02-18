package ru.prolib.bootes.lib.report.s3rep.utils;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;

/**
 * Get the end time of last speculation registered in S3 report.
 */
public class S3RLastSpeculationEndTime implements TStamped {
	private final IS3Report report;
	
	public S3RLastSpeculationEndTime(IS3Report report) {
		this.report = report;
	}

	@Override
	public Instant getTime() {
		int count = report.getRecordCount();
		if ( count <= 0 ) {
			return null;
		}
		return report.getRecord(count - 1).getExitTime();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(622215, 9629)
				.append(report)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != S3RLastSpeculationEndTime.class ) {
			return false;
		}
		S3RLastSpeculationEndTime o = (S3RLastSpeculationEndTime) other;
		return new EqualsBuilder()
				.append(o.report, report)
				.build();
	}

}
