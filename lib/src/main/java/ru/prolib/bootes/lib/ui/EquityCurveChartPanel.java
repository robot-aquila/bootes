package ru.prolib.bootes.lib.ui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartOrientation;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;

public class EquityCurveChartPanel {
	protected BarChartPanelImpl chartPanel;
	protected BarChart chart;
	protected SWValueAxisRulerRenderer valueRulerRenderer;
	protected ObservableSeries<?> source;
	
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
		
	}
	
	protected void dropLayers() {
		
	}
	
	protected void updateSource(ObservableSeries<?> source) {
		chartPanel.setCategories(this.source = source);
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
		updateViewport();
		
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
	public void update(ObservableSeries<?> source) {
		dropLayers();
		updateSource(source);
		createLayers();
		updateViewport();
	}
	
}
