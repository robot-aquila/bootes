package ru.prolib.bootes.lib.data;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.tseries.CandleCloseTSeries;
import ru.prolib.aquila.core.data.tseries.CandleVolumeTSeries;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.aquila.core.data.tseries.TSeriesCacheController;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.bootes.lib.app.AppServiceLocator;

/**
 * Common class for chart data handler setup based on app service locator and symbol.
 */
public abstract class SecurityChartSetupTX implements SecurityChartDataHandler.HandlerSetup {
	public static final int CONF_SCALE = 8;
	
	public static final String SID_OHLC = "OHLC";
	public static final String SID_OHLC_MUTATOR = "OHLC_MUTATOR";
	public static final String SID_CLOSE_PRICE = "CLOSE_PRICE";
	public static final String SID_VOLUME = "VOLUME";
	
	protected final AppServiceLocator serviceLocator;
	protected final Symbol symbol;
	protected TSeries<CDecimal> close, volume;
	
	public SecurityChartSetupTX(AppServiceLocator serviceLocator, Symbol symbol) {
		this.serviceLocator = serviceLocator;
		this.symbol = symbol;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
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
	public String getOhlcSeriesID() {
		return SID_OHLC;
	}

	@Override
	public String getOhlcMutatorSeriesID() {
		return SID_OHLC_MUTATOR;
	}
	
	@Override
	public void createDerivedSeries(STSeries source,
			TSeriesCacheController<Candle> cache,
			TSeries<Candle> ohlc)
	{
		source.registerRawSeries(close = new CandleCloseTSeries(SID_CLOSE_PRICE, ohlc));
		source.registerRawSeries(volume = new CandleVolumeTSeries(SID_VOLUME, ohlc));
	}
	
	protected int getLengthOfOhlcDataToInitialLoad() {
		return 0; // do not load
	}
	
	@Override
	public void loadInitialData(EditableTSeries<Candle> ohlc) {
		int length = getLengthOfOhlcDataToInitialLoad();
		if ( length > 0 ) {
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
	}
	
	@Override
	public void onStart() {
		
	}
	
	@Override
	public void onStop() {
		
	}

}
