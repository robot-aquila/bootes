package ru.prolib.bootes.tsgr001a.robot.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.lib.data.ts.TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;

public class StopLossGtATR extends AbstractFilter<TradeSignal> {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(StopLossGtATR.class);
	}
	
	private final RobotState state;
	
	public StopLossGtATR(RobotState state) {
		super("SL_gt_ATR");
		this.state = state;
	}

	@Override
	public boolean approve(TradeSignal signal) {
		CDecimal sl = signal.getStopLossPts(), atr = null;
		synchronized ( state ) {
			if ( state.isSeriesHandlerT0Defined() ) {
				TSeries<CDecimal> atr_s = state.getSeriesHandlerT0().getSeries().getSeries(SetupT0.SID_ATR); 
				if ( atr_s.getLength() < 2 ) {
					return false;
				}
				try {
					atr = atr_s.get(-1);
				} catch ( ValueException e ) {
					throw new IllegalStateException(e);
				}
			}
		}
		if ( sl != null && atr != null ) {
			Object args[] = { atr, sl, signal.getTime() };
			logger.debug("ATR={} SL={} T={}", args);
			return sl.compareTo(atr) > 0;
		}
		return false;
	}

}
