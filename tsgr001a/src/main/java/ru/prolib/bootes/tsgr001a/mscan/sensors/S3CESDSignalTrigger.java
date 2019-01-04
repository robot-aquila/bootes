package ru.prolib.bootes.tsgr001a.mscan.sensors;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;

/**
 * A trigger of three consecutive elements when each previous is greater or
 * lesser than next.
 */
public class S3CESDSignalTrigger implements SignalTrigger {
	private TSeries<CDecimal> source;
	
	public synchronized void setSource(TSeries<CDecimal> source) {
		this.source = source;
	}

	@Override
	public synchronized SignalType getSignal(Instant currentTime) {
		int index = source.toIndex(currentTime);
		if ( index < 3 ) {
			return SignalType.NONE;
		}
		CDecimal v1, v2, v3;
		try {
			v1 = source.get(index - 3);
			v2 = source.get(index - 2);
			v3 = source.get(index - 1);
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		}
		if ( v1 == null || v2 == null || v3 == null ) {
			return SignalType.NONE;
		}
		int v2_cr = v2.compareTo(v1);
		int v3_cr = v3.compareTo(v2);
		SignalType result = SignalType.NONE;
		if ( v2_cr > 0 && v3_cr > 0 ) {
			result = SignalType.BUY;
		} else
		if ( v2_cr < 0 && v3_cr < 0 ) {
			result = SignalType.SELL;
		}
		return result;
	}

}
