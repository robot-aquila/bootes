package ru.prolib.bootes.lib.rm;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;

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
	
	@Override
	public CDecimal getDailyPriceMove(Instant time) {
		return daily.getFirstBefore(time);
	}

	@Override
	public CDecimal getLocalPriceMove(Instant time) {
		return local.getFirstBefore(time);
	}

}
