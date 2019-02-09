package ru.prolib.bootes.lib.report.blockrep;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ZTFrame;

public class TimeIndexMapperTS implements ITimeIndexMapper {
	private final TSeries<?> basis;
	
	public TimeIndexMapperTS(TSeries<?> basis) {
		this.basis = basis;
	}
	
	public TSeries<?> getBasis() {
		return basis;
	}

	@Override
	public int toIndex(Instant time) throws IllegalStateException {
		basis.lock();
		try {
			int index = basis.toIndex(time);
			if ( index >= 0 ) {
				return index;
			}
			int len = basis.getLength();
			if ( len == 0 ) {
				throw new IllegalStateException("The series has no elements");
			} else if ( len == 1 ) {
				return 0;
			}
			ZTFrame tf = basis.getTimeFrame();
			Interval left = tf.getInterval(basis.toKey(0));
			Interval right = tf.getInterval(basis.toKey(len - 1));
			if ( left.contains(time) || time.compareTo(left.getStart()) < 0 ) {
				return 0;
			}
			if ( right.contains(time) || time.compareTo(right.getEnd()) >= 0 ) {
				return len - 1;
			}
			int index_left = 0, index_right = len - 1;
			Instant time_left = left.getEnd(), time_right = right.getStart();
			// If we're here then searching time is between two elements for sure
			// So the goal is to find two consecutive elements and choose one which is closer
			do {
				int index_mid = (index_right - index_left) / 2 + index_left;
				Interval interval_mid = tf.getInterval(basis.toKey(index_mid));
				if ( time.compareTo(interval_mid.getStart()) < 0 ) {
					index_right = index_mid;
					time_right = interval_mid.getStart();
				} else if ( time.compareTo(interval_mid.getEnd()) >= 0 ) {
					index_left = index_mid;
					time_left = interval_mid.getStart();
				} else {
					throw new IllegalStateException("Inconsistent underlying data");
				}
			} while ( index_right - index_left > 1 );
			long time_at_left = ChronoUnit.MILLIS.between(time_left, time);
			long time_at_right = ChronoUnit.MILLIS.between(time, time_right);
			return time_at_left < time_at_right ? index_left : index_right;
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		} finally {
			basis.unlock();
		}
	}

	@Override
	public Instant toIntervalStart(int index) throws IllegalArgumentException {
		try {
			return basis.toKey(index);
		} catch ( ValueException e ) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Instant toIntervalEnd(int index) throws IllegalArgumentException {
		return basis.getTimeFrame().getInterval(toIntervalStart(index)).getEnd();
	}

}
