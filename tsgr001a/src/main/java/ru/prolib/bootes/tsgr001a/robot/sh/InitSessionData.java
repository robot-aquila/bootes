package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;
import ru.prolib.bootes.tsgr001a.robot.SuperSeriesHandler;
import ru.prolib.bootes.tsgr001a.robot.SuperSeriesHandlerT0;

public class InitSessionData extends CommonHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(InitSessionData.class);
	}

	public InitSessionData(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		registerExit(E_OK);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		Symbol symbol = state.getContractParams().getSymbol();
		SuperSeriesHandler
			t0 = new SuperSeriesHandlerT0(serviceLocator, symbol),
			t1 = null,
			t2 = null;
		try {
			t0.initialize();
			t0.startDataHandling();
			// TODO: start T1, T2
		} catch ( Throwable t ) {
			// TODO: stop T2, T1
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
