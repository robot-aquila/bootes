package ru.prolib.bootes.protos;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.bootes.lib.data.ts.CMASignalTrigger;

public class PROTOSSigTrigObjectLocator implements CMASignalTrigger.ObjectLocator {
	private final PROTOSRobotState state;
	
	public PROTOSSigTrigObjectLocator(PROTOSRobotState state) {
		this.state = state;
	}

	@Override
	public TSeries<CDecimal> getFast() {
		return state.getSessionDataHandler()
				.getSeriesHandlerT0()
				.getSeries()
				.getSeries(PROTOSSetupT0.SID_MA_FAST);
	}

	@Override
	public TSeries<CDecimal> getSlow() {
		return state.getSessionDataHandler()
				.getSeriesHandlerT0()
				.getSeries()
				.getSeries(PROTOSSetupT0.SID_MA_SLOW);
	}

}
