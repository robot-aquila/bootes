package ru.prolib.bootes.lib.report.blockrep.ui;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.bootes.lib.report.blockrep.IBlock;
import ru.prolib.bootes.lib.report.blockrep.IBlockReport;
import ru.prolib.bootes.lib.report.blockrep.ITimeIndexMapper;
import ru.prolib.bootes.lib.report.blockrep.BlockReportUtils;
import ru.prolib.bootes.lib.ui.swing.GPrim;

public class BlockReportRendererSpdr extends BlockReportRendererElem {
	
	public BlockReportRendererSpdr(BlockReportUtils ru, GPrim gprim) {
		super(ru, gprim);
	}
	
	public BlockReportRendererSpdr() {
		super();
	}

	@Override
	protected void beforeDrawIcon(int iconX,
			int iconY,
			BCDisplayContext context,
			Graphics2D device,
			IBlockReport report,
			ITimeIndexMapper tim,
			int null_time_index)
	{
		CategoryAxisDisplayMapper cam = context.getCategoryAxisMapper();
		ValueAxisDisplayMapper vam = context.getValueAxisMapper();
		int index_min = cam.getFirstVisibleCategory(), index_max = cam.getLastVisibleCategory();
		CDecimal price_min = vam.getMinValue(), price_max = vam.getMaxValue();
		for ( IBlock block : report.getBlocks() ) {
			CDecimal price = block.getPrice();
			Instant time = block.getTime();
			if ( price == null ) {
				continue;
			}
			
			int index = time == null ? null_time_index : tim.toIndex(time);
			// Some blocks may be out of display area
			// But wee need to mark them all
			index = Math.max(index_min, index);
			index = Math.min(index_max, index);
			price = price.max(price_min);
			price = price.min(price_max);

			int x = cam.toDisplay(index).getMidpoint();
			int y = vam.toDisplay(price);
			device.setStroke(new BasicStroke(2));
			device.setColor(borderColor);
			device.drawLine(x, y, iconX, iconY);

			gprim.drawCircle(device, x, y, 4, fillColor, borderColor);
		}
	}

}
