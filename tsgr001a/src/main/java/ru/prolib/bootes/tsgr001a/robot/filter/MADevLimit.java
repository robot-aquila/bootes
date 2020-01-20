package ru.prolib.bootes.tsgr001a.robot.filter;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ADataHandler;

public class MADevLimit extends AbstractFilter<S3TradeSignal> {
	public static final CDecimal DEFAULT_MAX_DEVIATION = CDecimalBD.of("0.30000"); // 30%
	private final RobotState state;
	private final CDecimal maxDeviation;

	public MADevLimit(RobotState state, CDecimal maxDeviation) {
		super("MA_DEV_Lmt");
		this.state = state;
		this.maxDeviation = maxDeviation;
	}
	
	public MADevLimit(RobotState state) {
		this(state, DEFAULT_MAX_DEVIATION);
	}
	
	public CDecimal getMaxDeviation() {
		return maxDeviation;
	}

	@Override
	public boolean approve(S3TradeSignal signal) {
		CDecimal daily_range, price, ma;
		synchronized ( state ) {
			TSGR001ADataHandler dh = state.getSessionDataHandler();
			if ( dh.getSeriesHandlerT0() == null ) {
				return false;
			}
			daily_range = state.getPositionParams().getAvgDailyPriceMove();
			price = signal.getExpectedPrice();
			TSeries<CDecimal> ma_s = dh.getSeriesHandlerT0().getSeries().getSeries(SetupT0.SID_EMA);
			if ( ma_s.getLength() < 2 ) {
				return false;
			}
			try {
				ma = ma_s.get(signal.getIndex());
			} catch ( ValueException e ) {
				throw new IllegalStateException(e);
			}
		}
		CDecimal max_delta = daily_range.multiply(maxDeviation);
		switch ( signal.getType() ) {
		case BUY:
			CDecimal price_max = ma.add(max_delta);
			return price.compareTo(price_max) <= 0;
		case SELL:
			CDecimal price_min = ma.subtract(max_delta);
			return price.compareTo(price_min) >= 0;
		default:
			return false;
		}
	}

}
