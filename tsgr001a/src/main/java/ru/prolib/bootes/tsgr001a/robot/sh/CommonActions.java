package ru.prolib.bootes.tsgr001a.robot.sh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.rm.RMContractStrategy;
import ru.prolib.bootes.tsgr001a.rm.RMPriceStatsSB;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;
import ru.prolib.bootes.tsgr001a.robot.SetupT1;
import ru.prolib.bootes.tsgr001a.robot.SetupT2;

public class CommonActions {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CommonActions.class);
	}
	
	public void cleanupCurrentDataHandlers(RobotState state) {
		STSeriesHandler sh = null;
		if ( state.isSeriesHandlerT0Defined() ) {
			sh = state.getSeriesHandlerT0();
			sh.stopDataHandling();
			sh.close();
			state.setSeriesHandlerT0(null);
		}
		if ( state.isSeriesHandlerT1Defined() ) {
			sh = state.getSeriesHandlerT1();
			sh.stopDataHandling();
			sh.close();
			state.setSeriesHandlerT1(null);
		}
		if ( state.isSeriesHandlerT2Defined() ) {
			sh = state.getSeriesHandlerT2();
			sh.stopDataHandling();
			sh.close();
			state.setSeriesHandlerT2(null);
		}
		state.getStateListener().sessionDataCleanup();
	}
	
	public boolean initDataHandlers(AppServiceLocator serviceLocator, RobotState state) {
		Symbol symbol = state.getContractParams().getSymbol();
		STSeriesHandler
			t0 = new SecurityChartDataHandler(new SetupT0(serviceLocator, symbol)),
			t1 = new SecurityChartDataHandler(new SetupT1(serviceLocator, symbol)),
			t2 = new SecurityChartDataHandler(new SetupT2(serviceLocator, symbol));
		try {
			t0.initialize();
			t0.startDataHandling();
			t1.initialize();
			t1.startDataHandling();
			t2.initialize();
			t2.startDataHandling();
		} catch ( Throwable t ) {
			t2.stopDataHandling();
			t1.stopDataHandling();
			t0.stopDataHandling();
			logger.error("Data initialization error: ", t);
			return false;
		}
		
		state.setSeriesHandlerT0(t0);
		state.setSeriesHandlerT1(t1);
		state.setSeriesHandlerT2(t2);
		state.getStateListener().sessionDataAvailable();
		return true;
	}
	
	public void updatePositionParams(AppServiceLocator serviceLocator, RobotState state) {
		RMContractStrategy cs = state.getContractStrategy();
		RMPriceStatsSB ps = (RMPriceStatsSB) cs.getPriceStats();
		if ( ps == null ) {
			ps = new RMPriceStatsSB();
			cs.setPriceStats(ps);
		}
		ps.setDailyMoveSeries(state.getSeriesHandlerT2().getSeries().getSeries(SetupT2.SID_ATR));
		ps.setLocalMoveSeries(state.getSeriesHandlerT0().getSeries().getSeries(SetupT0.SID_ATR));
		cs.setPortfolio(state.getPortfolio());
		cs.setSecurity(state.getSecurity());
		state.setPositionParams(cs.getPositionParams(serviceLocator.getTerminal().getCurrentTime()));
		state.getStateListener().riskManagementUpdate();
	}

}
