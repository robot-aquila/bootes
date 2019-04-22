package ru.prolib.bootes.protos.ui;

import static ru.prolib.bootes.protos.PROTOSSetupT0.*;

import java.awt.Color;
import java.time.ZoneId;

import javax.swing.JPanel;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerSetup;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWBarHighlighter;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.report.blockrep.ui.BlockReportLayer;
import ru.prolib.bootes.lib.ui.SecurityChartPanel;
import ru.prolib.bootes.protos.PROTOSSetupT0;

public class PROTOSChartT0 extends SecurityChartPanel {
	private BarChart atrChart;
	private BarChartLayer lyrMaFast, lyrMaSlow, lyrAtr, lyrAtrCursorCat, lyrBlockrep;
	private SWValueAxisRulerRenderer atrValueRulerRenderer;
	private final IBlockReportStorage report;
	
	public PROTOSChartT0(ZoneId zoneID, IBlockReportStorage report) {
		this.report = report;
	}

	@Override
	protected String getOhlcSeriesID() {
		return PROTOSSetupT0.SID_OHLC;
	}

	@Override
	protected String getVolumeSeriesID() {
		return PROTOSSetupT0.SID_VOLUME;
	}
	
	private TSeries<CDecimal> gs(String seriesID) {
		return source.getSeries(seriesID);
	}
	
	@Override
	protected void createLayers() {
		super.createLayers();
		if ( priceChart != null ) {
			lyrMaFast = priceChart.addSmoothLine(gs(SID_MA_FAST)).setColor(Color.ORANGE);
			lyrMaSlow = priceChart.addSmoothLine(gs(SID_MA_SLOW)).setColor(Color.GREEN);
			lyrBlockrep = priceChart.addLayer(new BlockReportLayer("MS_REPORT", report, source));
		}
		if ( atrChart != null ) {
			lyrAtrCursorCat = atrChart.addLayer(new SWBarHighlighter(chartPanel.getCategoryTracker()));
			lyrAtr = atrChart.addHistogram(gs(SID_ATR)).setColor(new Color(127, 64, 127));
		}
	}
	
	@Override
	protected void dropLayers() {
		if ( lyrMaFast != null ) {
			priceChart.dropLayer(lyrMaFast);
			lyrMaFast = null;
		}
		if ( lyrMaSlow != null ) {
			priceChart.dropLayer(lyrMaSlow);
			lyrMaSlow = null;
		}
		if ( lyrBlockrep != null ) {
			priceChart.dropLayer(lyrBlockrep);
			lyrBlockrep = null;
		}
		
		if ( lyrAtr != null ) {
			atrChart.dropLayer(lyrAtr);
			lyrAtr = null;
		}
		if ( lyrAtrCursorCat != null ) {
			atrChart.dropLayer(lyrAtrCursorCat);
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
		chartPanel.setPreferredNumberOfBars(100);
		return panel;
	}

}
