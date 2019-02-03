package ru.prolib.bootes.lib.report.msr2.swing;

import java.awt.Graphics2D;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWAbstractLayer;
import ru.prolib.bootes.lib.report.msr2.IReport;
import ru.prolib.bootes.lib.report.msr2.IReportStorage;
import ru.prolib.bootes.lib.report.msr2.ITimeIndexMapper;
import ru.prolib.bootes.lib.report.msr2.TimeIndexMapperTS;

public class MSR2Layer extends SWAbstractLayer {
	private final ITimeIndexMapper tim;
	private final IReportStorage storage;
	private final ReportRenderer renderer;

	public MSR2Layer(String layerID,
			IReportStorage storage,
			ReportRenderer renderer,
			ITimeIndexMapper tim)
	{
		super(layerID);
		this.storage = storage;
		this.renderer = renderer;
		this.tim = tim;
	}
	
	public MSR2Layer(String layerID,
			IReportStorage storage,
			ReportRenderer renderer,
			TSeries<?> basis)
	{
		this(layerID, storage, renderer, new TimeIndexMapperTS(basis));
	}
	
	public MSR2Layer(String layerID,
			IReportStorage storage,
			TSeries<?> basis)
	{
		this(layerID, storage, new ReportRendererElem(), basis);
	}
	
	public ITimeIndexMapper getTimeIndexMapper() {
		return tim;
	}
	
	public IReportStorage getStorage() {
		return storage;
	}
	
	public ReportRenderer getRenderer() {
		return renderer;
	}

	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		return null;
	}

	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		CategoryAxisDisplayMapper cadm = context.getCategoryAxisMapper();
		AxisDirection dir = cadm.getAxisDirection();
		if ( dir != AxisDirection.RIGHT ) {
			throw new IllegalStateException("Unsupported direction: " + dir);
		}
		
		for ( IReport report : storage.getReports(Interval.of(
				tim.toIntervalStart(cadm.getFirstVisibleCategory()),
				tim.toIntervalEnd(cadm.getLastVisibleCategory())
			)) )
		{
			renderer.paintReport(context, graphics, report, tim);
		}
	}

}
