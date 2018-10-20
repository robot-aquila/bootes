package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.tseries.CandleCloseTSeries;
import ru.prolib.aquila.core.data.tseries.CandleVolumeTSeries;
import ru.prolib.aquila.core.data.tseries.QATRTSeries;
import ru.prolib.aquila.core.data.tseries.QEMATSeriesFast;
import ru.prolib.aquila.core.data.tseries.TSeriesCacheControllerETS;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesByLastTrade;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2Key;

/**
 * Data handler of main timeframe (T=T0=TZ) chart.
 */
public class DataSliceHandlerTZ implements DataSliceHandler {
	public static final ZTFrame CONF_TFRAME = ZTFrame.M5MSK;
	public static final int CONF_SCALE = 6;
	public static final int CONF_ATR_PERIOD = 10;
	public static final int CONF_EMA_PERIOD = 252; // 252 is number of M5 bars of one and half of trading day
	public static final int CONF_LOAD_HISTORY_DEPTH = CONF_EMA_PERIOD * 2;
	
	public static final String SID_OHLC = "OHLC";
	public static final String SID_OHLC_MUTATOR = "OHLC_MUTATOR";
	public static final String SID_CLOSE_PRICE = "CLOSE_PRICE";
	public static final String SID_VOLUME = "VOLUME";
	public static final String SID_ATR = "ATR";
	public static final String SID_EMA = "EMA";
	
	private final Symbol symbol;
	private final RobotServiceLocator serviceLocator;
	
	private boolean initialized, started, closed;
	private final SDP2Key dsKey;
	private SDP2DataSlice<SDP2Key> ds;
	private CandleSeriesByLastTrade ohlcProducer;
	
	public DataSliceHandlerTZ(RobotServiceLocator serviceLocator, Symbol symbol) {
		this.serviceLocator = serviceLocator;
		this.symbol = symbol;
		this.dsKey = new SDP2Key(CONF_TFRAME, symbol);
	}
	
	@Override
	public synchronized void initialize() {
		if ( initialized ) {
			throw new IllegalStateException("Already initialized: " + symbol);
		}
		
		ds = serviceLocator.getDataSliceProvider().createSlice(dsKey);
		try {
			EditableTSeries<Candle> ohlcMutator = ds.createSeries(SID_OHLC, false);
			// TODO: load historical data
			TSeriesCacheControllerETS<Candle> seriesCacheCtrl = new TSeriesCacheControllerETS<Candle>(ohlcMutator);
			ohlcMutator = seriesCacheCtrl;
			TSeries<Candle> ohlc = ds.getSeries(SID_OHLC);
			TSeries<CDecimal> close = new CandleCloseTSeries(SID_CLOSE_PRICE, ohlc);
			TSeries<CDecimal> volume = new CandleVolumeTSeries(SID_VOLUME, ohlc);
			QATRTSeries atr = new QATRTSeries(SID_ATR, ohlc, CONF_ATR_PERIOD);
			QEMATSeriesFast ema = new QEMATSeriesFast(SID_EMA, close, CONF_EMA_PERIOD, CONF_SCALE);
			
			seriesCacheCtrl.addCache(ema);
			
			ds.registerRawSeries(ohlcMutator, SID_OHLC_MUTATOR); // OHLC mutator+cache controller will clear caches on close
			ds.registerRawSeries(close);
			ds.registerRawSeries(volume);
			ds.registerRawSeries(atr);
			ds.registerRawSeries((EditableTSeries<?>) ema);
	
			ohlcProducer = new CandleSeriesByLastTrade(ohlcMutator, serviceLocator.getTerminal(), ds.getSymbol());
			
			initialized = true;
		} catch ( Throwable t ) {
			serviceLocator.getDataSliceProvider().purgeSlice(dsKey);
			throw t;
		}
	}
	
	@Override
	public synchronized void startDataHandling() {
		if ( started ) {
			throw new IllegalStateException("Handler already started: " + symbol);
		}
		if ( ! initialized ) {
			throw new IllegalStateException("Handler is not initialized: " + symbol);
		}
		if ( closed ) {
			throw new IllegalStateException("Handler is closed: " + symbol);
		}
		ohlcProducer.start();
		
		started = true;
	}
	
	@Override
	public synchronized void stopDataHandling() {
		if ( started ) {
			ohlcProducer.stop();
			
			started = false;
		}
	}
	
	@Override
	public synchronized void close() {
		if ( started ) {
			throw new IllegalStateException("Handler is started: " + symbol);
		}
		if ( ! initialized || closed  ) {
			return;
		}
		serviceLocator.getDataSliceProvider().purgeSlice(dsKey);
		
		closed = true;
	}

}
