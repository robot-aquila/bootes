package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.data.tseries.SuperTSeries;

public interface SuperSeriesHandler {

	void initialize();
	void startDataHandling();
	void stopDataHandling();
	void close();
	SuperTSeries getSuperSeries();
}