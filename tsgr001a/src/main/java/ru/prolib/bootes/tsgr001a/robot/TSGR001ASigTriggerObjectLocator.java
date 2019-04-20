package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.bootes.lib.data.ts.S3CESDSignalTrigger.ObjectLocator;

public class TSGR001ASigTriggerObjectLocator implements ObjectLocator {
	private final RobotState state;
	
	public TSGR001ASigTriggerObjectLocator(RobotState state) {
		this.state = state;
	}

	@Override
	public TSeries<CDecimal> getSource() {
		return state.getSessionDataHandler()
				.getSeriesHandlerT0()
				.getSeries()
				.getSeries(SetupT0.SID_PVC_WAVG);
	}

}
