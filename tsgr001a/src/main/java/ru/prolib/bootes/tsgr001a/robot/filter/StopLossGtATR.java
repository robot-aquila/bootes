package ru.prolib.bootes.tsgr001a.robot.filter;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.impl.AbstractFilter;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;
import ru.prolib.bootes.tsgr001a.robot.TSGR001ADataHandler;

public class StopLossGtATR extends AbstractFilter<S3TradeSignal> {
	private final TSGR001ADataHandler dataHandler;
	
	public StopLossGtATR(TSGR001ADataHandler dataHandler) {
		super("SL_gt_ATR");
		this.dataHandler = dataHandler;
	}

	@Override
	public boolean approve(S3TradeSignal signal) {
		CDecimal sl = signal.getStopLossPts(), atr = null;
		synchronized ( dataHandler ) {
			if ( dataHandler.getSeriesHandlerT0() != null ) {
				TSeries<CDecimal> atr_s = dataHandler.getSeriesHandlerT0()
						.getSeries()
						.getSeries(SetupT0.SID_ATR); 
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
			//Object args[] = { atr, sl, signal.getTime() };
			//logger.debug("ATR={} SL={} T={}", args);
			return sl.compareTo(atr) > 0;
		}
		return false;
	}

}
