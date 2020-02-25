package ru.prolib.bootes.tsgr001a.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler.HandlerSetup;
import ru.prolib.aquila.data.replay.CandleReplayToSeriesStarter;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.SecurityChartSetupTX;
import ru.prolib.bootes.lib.robo.ISessionDataHandler;
import ru.prolib.bootes.lib.robo.sh.statereq.IContractDeterminable;

public class TSGR001ADataHandler implements ISessionDataHandler {
	static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TSGR001ADataHandler.class);
	}
	
	private final AppServiceLocator serviceLocator;
	private final IContractDeterminable state;
	private STSeriesHandler t0, t1, t2;
	
	public TSGR001ADataHandler(AppServiceLocator serviceLocator, IContractDeterminable state) {
		this.serviceLocator = serviceLocator;
		this.state = state;
	}
	
	@Override
	public synchronized boolean startSession() {
		try {
			t0 = create(new SetupT0(serviceLocator, state));
			t1 = create(new SetupT1(serviceLocator, state));
			t2 = create(new SetupT2(serviceLocator, state));
			return true;
		} catch ( Throwable t ) {
			logger.error("Unexpected exception: ", t);
			cleanSession();
			return false;
		}
	}

	@Override
	public synchronized void cleanSession() {
		t0 = close(t0);
		t1 = close(t1);
		t2 = close(t2);
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT0() {
		return t0;
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT1() {
		return t1;
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT2() {
		return t2;
	}
	
	static class MyFactory extends SecurityChartDataHandler.FactoryImpl {
		protected final AppServiceLocator services;

		public MyFactory(AppServiceLocator services, HandlerSetup setup) {
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
	
	private STSeriesHandler create(SecurityChartSetupTX setup) {
		//STSeriesHandler h = new SecurityChartDataHandler(setup);
		STSeriesHandler h = new SecurityChartDataHandler(setup, new MyFactory(serviceLocator, setup));
		try {
			h.initialize();
			h.startDataHandling();
			return h;
		} catch ( Throwable t ) {
			h.stopDataHandling();
			h.close();
			throw t;
		}
	}
	
	private STSeriesHandler close(STSeriesHandler h) {
		if ( h != null ) {
			h.stopDataHandling();
			h.close();
		}
		return null;
	}

}
