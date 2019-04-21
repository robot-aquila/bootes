package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.lib.data.SecurityChartSetupTX;
import ru.prolib.bootes.lib.robo.ISessionDataHandler;
import ru.prolib.bootes.lib.robo.sh.statereq.IContractDeterminable;

public class TSGR001ADataHandler implements ISessionDataHandler {
	private final AppServiceLocator serviceLocator;
	private final IContractDeterminable state;
	private STSeriesHandler t0, t1, t2;
	
	public TSGR001ADataHandler(AppServiceLocator serviceLocator, IContractDeterminable state) {
		this.serviceLocator = serviceLocator;
		this.state = state;
	}
	
	@Override
	public synchronized boolean startSession() {
		Symbol symbol = state.getContractParams().getSymbol();
		try {
			t0 = create(new SetupT0(serviceLocator, symbol));
			t1 = create(new SetupT1(serviceLocator, symbol));
			t2 = create(new SetupT2(serviceLocator, symbol));
			return true;
		} catch ( Throwable t ) {
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
