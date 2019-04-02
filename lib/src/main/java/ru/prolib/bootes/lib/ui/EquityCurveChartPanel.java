package ru.prolib.bootes.lib.ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleHighSeries;
import ru.prolib.aquila.core.data.CandleLowSeries;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartOrientation;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWAreaLayer;

public class EquityCurveChartPanel {
	protected BarChartPanelImpl chartPanel;
	protected BarChart chart;
	protected SWValueAxisRulerRenderer valueRulerRenderer;
	protected ObservableSeries<Candle> source;
	protected Series<CDecimal> sourceMax, sourceMin;
	protected BarChartLayer layerMin, layerMax;

	protected void createChart() {
		chart = chartPanel.addChart("EQUITY");
		// time axis ruler setup
		ValueAxisDriver vad = chart.getValueAxisDriver();
		valueRulerRenderer = (SWValueAxisRulerRenderer) vad.getRenderer("LABEL");
		ChartSpaceManager hsm = chart.getHorizontalSpaceManager();
		hsm.getGridLinesSetup("VALUE", "LABEL").setVisible(true);
		hsm.getUpperRulerSetup("VALUE", "LABEL").setVisible(true);
		hsm.getLowerRulerSetup("VALUE", "LABEL").setVisible(true);		
	}
	
	protected void createLayers() {
		if ( chart == null ) {
			return;
		}
		layerMin = chart.addLayer(new SWAreaLayer(sourceMin).setColor(new Color(127, 127, 127, 127)));
		layerMax = chart.addLayer(new SWAreaLayer(sourceMax).setColor(new Color(127, 127, 0, 127)));
	}
	
	protected void dropLayers() {
		if ( layerMin != null ) {
			chart.dropLayer(layerMin.getId());
			layerMin = null;
		}
		if ( layerMax != null ) {
			chart.dropLayer(layerMax.getId());
			layerMax = null;
		}
	}
	
	protected void updateSource(ObservableSeries<Candle> source) {
		chartPanel.setCategories(this.source = source);
		sourceMax = new CandleHighSeries(source);
		sourceMin = new CandleLowSeries(source);
	}

	/**
	 * First-time initialization.
	 * <p>
	 * @return panel containing equity curve chart
	 */
	public JPanel create() {
		if ( chartPanel != null ) {
			throw new IllegalStateException();
		}
		chartPanel = new BarChartPanelImpl(BarChartOrientation.LEFT_TO_RIGHT);
		createChart();
		
		JPanel chartRoot = new JPanel(new GridLayout(1, 1));
		chartRoot.add(chartPanel.getRootPanel());
		return chartRoot;
	}
	
	/**
	 * Update chart data source.
	 * <p>
	 * This call causes dropping and recreating all existing layers. Not for regular
	 * updates. Should be used only if data source instance is defined or changed.
	 * <p>
	 * @param source - data source instance
	 */
	public void update(ObservableSeries<Candle> source) {
		dropLayers();
		updateSource(source);
		createLayers();
	}
	
}
