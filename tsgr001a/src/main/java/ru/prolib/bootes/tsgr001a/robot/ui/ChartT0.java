package ru.prolib.bootes.tsgr001a.robot.ui;

import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.SuperSeriesHandlerT0;

public class ChartT0 extends SecurityChartPanel {

	@Override
	protected String getPriceSeriesID() {
		return SuperSeriesHandlerT0.SID_OHLC;
	}

	@Override
	protected String getVolumeSeriesID() {
		return SuperSeriesHandlerT0.SID_VOLUME;
	}
	
}
