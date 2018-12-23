package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.QATRTSeriesFast;
import ru.prolib.aquila.core.data.tseries.QEMATSeriesFast;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.core.data.tseries.TSeriesCacheController;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.data.PVCluster;
import ru.prolib.bootes.tsgr001a.data.PVClusterSeriesByLastTrade;
import ru.prolib.bootes.tsgr001a.data.WeightedAverageTSeries;

/**
 * Setup of data handler of primary timeframe (T=T0=TZ).
 */
public class SetupT0 extends SetupTX {
	public static final ZTFrame CONF_TFRAME = ZTFrame.M5MSK;
	public static final int CONF_ATR_PERIOD = 10;
	public static final int CONF_EMA_PERIOD = 252; // 252 is number of M5 bars of 1.5 trading day
	public static final int CONF_LOAD_HISTORY_DEPTH = CONF_EMA_PERIOD * 2;
	
	public static final String SID_SHARED = "TZ";
	public static final String SID_ATR = "ATR";
	public static final String SID_EMA = "EMA";
	public static final String SID_PVC_RAW = "PVC_RAW";
	public static final String SID_PVC_WAVG = "PVC_WAVG";
	
	private PVClusterSeriesByLastTrade pvcProducer;

	public SetupT0(AppServiceLocator serviceLocator, Symbol symbol) {
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
		
		EditableTSeries<PVCluster> pvc_raw = source.createSeries(SID_PVC_RAW, false);
		WeightedAverageTSeries pvc_wavg = new WeightedAverageTSeries(pvc_raw, SID_PVC_WAVG);
		pvcProducer = new PVClusterSeriesByLastTrade(pvc_raw, getTerminal(), symbol);
		
		QATRTSeriesFast atr = new QATRTSeriesFast(SID_ATR, ohlc, CONF_ATR_PERIOD, CONF_SCALE);
		QEMATSeriesFast ema = new QEMATSeriesFast(SID_EMA, close, CONF_EMA_PERIOD, CONF_SCALE);
		
		cache.addCache(atr);
		cache.addCache(ema);
	
		source.registerRawSeries(pvc_wavg);
		source.registerRawSeries(atr);
		source.registerRawSeries(ema);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		pvcProducer.start();
	}
	
	@Override
	public void onStop() {
		pvcProducer.stop();
		super.onStop();
	}

}
