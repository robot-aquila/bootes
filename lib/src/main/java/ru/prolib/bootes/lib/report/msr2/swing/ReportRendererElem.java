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
	protected final ReportUtils ru;
	protected final GPrim gprim;
	protected Color fillColor = new Color(127, 255, 212);
	protected Color borderColor = new Color(25, 25, 112); 
	
	public ReportRendererElem(ReportUtils ru, GPrim gprim) {
		this.ru = ru;
		this.gprim = gprim;
	}
	
	public ReportRendererElem() {
		this(ReportUtils.getInstance(), GPrim.getInstance());
	}

	@Override
	public boolean paintReport(BCDisplayContext context,
			Graphics2D device,
			IReport report,
			ITimeIndexMapper tim)
	{
		CategoryAxisDisplayMapper cam = context.getCategoryAxisMapper();
		AxisDirection dir = cam.getAxisDirection();
		if ( dir != AxisDirection.RIGHT ) {
			throw new IllegalStateException("Unsupported direction: " + dir);
		}

		int null_time_index = cam.getLastVisibleCategory();
		Integer i_avg = ru.getAverageIndex(report, tim, null_time_index);
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
		beforeDrawIcon(x, y, context, device, report, tim, null_time_index);
		gprim.drawCircle(device, x, y, 6, fillColor, borderColor);
		return true;
	}
	
	protected void beforeDrawIcon(int iconX,
			int iconY,
			BCDisplayContext context,
			Graphics2D device,
			IReport report,
			ITimeIndexMapper tim,
			int null_time_index)
	{
		
	}

}
