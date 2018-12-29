package ru.prolib.bootes.tsgr001a.mscan.sensors;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.tsgr001a.mscan.MSCANEvent;
import ru.prolib.bootes.tsgr001a.mscan.MSCANEventImpl;
import ru.prolib.bootes.tsgr001a.mscan.MSCANLogEntry;
import ru.prolib.bootes.tsgr001a.mscan.MSCANLogEntryImpl;
import ru.prolib.bootes.tsgr001a.mscan.MSCANSensor;

/**
 * Sensor to detect serie of three candles in same direction. Same direction
 * mean that there is series of increasing or decreasing values. 
 * This class uses two sources to produce log entries: condition series is
 * used to test condition and detect when event occurs. Value series is
 * used to determine a trigger value (for example price to enter market). Both
 * series are decimal values which makes class unified to use with any
 * source type. Typically the first serie is close price of candle series or
 * weighted average price series. The second is candle close or candle open
 * price.
 */
public class SensorWAP3C implements MSCANSensor {
	private TSeries<CDecimal> conditionSource, valueSource;

	public synchronized void setConditionSeries(TSeries<CDecimal> source) {
		this.conditionSource = source;
	}
	
	public synchronized void setValueSeries(TSeries<CDecimal> source) {
		this.valueSource = source;
	}

	@Override
	public synchronized MSCANEvent analyze(Instant currentTime) {
		try {
			int index = conditionSource.toIndex(currentTime);
			if ( index < 3 ) {
				return null;
			}
			CDecimal v1 = conditionSource.get(index - 3);
			CDecimal v2 = conditionSource.get(index - 2);
			CDecimal v3 = conditionSource.get(index - 1);
			if ( v1 == null || v2 == null || v3 == null ) {
				return null;
			}
			int v2_cr = v2.compareTo(v1);
			int v3_cr = v3.compareTo(v2);
			String typeID = null;
			if ( v2_cr > 0 && v3_cr > 0 ) {
				typeID = "BUY";
			} else
			if ( v2_cr < 0 && v3_cr < 0 ) {
				typeID = "SELL";
			} else {
				return null;
			}
			CDecimal tv = valueSource.get(currentTime);
			if ( tv == null ) {
				return null;
			}
			MSCANLogEntry entry = new MSCANLogEntryImpl(
					true,
					typeID,
					currentTime,
					tv,
					typeID
				);
			return new MSCANEventImpl(entry);
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		}
	}

	@Override
	public MSCANLogEntry analyze(Instant currentTime, MSCANEvent event) {
		throw new UnsupportedOperationException();
	}

}
