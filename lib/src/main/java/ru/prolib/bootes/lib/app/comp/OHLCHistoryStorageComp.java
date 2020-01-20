package ru.prolib.bootes.lib.app.comp;

import java.io.File;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.data.replay.CandleReplayServiceImpl;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.MDStorageSimpleWarmer;
import ru.prolib.aquila.web.utils.finam.data.FinamData;
import ru.prolib.bootes.lib.app.AppConfigService2;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.config.AppConfig2;
import ru.prolib.bootes.lib.config.OHLCHistoryConfig2;
import ru.prolib.bootes.lib.config.OHLCHistoryConfig2Section;

public class OHLCHistoryStorageComp extends CommonComp {
	private static final String CONFIG_SECTION_ID = "ohlc-history";
	private static final String DEFAULT_ID = "OHLC-HISTORY";
	
	public OHLCHistoryStorageComp(AppServiceLocator serviceLocator, String serviceID) {
		super(serviceLocator, serviceID);
	}
	
	public OHLCHistoryStorageComp(AppServiceLocator serviceLocator)
	{
		this(serviceLocator, DEFAULT_ID);
	}

	@Override
	public void init() throws Throwable {
		AppConfig2 app_conf = serviceLocator.getConfig();
		OHLCHistoryConfig2 conf = app_conf.getSection(CONFIG_SECTION_ID);
		File data_dir = conf.getDataDirectory(), cache_dir = conf.getCacheDirectory();
		if ( data_dir != null && cache_dir != null ) {
			MDStorage<TFSymbol, Candle> storage = new MDStorageSimpleWarmer<>(new FinamData()
				.createCachingOHLCV(
					conf.getDataDirectory(),
					conf.getCacheDirectory(),
					serviceLocator.getPriceScaleDB()
				));
			
			serviceLocator.setOHLCHistoryStorage(storage);
			serviceLocator.setOHLCReplayService(new CandleReplayServiceImpl(serviceLocator.getScheduler(), storage));
		}
	}

	@Override
	public void startup() throws Throwable {
		
	}

	@Override
	public void shutdown() throws Throwable {
		
	}

	@Override
	public void registerConfig(AppConfigService2 config_service) {
		config_service.addSection(CONFIG_SECTION_ID, new OHLCHistoryConfig2Section());
	}

}
