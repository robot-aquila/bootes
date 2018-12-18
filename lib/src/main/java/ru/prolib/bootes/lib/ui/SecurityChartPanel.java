package ru.prolib.bootes.lib.ui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.tseries.STSeries;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.BarChartOrientation;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerRendererV2;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerSetup;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWBarHighlighter;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWCandlestickLayer;

abstract public class SecurityChartPanel {
	protected BarChartPanelImpl chartPanel;
	protected SWTimeAxisRulerRendererV2 timeRulerRenderer;
	protected SWValueAxisRulerRenderer priceValueRulerRenderer, volValueRulerRenderer;
	protected BarChart priceChart, volChart;
	protected BarChartLayer lyrPrice, lyrVol, lyrPriceCursorCat, lyrVolCursorCat;
	protected STSeries source;

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
	
	abstract protected String getOhlcSeriesID();
	abstract protected String getVolumeSeriesID();
	
	protected void createLayers() {
		if ( priceChart != null ) {
			lyrPriceCursorCat = priceChart.addLayer(new SWBarHighlighter(chartPanel.getCategoryTracker()));
			lyrPrice = priceChart.addLayer(new SWCandlestickLayer(source.getSeries(getOhlcSeriesID())))
					.setColor(SWCandlestickLayer.BEARISH_BODY_COLOR, new Color(127, 64, 64))
					.setColor(SWCandlestickLayer.BEARISH_SHADOW_COLOR, new Color(127, 64, 64))
					.setColor(SWCandlestickLayer.BULLISH_BODY_COLOR, new Color(64, 127, 64))
					.setColor(SWCandlestickLayer.BULLISH_SHADOW_COLOR, new Color(64, 127, 64));
		}
		if ( volChart != null ) {
			lyrVolCursorCat = volChart.addLayer(new SWBarHighlighter(chartPanel.getCategoryTracker()));
			lyrVol = volChart.addHistogram(source.getSeries(getVolumeSeriesID()));
		}
	}
	
	protected void dropLayers() {
		if ( priceChart != null ) {
			if ( lyrPrice != null ) {
				priceChart.dropLayer(lyrPrice.getId());
				lyrPrice = null;
			}
			if ( lyrPriceCursorCat != null ) {
				priceChart.dropLayer(lyrPriceCursorCat.getId());
				lyrPriceCursorCat = null;
			}
		}
		if ( volChart != null ) {
			if ( lyrVol != null ) {
				volChart.dropLayer(lyrVol.getId());
				lyrVol = null;
			}
			if ( lyrVolCursorCat != null ) {
				volChart.dropLayer(lyrVolCursorCat.getId());
				lyrVolCursorCat = null;
			}
		}
	}
	
	protected void createPriceChart() {
		priceChart = chartPanel.addChart("OHLC")
				.setHeight(600);
		ChartSpaceManager vsm = priceChart.getVerticalSpaceManager();
		vsm.getGridLinesSetup("CATEGORY", "TIME").setVisible(true);
		((SWTimeAxisRulerSetup) vsm.getLowerRulerSetup("CATEGORY", "TIME"))
			.setDisplayPriority(20)
			.setShowInnerLine(true)
			.setShowOuterLine(false)
			.setVisible(true);
		((SWTimeAxisRulerSetup) vsm.getUpperRulerSetup("CATEGORY", "TIME"))
			.setDisplayPriority(10)
			.setShowInnerLine(true)
			.setShowOuterLine(false)
			.setVisible(true);
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
		vsm.getGridLinesSetup("CATEGORY", "TIME").setVisible(true);
		((SWTimeAxisRulerSetup) vsm.getLowerRulerSetup("CATEGORY", "TIME"))
			//.setDisplayPriority(20) // hide this first
			//.setShowInnerLine(true)
			//.setShowOuterLine(true)
			.setVisible(false);
		((SWTimeAxisRulerSetup) vsm.getUpperRulerSetup("CATEGORY", "TIME"))
			.setDisplayPriority(10)
			.setShowInnerLine(true)
			.setShowOuterLine(false)
			.setVisible(true);
		// TODO: add date ruler to bottom
		ValueAxisDriver vad = volChart.getValueAxisDriver();
		volValueRulerRenderer = (SWValueAxisRulerRenderer) vad.getRenderer("LABEL");
		volValueRulerRenderer.setTickSize(CDecimalBD.of(1L)); // always 1
		ChartSpaceManager hsm = volChart.getHorizontalSpaceManager();
		hsm.getGridLinesSetup("VALUE", "LABEL").setVisible(true);
		hsm.getLowerRulerSetup("VALUE", "LABEL").setVisible(true);
		hsm.getUpperRulerSetup("VALUE", "LABEL").setVisible(true);
	}
	
	protected void createCharts() {
		createPriceChart();
		createVolumeChart();
	}
	
	public JPanel create() {
		chartPanel = new BarChartPanelImpl(BarChartOrientation.LEFT_TO_RIGHT);
		
		CategoryAxisDriver cad = chartPanel.getCategoryAxisDriver();
		cad.registerRenderer(timeRulerRenderer = new SWTimeAxisRulerRendererV2("TIME"));
		
		createCharts();
		updateViewport();
		
		JPanel chartRoot = new JPanel(new GridLayout(1, 1));
		chartRoot.add(chartPanel.getRootPanel());
		return chartRoot;
	}
	
	protected void updateSource(STSeries source) {
		this.source = source;
		chartPanel.setCategories(source);
		timeRulerRenderer.setCategories(source);
	}
	
	protected void updateSecurity(Security security) {
		if ( priceValueRulerRenderer != null ) {
			priceValueRulerRenderer.setTickSize(security.getTickSize());
		}
	}
	
	public void update(STSeries source, Security security) {
		dropLayers();
		updateSource(source);
		updateSecurity(security);
		createLayers();
		updateViewport();
	}
	
	public void clear() {
		dropLayers();
	}
	
	public void paint() {
		chartPanel.paint();
	}

}
