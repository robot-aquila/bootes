package ru.prolib.bootes.protos;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.SecurityChartSetupTX;
import ru.prolib.bootes.lib.robo.ISessionDataHandler;
import ru.prolib.bootes.lib.robo.s3.S3RobotState;

public class PROTOSDataHandler implements ISessionDataHandler {
	private final AppServiceLocator serviceLocator;
	private final S3RobotState state;
	private STSeriesHandler t0, d1;
	
	public PROTOSDataHandler(AppServiceLocator serviceLocator,
							 S3RobotState state)
	{
		this.serviceLocator = serviceLocator;
		this.state = state;
	}

	@Override
	public synchronized boolean startSession() {
		if ( t0 != null ) {
			throw new IllegalStateException();
		}
		Symbol symbol = state.getContractParams().getSymbol();
		try {
			t0 = create(new PROTOSSetupT0(serviceLocator, symbol));
			d1 = create(new PROTOSSetupD1(serviceLocator, symbol));
			return true;
		} catch ( Throwable t ) {
			cleanSession();
			return false;
		}
	}

	@Override
	public synchronized void cleanSession() {
		if ( t0 == null ) {
			throw new IllegalStateException();
		}
		t0 = close(t0);
		d1 = close(d1);
	}
	
	public synchronized STSeriesHandler getSeriesHandlerT0() {
		return t0;
	}
	
	public synchronized STSeriesHandler getSeriesHandlerD1() {
		return d1;
	}
	
	private STSeriesHandler create(SecurityChartSetupTX setup) {
		STSeriesHandler h = new SecurityChartDataHandler(setup);
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
