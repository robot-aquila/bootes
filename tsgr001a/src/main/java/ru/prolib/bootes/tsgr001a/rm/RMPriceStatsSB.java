package ru.prolib.bootes.tsgr001a.rm;

import java.time.Instant;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Series based price statistics.
 */
public class RMPriceStatsSB implements RMPriceStats {
	private TSeries<CDecimal> daily, local;
	
	public void setDailyMoveSeries(TSeries<CDecimal> series) {
		this.daily = series;
	}
	
	public void setLocalMoveSeries(TSeries<CDecimal> series) {
		this.local = series;
	}
	
	public TSeries<CDecimal> getDailyMoveSeries() {
		return daily;
	}
	
	public TSeries<CDecimal> getLocalMoveSeries() {
		return local;
	}
	
	private CDecimal getFirstBefore(TSeries<CDecimal> series, Instant time) {
		series.lock();
		try {
			Interval m_int = series.getTimeFrame().getInterval(time);
			Instant m_time = m_int.getStart();
			for ( int i = series.getLength() - 1; i >= 0; i -- ) {
				Instant c_time = series.toKey(i);
				if ( c_time.compareTo(m_time) < 0 ) {
					return series.get(i);
				}
			}
			return null;
		} catch ( ValueException e ) {
			throw new RuntimeException("Unexpectedc exception: ", e);
		} finally {
			series.unlock();
		}
	}

	@Override
	public CDecimal getDailyPriceMove(Instant time) {
		return getFirstBefore(daily, time);
	}

	@Override
	public CDecimal getLocalPriceMove(Instant time) {
		return getFirstBefore(local, time);
	}

}
