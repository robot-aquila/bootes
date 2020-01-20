package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.bootes.lib.data.ts.S3CESDSignalTrigger.ObjectLocator;

public class TSGR001ASigTriggerObjectLocator implements ObjectLocator {
	private final RobotState state;
	private final String sourceSeriesID;
	
	public TSGR001ASigTriggerObjectLocator(RobotState state, String source_series_id) {
		this.state = state;
		this.sourceSeriesID = source_series_id;
	}

	@Override
	public TSeries<CDecimal> getSource() {
		return state.getSessionDataHandler()
				.getSeriesHandlerT0()
				.getSeries()
				.getSeries(sourceSeriesID);
	}

}
