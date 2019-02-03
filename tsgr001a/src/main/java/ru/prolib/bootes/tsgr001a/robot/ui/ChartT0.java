package ru.prolib.bootes.tsgr001a.robot.ui;

import java.awt.Color;
import java.time.ZoneId;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerSetup;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWBarHighlighter;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWSimpleTextOverlay;
import ru.prolib.bootes.lib.report.msr2.IReportStorage;
import ru.prolib.bootes.lib.report.msr2.swing.MSR2Layer;
import ru.prolib.bootes.lib.ui.IndicatorChartTitleOverlay;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.tsgr001a.robot.SetupT0;

public class ChartT0 extends SecurityChartPanel {
	private final ZoneId zoneID;
	private final IReportStorage reportStorage;
	private BarChart atrChart;
	private BarChartLayer lyrEma, lyrAtr, lyrAtrCursorCat, lyrPriceTitle,
		lyrAtrTitle, lyrPvc, lyrMsr2;
	private SWValueAxisRulerRenderer atrValueRulerRenderer;
	
	public ChartT0(ZoneId zoneID, IReportStorage reportStorage) {
		this.zoneID = zoneID;
		this.reportStorage = reportStorage;
	}

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
			lyrEma = priceChart.addSmoothLine(source.getSeries(SetupT0.SID_EMA)).setColor(new Color(64, 64, 127));
			lyrPriceTitle = priceChart.addLayer(new SWSimpleTextOverlay(new PriceChartTitleOverlayWithEMA(
					security.getDisplayName(),
					zoneID,
					source.getSeries(SetupT0.SID_OHLC),
					source.getSeries(SetupT0.SID_EMA),
					chartPanel.getCategoryTracker()
				)
			));
			lyrPvc = priceChart.addPolyLine(source.getSeries(SetupT0.SID_PVC_WAVG)).setColor(Color.ORANGE);
			lyrMsr2 = priceChart.addLayer(new MSR2Layer("MS_REPORT", reportStorage, source));
		}
		if ( atrChart != null ) {
			lyrAtrCursorCat = atrChart.addLayer(new SWBarHighlighter(chartPanel.getCategoryTracker()));
			lyrAtr = atrChart.addHistogram(source.getSeries(SetupT0.SID_ATR)).setColor(new Color(127, 64, 127));
			lyrAtrTitle = atrChart.addLayer(new SWSimpleTextOverlay(new IndicatorChartTitleOverlay(
					security.getDisplayName() + " ATR",
					zoneID,
					source.getSeries(SetupT0.SID_ATR),
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
		if ( lyrPvc != null ) {
			priceChart.dropLayer(lyrPvc.getId());
			lyrPvc = null;
		}
		if ( lyrMsr2 != null ) {
			priceChart.dropLayer(lyrMsr2.getId());
			lyrMsr2 = null;
		}
		if ( lyrAtr != null ) {
			atrChart.dropLayer(lyrAtr.getId());
			lyrAtr = null;
		}
		if ( lyrAtrCursorCat != null ) {
			atrChart.dropLayer(lyrAtrCursorCat.getId());
			lyrAtrCursorCat = null;
		}
		if ( lyrAtrTitle != null ) {
			atrChart.dropLayer(lyrAtrTitle.getId());
			lyrAtrTitle = null;
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
	protected void updateViewport(CategoryAxisViewport viewport) {
		super.updateViewport(viewport);
		viewport.setPreferredNumberOfBars(252); // 1.5 days=14+7=21h=252*M5
	}
	
}
