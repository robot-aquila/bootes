package ru.prolib.bootes.protos.ui;

import java.awt.Color;
import java.time.ZoneId;

import javax.swing.JPanel;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerSetup;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWBarHighlighter;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.protos.PROTOSSetupT1;

public class PROTOSChartT1 extends SecurityChartPanel {
	private BarChart atrChart;
	private BarChartLayer lyrAtr, lyrAtrCursorCat;
	private SWValueAxisRulerRenderer atrValueRulerRenderer;
	
	public PROTOSChartT1(ZoneId zoneID) {
		
	}

	@Override
	protected String getOhlcSeriesID() {
		return PROTOSSetupT1.SID_OHLC;
	}

	@Override
	protected String getVolumeSeriesID() {
		return PROTOSSetupT1.SID_VOLUME;
	}
	
	@Override
	protected void createLayers() {
		super.createLayers();
		
		if ( atrChart != null ) {
			lyrAtrCursorCat = atrChart.addLayer(new SWBarHighlighter(chartPanel.getCategoryTracker()));
			lyrAtr = atrChart.addHistogram(source.getSeries(PROTOSSetupT1.SID_ATR)).setColor(new Color(127, 64, 127));
		}
	}
	
	@Override
	protected void dropLayers() {
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
		ChartSpaceManager vsm = atrChart.getVerticalSpaceManager();
		vsm.getGridLinesSetup("CATEGORY", "TIME").setVisible(true);
		((SWTimeAxisRulerSetup) vsm.getLowerRulerSetup("CATEGORY", "TIME"))
			.setVisible(false);
		((SWTimeAxisRulerSetup) vsm.getUpperRulerSetup("CATEGORY", "TIME"))
			.setDisplayPriority(10)
			.setShowInnerLine(true)
			.setShowOuterLine(false)
			.setVisible(true);
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
	public JPanel create() {
		JPanel panel = super.create();
		chartPanel.setPreferredNumberOfBars(30);
		return panel;
	}

}
