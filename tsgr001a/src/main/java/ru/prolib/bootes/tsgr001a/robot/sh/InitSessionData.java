package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;
import ru.prolib.bootes.tsgr001a.robot.SetupT1;
import ru.prolib.bootes.tsgr001a.robot.SetupT2;

public class InitSessionData extends CommonHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(InitSessionData.class);
	}
	
	private final CommonActions ca;

	public InitSessionData(AppServiceLocator serviceLocator,
			RobotState state,
			CommonActions ca)
	{
		super(serviceLocator, state);
		this.ca = ca;
		registerExit(E_OK);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Symbol symbol = state.getContractParams().getSymbol();
		ca.cleanupCurrentDataHandlers(state);
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
			return getExit(E_ERROR);
		}
		
		state.setSeriesHandlerT0(t0);
		state.setSeriesHandlerT1(t1);
		state.setSeriesHandlerT2(t2);
		state.getStateListener().sessionDataAvailable();
		return getExit(E_OK);
	}
	
}
