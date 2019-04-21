package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.QATRTSeriesFast;
import ru.prolib.aquila.core.data.tseries.QEMATSeriesFast;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.core.data.tseries.TSeriesCacheController;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.SecurityChartSetupTX;

public class SetupT2 extends SecurityChartSetupTX {
	public static final ZTFrame CONF_TFRAME = ZTFrame.D1MSK;
	public static final int CONF_ATR_PERIOD = 10;
	public static final int CONF_EMA_PERIOD = 90;
	public static final int CONF_LOAD_HISTORY_DEPTH = CONF_EMA_PERIOD * 2;
	
	public static final String SID_SHARED = "T2";
	public static final String SID_ATR = "ATR";
	public static final String SID_EMA = "EMA";

	public SetupT2(AppServiceLocator serviceLocator, Symbol symbol) {
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
		QATRTSeriesFast atr = new QATRTSeriesFast(SID_ATR, ohlc, CONF_ATR_PERIOD, CONF_SCALE);
		QEMATSeriesFast ema = new QEMATSeriesFast(SID_EMA, close, CONF_EMA_PERIOD, CONF_SCALE);
		
		cache.addCache(atr);
		cache.addCache(ema);
	
		source.registerRawSeries(atr);
		source.registerRawSeries(ema);
	}

}
