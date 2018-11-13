package ru.prolib.bootes.lib.ui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.tseries.SuperTSeries;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartOrientation;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerSetup;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWCandlestickLayer;

abstract public class SecurityChartPanel {
	protected BarChartPanelImpl chartPanel;
	protected SWTimeAxisRulerRenderer timeRulerRenderer;
	protected SWValueAxisRulerRenderer priceValueRulerRenderer, volValueRulerRenderer;
	protected BarChart priceChart, volChart;
	protected BarChartLayer priceLayer, volLayer;
	protected SuperTSeries source;

	protected void updateViewport(CategoryAxisViewport viewport) {
		viewport.setPreferredNumberOfBars(100);
		if ( source != null ) {
			viewport.setCategoryRangeByFirstAndNumber(0, source.getLength());
		} else {
			viewport.setCategoryRangeByFirstAndNumber(0, 0);
		}
	}
	
	protected void updateViewport() {
		updateViewport(chartPanel.getCategoryAxisViewport()); 
	}
	
	abstract protected String getPriceSeriesID();
	abstract protected String getVolumeSeriesID();
	
	protected void createLayers() {
		priceLayer = priceChart.addLayer(new SWCandlestickLayer(source.getSeries(getPriceSeriesID())));
		volLayer = volChart.addHistogram(source.getSeries(getVolumeSeriesID()));
	}
	
	protected void dropLayers() {
		if ( priceChart != null ) {
			if ( priceLayer != null ) {
				priceChart.dropLayer(priceLayer.getId());
				priceLayer = null;
			}
		}
		if ( volChart != null ) {
			if ( volLayer != null ) {
				volChart.dropLayer(volLayer.getId());
				volLayer = null;
			}
		}
	}
	
	protected void createPriceChart() {
		priceChart = chartPanel.addChart("OHLC")
				.setHeight(600);
		ChartSpaceManager vsm = priceChart.getVerticalSpaceManager();
		vsm.getGridLinesSetup("CATEGORY", "TIME").setVisible(true);
		((SWTimeAxisRulerSetup) vsm.getLowerRulerSetup("CATEGORY", "TIME"))
			.setVisible(true)
			.setShowInnerLine(true)
			.setShowOuterLine(false);
		((SWTimeAxisRulerSetup) vsm.getUpperRulerSetup("CATEGORY", "TIME"))
			.setVisible(false);
		ValueAxisDriver vad = priceChart.getValueAxisDriver();
		priceValueRulerRenderer = (SWValueAxisRulerRenderer) vad.getRenderer("LABEL");
		ChartSpaceManager hsm = priceChart.getHorizontalSpaceManager();
		hsm.getGridLinesSetup("VALUE", "LABEL").setVisible(true);
		hsm.getUpperRulerSetup("VALUE", "LABEL").setVisible(true);
		hsm.getLowerRulerSetup("VALUE", "LABEL").setVisible(true);
	}
	
	protected void createVolumeChart() {
		volChart = chartPanel.addChart("VOLUME")
				.setHeight(200);
		ChartSpaceManager vsm = volChart.getVerticalSpaceManager();
		((SWTimeAxisRulerSetup) vsm.getLowerRulerSetup("CATEGORY", "TIME"))
			.setVisible(true)
			.setDisplayPriority(20) // hide this first
			.setShowInnerLine(true)
			.setShowOuterLine(true);
		((SWTimeAxisRulerSetup) vsm.getUpperRulerSetup("CATEGORY", "TIME"))
			.setVisible(true)
			.setDisplayPriority(10)
			.setShowInnerLine(true)
			.setShowOuterLine(false);
		// TODO: add date ruler to bottom
		ValueAxisDriver vad = volChart.getValueAxisDriver();
		volValueRulerRenderer = (SWValueAxisRulerRenderer) vad.getRenderer("LABEL");
		volValueRulerRenderer.setTickSize(CDecimalBD.of(1L)); // always 1
		ChartSpaceManager hsm = volChart.getHorizontalSpaceManager();
		hsm.getLowerRulerSetup("VALUE", "LABEL").setVisible(true);
		hsm.getUpperRulerSetup("VALUE", "LABEL").setVisible(true);
	}
	
	public JPanel create() {
		chartPanel = new BarChartPanelImpl(BarChartOrientation.LEFT_TO_RIGHT);
		
		CategoryAxisDriver cad = chartPanel.getCategoryAxisDriver();
		cad.registerRenderer(timeRulerRenderer = new SWTimeAxisRulerRenderer("TIME"));
		
		createPriceChart();
		createVolumeChart();
		updateViewport();
		
		JPanel chartRoot = new JPanel(new GridLayout(1, 1));
		chartRoot.add(chartPanel.getRootPanel());
		return chartRoot;
	}
	
	public void update(SuperTSeries source, Security security) {
		dropLayers();
		this.source = source;
		chartPanel.setCategories(source);
		timeRulerRenderer.setCategories(source);
		priceValueRulerRenderer.setTickSize(security.getTickSize());
		createLayers();
		updateViewport();
	}
	
	public void clear() {
		dropLayers();
	}

}
