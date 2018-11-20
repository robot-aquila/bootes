package ru.prolib.bootes.lib.app.comp;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.web.utils.finam.data.FinamData;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.OHLCHistoryConfig;

public class OHLCHistoryStorageComp extends CommonComp {
	private static final String DEFAULT_ID = "OHLC-HISTORY";
	
	public OHLCHistoryStorageComp(AppConfig appConfig,
			AppServiceLocator serviceLocator,
			String serviceID)
	{
		super(appConfig, serviceLocator, serviceID);
	}
	
	public OHLCHistoryStorageComp(AppConfig appConfig,
			AppServiceLocator serviceLocator)
	{
		this(appConfig, serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		OHLCHistoryConfig conf = appConfig.getOHLCHistoryConfig();
		MDStorage<TFSymbol, Candle> storage = new FinamData()
			.createCachingOHLCV(
				conf.getDataDirectory(),
				conf.getCacheDirectory(),
				serviceLocator.getPriceScaleDB()
			);
		serviceLocator.setOHLCHistoryStorage(storage);
	}

	@Override
	public void startup() throws Throwable {
		
	}

	@Override
	public void shutdown() throws Throwable {
		
	}

}
