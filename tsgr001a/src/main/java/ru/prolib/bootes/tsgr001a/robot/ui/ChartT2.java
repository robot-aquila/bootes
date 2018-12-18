package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.Color;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWBarHighlighter;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.SetupT2;

public class ChartT2 extends SecurityChartPanel {
	private BarChart atrChart;
	private BarChartLayer lyrEma, lyrAtr, lyrAtrCursorCat;
	private SWValueAxisRulerRenderer atrValueRulerRenderer;

	@Override
	protected String getOhlcSeriesID() {
		return SetupT2.SID_OHLC;
	}

	@Override
	protected String getVolumeSeriesID() {
		return SetupT2.SID_VOLUME;
	}
	
	@Override
	protected void createLayers() {
		super.createLayers();
		if ( priceChart != null ) {
			lyrEma = priceChart.addSmoothLine(source.getSeries(SetupT2.SID_EMA)).setColor(Color.BLUE);
		}
		if ( atrChart != null ) {
			lyrAtrCursorCat = atrChart.addLayer(new SWBarHighlighter(chartPanel.getCategoryTracker()));
			lyrAtr = atrChart.addHistogram(source.getSeries(SetupT2.SID_ATR)).setColor(Color.BLUE);
		}
	}

	@Override
	protected void dropLayers() {
		if ( lyrEma != null ) {
			priceChart.dropLayer(lyrEma.getId());
			lyrEma = null;
		}
		if ( lyrAtr != null ) {
			atrChart.dropLayer(lyrAtr.getId());
			lyrAtr = null;
		}
		if ( lyrAtrCursorCat != null ) {
			atrChart.dropLayer(lyrAtrCursorCat.getId());
			lyrAtrCursorCat = null;
		}
		super.dropLayers();
	}

	protected void createAtrChart() {
		atrChart = chartPanel.addChart("ATR")
				.setHeight(200);
		ValueAxisDriver vad = atrChart.getValueAxisDriver();
		atrValueRulerRenderer = (SWValueAxisRulerRenderer) vad.getRenderer("LABEL");
		ChartSpaceManager hsm = atrChart.getHorizontalSpaceManager();
		hsm.getGridLinesSetup("VALUE", "LABEL").setVisible(true);
		hsm.getUpperRulerSetup("VALUE", "LABEL").setVisible(true);
		hsm.getLowerRulerSetup("VALUE", "LABEL").setVisible(true);
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
		atrValueRulerRenderer.setTickSize(security.getTickSize());
	}

	@Override
	protected void updateViewport(CategoryAxisViewport viewport) {
		super.updateViewport(viewport);
		viewport.setPreferredNumberOfBars(90); // 3 months=90 days
	}
}
