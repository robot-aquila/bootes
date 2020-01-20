package ru.prolib.bootes.tsgr001a.robot.filter;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ADataHandler;

/**
 * Few candles of same direction.
 */
public class FilterFCSD extends AbstractFilter<S3TradeSignal> {
	private static final int DEFAULT_NUMBER_OF_CANDLES = 3;
	private final TSGR001ADataHandler handler;
	private final int number;

	public FilterFCSD(TSGR001ADataHandler handler, int numberOfCandles) {
		super("FCSD");
		this.handler = handler;
		this.number = numberOfCandles;
	}
	
	public FilterFCSD(TSGR001ADataHandler handler) {
		this(handler, DEFAULT_NUMBER_OF_CANDLES);
	}
	
	public int getNumberOfCandles() {
		return number;
	}

	@Override
	public boolean approve(S3TradeSignal signal) {
		TSeries<Candle> ohlc_s;
		synchronized ( handler ) {
			if ( handler.getSeriesHandlerT0() == null ) {
				return false;
			}
			ohlc_s = handler.getSeriesHandlerT0().getSeries().getSeries(SetupT0.SID_OHLC);
		}
		int index = signal.getIndex();
		if ( index < 0 ) {
			return false;
		}
		int first = index + 1 - number;
		if ( first < 0 ) {
			return false;
		}
		int result = 0;
		for ( int i = 0; i < number; i ++ ) {
			try {
				result += ohlc_s.get(first + i).isBullish() ? 1 : -1;
			} catch ( ValueException e ) {
				throw new IllegalStateException(e);
			}
		}
		switch ( signal.getType() ) {
		case BUY:
			return result == number;
		case SELL:
			return result == -number;
		default:
			return false;
		}
	}
	
}
