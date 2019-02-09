package ru.prolib.bootes.lib.report.blockrep.ui;

import java.awt.Graphics2D;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWAbstractLayer;
import ru.prolib.bootes.lib.report.blockrep.IBlockReport;
import ru.prolib.bootes.lib.report.blockrep.IBlockReportStorage;
import ru.prolib.bootes.lib.report.blockrep.ITimeIndexMapper;
import ru.prolib.bootes.lib.report.blockrep.TimeIndexMapperTS;

public class BlockReportLayer extends SWAbstractLayer {
	private final ITimeIndexMapper tim;
	private final IBlockReportStorage storage;
	private final BlockReportRenderer renderer;

	public BlockReportLayer(String layerID,
			IBlockReportStorage storage,
			ITimeIndexMapper tim,
			BlockReportRenderer renderer)
	{
		super(layerID);
		this.storage = storage;
		this.renderer = renderer;
		this.tim = tim;
	}
	
	public BlockReportLayer(String layerID,
			IBlockReportStorage storage,
			TSeries<?> basis,
			BlockReportRenderer renderer)
	{
		this(layerID, storage, new TimeIndexMapperTS(basis), renderer);
	}
	
	public BlockReportLayer(String layerID,
			IBlockReportStorage storage,
			TSeries<?> basis)
	{
		this(layerID, storage, basis, new BlockReportRendererSpdr());
	}
	
	public ITimeIndexMapper getTimeIndexMapper() {
		return tim;
	}
	
	public IBlockReportStorage getStorage() {
		return storage;
	}
	
	public BlockReportRenderer getRenderer() {
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
		
		for ( IBlockReport report : storage.getReports(Interval.of(
				tim.toIntervalStart(cadm.getFirstVisibleCategory()),
				tim.toIntervalEnd(cadm.getLastVisibleCategory())
			)) )
		{
			renderer.paintReport(context, graphics, report, tim);
		}
	}

}
