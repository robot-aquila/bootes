package ru.prolib.bootes.tsgr001a.robot.filter;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;

/**
 * Few candles of same direction.
 */
public class FilterFCSD extends AbstractFilter<S3TradeSignal> {
	private static final int DEFAULT_NUMBER_OF_CANDLES = 3;
	private final RobotState state;
	private final int number;

	public FilterFCSD(RobotState state, int numberOfCandles) {
		super("FCSD");
		this.state = state;
		this.number = numberOfCandles;
	}
	
	public FilterFCSD(RobotState state) {
		this(state, DEFAULT_NUMBER_OF_CANDLES);
	}
	
	public int getNumberOfCandles() {
		return number;
	}

	@Override
	public boolean approve(S3TradeSignal signal) {
		TSeries<Candle> ohlc_s;
		synchronized ( state ) {
			if ( ! state.isSeriesHandlerT0Defined() ) {
				return false;
			}
			ohlc_s = state.getSeriesHandlerT0().getSeries().getSeries(SetupT0.SID_OHLC);
		}
		int index = ohlc_s.toIndex(signal.getTime());
		if ( index < 0 ) {
			return false;
		}
		int first = index - number;
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
