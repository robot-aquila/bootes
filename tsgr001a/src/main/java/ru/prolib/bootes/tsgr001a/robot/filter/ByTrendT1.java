package ru.prolib.bootes.tsgr001a.robot.filter;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;
import ru.prolib.bootes.tsgr001a.robot.SetupT1;

public class ByTrendT1 extends AbstractFilter<S3TradeSignal> {
	private final RobotState state;
	
	public ByTrendT1(RobotState state) {
		super("ByTrendT1");
		this.state = state;
	}

	@Override
	public boolean approve(S3TradeSignal signal) {
		TSeries<CDecimal> t1_ma, t0_close;
		synchronized ( state ) {
			STSeriesHandler
					t1_h = state.getSessionDataHandler().getSeriesHandlerT1(),
					t0_h = state.getSessionDataHandler().getSeriesHandlerT0();
			if ( t1_h == null || t0_h == null ) {
				return false;
			}
			t1_ma = t1_h.getSeries().getSeries(SetupT1.SID_EMA);
			t0_close = t0_h.getSeries().getSeries(SetupT0.SID_CLOSE_PRICE);
		}
		CDecimal t1_ma_val, t0_close_val;
		try {
			t1_ma_val = t1_ma.getFirstBefore(signal.getTime());
			t0_close_val = t0_close.get(signal.getIndex());
		} catch ( ValueException e ) {
			throw new IllegalStateException(e);
		}
		if ( t1_ma_val == null || t0_close_val == null ) {
			return false;
		}
		switch ( signal.getType() ) {
		case BUY:
			return t0_close_val.compareTo(t1_ma_val) >= 0;
		case SELL:
			return t0_close_val.compareTo(t1_ma_val) <= 0;
		default:
			return false;
		}
	}

}
