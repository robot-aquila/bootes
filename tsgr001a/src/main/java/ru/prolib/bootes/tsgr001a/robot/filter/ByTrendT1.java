package ru.prolib.bootes.tsgr001a.robot.filter;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT1;

public class ByTrendT1 extends AbstractFilter<S3TradeSignal> {
	private final RobotState state;
	
	public ByTrendT1(RobotState state) {
		super("ByTrendT1");
		this.state = state;
	}

	@Override
	public boolean approve(S3TradeSignal signal) {
		TSeries<CDecimal> ma_s;
		Security security;
		synchronized ( state ) {
			if ( ! state.isSeriesHandlerT1Defined() ) {
				return false;
			}
			ma_s = state.getSeriesHandlerT1().getSeries().getSeries(SetupT1.SID_EMA);
			security = state.getSecurity();
		}
		CDecimal ma_val;
		try {
			ma_val = ma_s.get(-1);
		} catch ( ValueException e ) {
			throw new IllegalStateException(e);
		}
		Tick last_trade = security.getLastTrade();
		if ( last_trade == null ) {
			return false;
		}
		CDecimal last_price = last_trade.getPrice();
		switch ( signal.getType() ) {
		case BUY:
			return last_price.compareTo(ma_val) >= 0;
		case SELL:
			return last_price.compareTo(ma_val) <= 0;
		default:
			return false;
		}
	}

}
