package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.Color;
import java.time.ZoneId;

import javax.swing.JPanel;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWSimpleTextOverlay;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.SetupT1;

public class ChartT1 extends SecurityChartPanel {
	private final ZoneId zoneID;
	private BarChartLayer lyrEma,lyrPriceTitle;
	
	public ChartT1(ZoneId zoneID) {
		this.zoneID = zoneID;
	}

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
			lyrEma = priceChart.addSmoothLine(source.getSeries(SetupT1.SID_EMA)).setColor(new Color(64, 64, 127));
			lyrPriceTitle = priceChart.addLayer(new SWSimpleTextOverlay(new PriceChartTitleOverlayWithEMA(
					security.getDisplayName(),
					zoneID,
					source.getSeries(SetupT1.SID_OHLC),
					source.getSeries(SetupT1.SID_EMA),
					chartPanel.getCategoryTracker()
				)
			));
		}
	}

	@Override
	protected void dropLayers() {
		if ( lyrEma != null ) {
			priceChart.dropLayer(lyrEma.getId());
			lyrEma = null;
		}
		if ( lyrPriceTitle != null ) {
			priceChart.dropLayer(lyrPriceTitle.getId());
			lyrPriceTitle = null;
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
	public JPanel create() {
		JPanel panel = super.create();
		chartPanel.setPreferredNumberOfBars(140); // 10 days=10*14=140 hours
		return panel;
	}

}
