package ru.prolib.bootes.tsgr001a.robot.sh;

import static ru.prolib.bootes.tsgr001a.robot.sh.Constants.E_OK;

import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.bootes.lib.app.AppServiceLocator;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class CleanSessionData extends CommonHandler {

	public CleanSessionData(AppServiceLocator serviceLocator, RobotState state) {
		super(serviceLocator, state);
		registerExit(E_OK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		STSeriesHandler
			t0 = state.getSeriesHandlerT0(),
			t1 = state.getSeriesHandlerT1(),
			t2 = state.getSeriesHandlerT2();
		if ( t0 != null ) {
			t0.stopDataHandling();
			t0.close();
			state.setSeriesHandlerT0(null);
		}
		if ( t1 != null ) {
			t1.stopDataHandling();
			t1.close();
			state.setSeriesHandlerT1(null);
		}
		if ( t2 != null ) {
			t2.stopDataHandling();
			t2.close();
			state.setSeriesHandlerT2(null);
		}
		state.getStateListener().sessionDataCleanup();
		return getExit(E_OK);
	}

}
