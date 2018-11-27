package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.Color;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;

public class ChartT0 extends SecurityChartPanel {
	private BarChart atrChart;
	private BarChartLayer emaLayer, atrLayer;
	private SWValueAxisRulerRenderer atrValueRulerRenderer;

	@Override
	protected String getOhlcSeriesID() {
		return SetupT0.SID_OHLC;
	}

	@Override
	protected String getVolumeSeriesID() {
		return SetupT0.SID_VOLUME;
	}
	
	@Override
	protected void createLayers() {
		super.createLayers();
		if ( priceChart != null ) {
			emaLayer = priceChart.addSmoothLine(source.getSeries(SetupT0.SID_EMA))
				.setColor(Color.BLUE);
		}
		if ( atrChart != null ) {
			atrLayer = atrChart.addHistogram(source.getSeries(SetupT0.SID_ATR))
				.setColor(Color.BLUE);
		}
	}
	
	@Override
	protected void dropLayers() {
		if ( emaLayer != null ) {
			priceChart.dropLayer(emaLayer.getId());
			emaLayer = null;
		}
		if ( atrLayer != null ) {
			atrChart.dropLayer(atrLayer.getId());
			atrLayer = null;
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
		//viewport.setPreferredNumberOfBars(252); // 1.5 days=14+7=21h=252*M5
		viewport.setPreferredNumberOfBars(140);
	}
	
}
