package ru.prolib.bootes.protos;

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

public class PROTOSSetupT0 extends SecurityChartSetupTX {
	public static final ZTFrame CONF_TFRAME = ZTFrame.M5MSK;
	public static final int CONF_ATR_PERIOD = 10;
	public static final int CONF_MA_SLOW_PERIOD = 14;
	public static final int CONF_MA_FAST_PERIOD = 7;
	public static final int CONF_LOAD_HISTORY_DEPTH = CONF_MA_SLOW_PERIOD * 2;
	
	public static final String SID_SHARED = "T0";
	public static final String SID_ATR = "ATR";
	public static final String SID_MA_SLOW = "MA_SLOW";
	public static final String SID_MA_FAST = "MA_FAST";
		
	public PROTOSSetupT0(AppServiceLocator serviceLocator, Symbol symbol) {
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
	
	private QEMATSeriesFast newEMA(String id, int period) {
		return new QEMATSeriesFast(id, close, period, CONF_SCALE);
	}

	@Override
	public void createDerivedSeries(STSeries source,
									TSeriesCacheController<Candle> cache,
									TSeries<Candle> ohlc)
	{
		super.createDerivedSeries(source, cache, ohlc);

		QATRTSeriesFast atr = new QATRTSeriesFast(SID_ATR, ohlc, CONF_ATR_PERIOD, CONF_SCALE);
		QEMATSeriesFast
			ma_slow = newEMA(SID_MA_SLOW, CONF_MA_SLOW_PERIOD),
			ma_fast = newEMA(SID_MA_FAST, CONF_MA_FAST_PERIOD);
		
		cache.addCache(atr);
		cache.addCache(ma_slow);
		cache.addCache(ma_fast);
		
		source.registerRawSeries(atr);
		source.registerRawSeries(ma_slow);
		source.registerRawSeries(ma_fast);
	}
	
}