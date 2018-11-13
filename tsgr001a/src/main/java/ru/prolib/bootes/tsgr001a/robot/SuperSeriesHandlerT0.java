package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.tseries.CandleCloseTSeries;
import ru.prolib.aquila.core.data.tseries.CandleVolumeTSeries;
import ru.prolib.aquila.core.data.tseries.QATRTSeries;
import ru.prolib.aquila.core.data.tseries.QEMATSeriesFast;
import ru.prolib.aquila.core.data.tseries.SuperTSeries;
import ru.prolib.aquila.core.data.tseries.TSeriesCacheControllerETS;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesByLastTrade;
import ru.prolib.bootes.lib.app.AppServiceLocator;

/**
 * Data handler of main timeframe (T=T0=TZ) chart.
 */
public class SuperSeriesHandlerT0 implements SuperSeriesHandler {
	public static final ZTFrame CONF_TFRAME = ZTFrame.M5MSK;
	public static final int CONF_SCALE = 6;
	public static final int CONF_ATR_PERIOD = 10;
	public static final int CONF_EMA_PERIOD = 252; // 252 is number of M5 bars of 1.5 trading day
	public static final int CONF_LOAD_HISTORY_DEPTH = CONF_EMA_PERIOD * 2;
	
	public static final String SID_T0 = "TZ";
	public static final String SID_OHLC = "OHLC";
	public static final String SID_OHLC_MUTATOR = "OHLC_MUTATOR";
	public static final String SID_CLOSE_PRICE = "CLOSE_PRICE";
	public static final String SID_VOLUME = "VOLUME";
	public static final String SID_ATR = "ATR";
	public static final String SID_EMA = "EMA";
	
	private final Symbol symbol;
	private final AppServiceLocator serviceLocator;
	
	private boolean initialized, started, closed;
	private SuperTSeries superSeries;
	private CandleSeriesByLastTrade ohlcProducer;
	
	public SuperSeriesHandlerT0(AppServiceLocator serviceLocator, Symbol symbol) {
		this.symbol = symbol;
		this.serviceLocator = serviceLocator;
	}
	
	@Override
	public synchronized SuperTSeries getSuperSeries() {
		return superSeries;
	}
	
	@Override
	public synchronized void initialize() {
		if ( initialized ) {
			throw new IllegalStateException("Already initialized: " + symbol);
		}
		Terminal terminal = serviceLocator.getTerminal();

		superSeries = new SuperTSeries(SID_T0, ZTFrame.M5, serviceLocator.getEventQueue());
		EditableTSeries<Candle> ohlcMutator = superSeries.createSeries(SID_OHLC, false);
		// TODO: load historical data
		TSeriesCacheControllerETS<Candle> seriesCacheCtrl = new TSeriesCacheControllerETS<Candle>(ohlcMutator);
		ohlcMutator = seriesCacheCtrl;
		TSeries<Candle> ohlc = superSeries.getSeries(SID_OHLC);
		TSeries<CDecimal> close = new CandleCloseTSeries(SID_CLOSE_PRICE, ohlc);
		TSeries<CDecimal> volume = new CandleVolumeTSeries(SID_VOLUME, ohlc);
		QATRTSeries atr = new QATRTSeries(SID_ATR, ohlc, CONF_ATR_PERIOD);
		QEMATSeriesFast ema = new QEMATSeriesFast(SID_EMA, close, CONF_EMA_PERIOD, CONF_SCALE);
		
		seriesCacheCtrl.addCache(ema);
		
		// OHLC mutator+cache controller will clear caches on close
		superSeries.registerRawSeries(ohlcMutator, SID_OHLC_MUTATOR);
		superSeries.registerRawSeries(close);
		superSeries.registerRawSeries(volume);
		superSeries.registerRawSeries(atr);
		superSeries.registerRawSeries(/*(EditableTSeries<?>) ???*/ ema);

		ohlcProducer = new CandleSeriesByLastTrade(ohlcMutator, terminal, symbol);
		
		initialized = true;
	}
	
	@Override
	public synchronized void startDataHandling() {
		if ( started ) {
			return;
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
		
		closed = true;
	}

}
