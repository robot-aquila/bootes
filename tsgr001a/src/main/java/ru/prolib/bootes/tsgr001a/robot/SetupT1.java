package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.QEMATSeriesFast;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.core.data.tseries.TSeriesCacheController;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class SetupT1 extends SetupTX {
	public static final ZTFrame CONF_TFRAME = ZTFrame.H1MSK;
	public static final int CONF_EMA_PERIOD = 70;
	public static final int CONF_LOAD_HISTORY_DEPTH = CONF_EMA_PERIOD * 2;
	
	public static final String SID_SHARED = "T1";
	public static final String SID_EMA = "EMA";
	
	public SetupT1(AppServiceLocator serviceLocator, Symbol symbol) {
		super(serviceLocator, symbol);
	}

	@Override
	public ZTFrame getTimeFrame() {
		return CONF_TFRAME;
	}

	@Override
	public String getSharedSeriesID() {
		return SID_SHARED;
	}

	@Override
	protected int getLengthOfOhlcDataToInitialLoad() {
		return CONF_LOAD_HISTORY_DEPTH;
	}

	@Override
	public void createDerivedSeries(STSeries source,
			TSeriesCacheController<Candle> cache,
			TSeries<Candle> ohlc)
	{
		super.createDerivedSeries(source, cache, ohlc);
		QEMATSeriesFast ema = new QEMATSeriesFast(SID_EMA, close, CONF_EMA_PERIOD, CONF_SCALE);
		
		cache.addCache(ema);
	
		source.registerRawSeries(ema);
	}

}
