package ru.prolib.bootes.protos;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.QATRTSeriesFast;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.core.data.tseries.TSeriesCacheController;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.SecurityChartSetupTX;

public class PROTOSSetupT1 extends SecurityChartSetupTX {
	public static final ZTFrame CONF_TFRAME = ZTFrame.D1MSK;
	public static final int CONF_ATR_PERIOD = 10;
	public static final int CONF_LOAD_HISTORY_DEPTH = CONF_ATR_PERIOD * 2;
	
	public static final String SID_SHARED = "T1";
	public static final String SID_ATR = "ATR";

	public PROTOSSetupT1(AppServiceLocator serviceLocator, Symbol symbol) {
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
		
		cache.addCache(atr);
		
		source.registerRawSeries(atr);
	}

}
