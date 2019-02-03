package ru.prolib.bootes.lib.report.msr2.swing;

import java.awt.Color;
import java.awt.Graphics2D;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.bootes.lib.report.msr2.IReport;
import ru.prolib.bootes.lib.report.msr2.ReportUtils;
import ru.prolib.bootes.lib.ui.swing.GPrim;
import ru.prolib.bootes.lib.report.msr2.ITimeIndexMapper;

public class ReportRendererElem implements ReportRenderer {
	private final ReportUtils ru;
	private final GPrim gprim;
	
	public ReportRendererElem(ReportUtils ru, GPrim gprim) {
		this.ru = ru;
		this.gprim = gprim;
	}
	
	public ReportRendererElem() {
		this(ReportUtils.getInstance(), GPrim.getInstance());
	}

	@Override
	public void paintReport(BCDisplayContext context,
			Graphics2D graphics,
			IReport report,
			ITimeIndexMapper tim)
	{
		CategoryAxisDisplayMapper cam = context.getCategoryAxisMapper();
		AxisDirection dir = cam.getAxisDirection();
		if ( dir != AxisDirection.RIGHT ) {
			throw new IllegalStateException("Unsupported direction: " + dir);
		}

		Integer i_avg = ru.getAverageIndex(report, tim);
		if ( i_avg == null ) {
			i_avg = cam.getNumberOfVisibleCategories() / 2 + cam.getFirstVisibleCategory();
		} else {
			i_avg = Math.min(i_avg, cam.getLastVisibleCategory());
			i_avg = Math.max(i_avg, cam.getFirstVisibleCategory());
		}

		ValueAxisDisplayMapper vam = context.getValueAxisMapper();
		CDecimal p_avg = ru.getAveragePrice(report);
		if ( p_avg == null ) {
			p_avg = vam.getMaxValue().subtract(vam.getMinValue()).divide(2L).add(vam.getMinValue());
		} else {
			p_avg = p_avg.min(vam.getMaxValue());
			p_avg = p_avg.max(vam.getMinValue());			
		}
		
		int x = cam.toDisplay(i_avg).getMidpoint();
		int y = vam.toDisplay(p_avg);
		gprim.drawCircle(graphics, x, y, 6, Color.ORANGE, Color.BLACK);
	}

}
