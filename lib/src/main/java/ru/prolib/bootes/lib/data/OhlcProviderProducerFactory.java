package ru.prolib.bootes.lib.data;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler.HandlerSetup;
import ru.prolib.aquila.data.replay.CandleReplayToSeriesStarter;
import ru.prolib.bootes.lib.app.AppServiceLocator;

public class OhlcProviderProducerFactory extends SecurityChartDataHandler.FactoryImpl {
	protected final AppServiceLocator services;

	public OhlcProviderProducerFactory(AppServiceLocator services, HandlerSetup setup) {
		super(setup);
		this.services = services;
	}
	
	@Override
	public Starter createOhlcProducer(EditableTSeries<Candle> ohlc) {
		return new CandleReplayToSeriesStarter(
				services.getOHLCReplayService(),
				setup.getSymbol(),
				ohlc
			);
	}
	
}