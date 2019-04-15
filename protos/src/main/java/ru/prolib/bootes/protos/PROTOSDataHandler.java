package ru.prolib.bootes.protos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.CandleCloseTSeries;
import ru.prolib.aquila.core.data.tseries.CandleVolumeTSeries;
import ru.prolib.aquila.core.data.tseries.QEMATSeriesFast;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.aquila.core.data.tseries.TSeriesCacheController;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.robo.ISessionDataHandler;
import ru.prolib.bootes.lib.robo.s3.S3RobotState;

public class PROTOSDataHandler implements ISessionDataHandler {
	
	public static class SetupT0 implements SecurityChartDataHandler.HandlerSetup {
		public static final ZTFrame CONF_TFRAME = ZTFrame.M5MSK;
		public static final int CONF_SCALE = 8;
		public static final int CONF_MA_SLOW_PERIOD = 14;
		public static final int CONF_MA_FAST_PERIOD = 7;
		public static final int CONF_HISTORY_DEPTH = CONF_MA_SLOW_PERIOD * 2;
		
		public static final String SID_OHLC = "OHLC";
		public static final String SID_OHLC_MUTATOR = "OHLC_MUTATOR";
		public static final String SID_CLOSE_PRICE = "CLOSE_PRICE";
		public static final String SID_VOLUME = "VOLUME";
		public static final String SID_SHARED = "T0";
		public static final String SID_MA_SLOW = "MA_SLOW";
		public static final String SID_MA_FAST = "MA_FAST";
		
		protected final AppServiceLocator serviceLocator;
		protected final Symbol symbol;
		protected TSeries<CDecimal> close, volume;
		
		public SetupT0(AppServiceLocator serviceLocator, Symbol symbol) {
			this.serviceLocator = serviceLocator;
			this.symbol = symbol;
		}

		@Override
		public Symbol getSymbol() {
			return symbol;
		}

		@Override
		public ZTFrame getTimeFrame() {
			return CONF_TFRAME;
		}

		@Override
		public Terminal getTerminal() {
			return serviceLocator.getTerminal();
		}

		@Override
		public EventQueue getEventQueue() {
			return serviceLocator.getEventQueue();
		}

		@Override
		public String getSharedSeriesID() {
			return SID_SHARED;
		}

		@Override
		public String getOhlcSeriesID() {
			return SID_OHLC;
		}

		@Override
		public String getOhlcMutatorSeriesID() {
			return SID_OHLC_MUTATOR;
		}
		
		private QEMATSeriesFast newEMA(String id, int period) {
			return new QEMATSeriesFast(id, close, period, CONF_SCALE);
		}

		@Override
		public void createDerivedSeries(STSeries source,
										TSeriesCacheController<Candle> cache,
										TSeries<Candle> ohlc)
		{
			source.registerRawSeries(close = new CandleCloseTSeries(SID_CLOSE_PRICE, ohlc));
			source.registerRawSeries(volume = new CandleVolumeTSeries(SID_VOLUME, ohlc));

			QEMATSeriesFast
				ma_slow = newEMA(SID_MA_SLOW, CONF_MA_SLOW_PERIOD),
				ma_fast = newEMA(SID_MA_FAST, CONF_MA_FAST_PERIOD);
			
			cache.addCache(ma_slow);
			cache.addCache(ma_fast);
			
			source.registerRawSeries(ma_slow);
			source.registerRawSeries(ma_fast);
		}

		@Override
		public void loadInitialData(EditableTSeries<Candle> ohlc) {
			int length = CONF_HISTORY_DEPTH;
			if ( length <= 0 ) {
				return;
			}
			MDStorage<TFSymbol, Candle> stor = serviceLocator.getOHLCHistoryStorage();
			try ( CloseableIterator<Candle> it = stor.createReader(
					new TFSymbol(symbol, ohlc.getTimeFrame()),
					length,
					serviceLocator.getTerminal().getCurrentTime()) )
			{
				while ( it.next() ) {
					ohlc.set(it.item().getStartTime(), it.item());
				}
			} catch ( Exception e ) {
				throw new RuntimeException("Error loading OHLC history", e);
			}
		}
		
		@Override
		public void onStart() {
			
		}

		@Override
		public void onStop() {
			
		}
		
	}
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PROTOSDataHandler.class);
	}
	
	private final AppServiceLocator serviceLocator;
	private final S3RobotState state;
	private STSeriesHandler root;
	
	public PROTOSDataHandler(AppServiceLocator serviceLocator,
							 S3RobotState state)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
	}

	@Override
	public boolean startSession() {
		if ( root != null ) {
			throw new IllegalStateException();
		}
		root = new SecurityChartDataHandler(new SetupT0(
				serviceLocator,
				state.getSecurity().getSymbol()
			));
		try {
			root.initialize();
			root.startDataHandling();
			return true;
		} catch ( Throwable t ) {
			root.stopDataHandling();
			root.close();
			root = null;
			logger.error("Data initialization error: ", t);
			return false;
		}
	}

	@Override
	public void cleanSession() {
		if ( root == null ) {
			throw new IllegalStateException();
		}
		root.stopDataHandling();
		root.close();
		root = null;
	}

}
