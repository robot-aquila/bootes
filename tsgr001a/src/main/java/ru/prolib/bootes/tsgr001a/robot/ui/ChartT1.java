package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.Color;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.SetupT1;

public class ChartT1 extends SecurityChartPanel {
	private BarChartLayer emaLayer;

	@Override
	protected String getOhlcSeriesID() {
		return SetupT1.SID_OHLC;
	}

	@Override
	protected String getVolumeSeriesID() {
		return SetupT1.SID_VOLUME;
	}
	
	@Override
	protected void createLayers() {
		super.createLayers();
		if ( priceChart != null ) {
			emaLayer = priceChart.addSmoothLine(source.getSeries(SetupT1.SID_EMA)).setColor(new Color(64, 64, 127));
		}
	}

	@Override
	protected void dropLayers() {
		if ( emaLayer != null ) {
			priceChart.dropLayer(emaLayer.getId());
			emaLayer = null;
		}
		super.dropLayers();
	}

	protected void createAtrChart() {

	}

	@Override
	protected void createCharts() {
		createPriceChart();
		createAtrChart();
		createVolumeChart();
	}
	
	@Override
	protected void updateSecurity(Security security) {
		super.updateSecurity(security);
	}
	
	@Override
	protected void updateViewport(CategoryAxisViewport viewport) {
		super.updateViewport(viewport);
		viewport.setPreferredNumberOfBars(140); // 10 days=10*14=140 hours
	}

}
