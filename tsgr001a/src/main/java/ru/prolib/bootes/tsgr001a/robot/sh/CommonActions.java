package ru.prolib.bootes.tsgr001a.robot.sh;

import ru.prolib.aquila.core.data.tseries.STSeriesHandler;
import ru.prolib.bootes.tsgr001a.robot.RobotState;

public class CommonActions {
	
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

}
