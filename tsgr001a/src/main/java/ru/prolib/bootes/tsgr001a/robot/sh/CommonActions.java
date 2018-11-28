package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class CommonActions {
	
	public void cleanupCurrentDataHandlers(RobotState state) {
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
	}

}
