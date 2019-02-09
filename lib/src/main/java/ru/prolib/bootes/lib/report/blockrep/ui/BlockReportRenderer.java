package ru.prolib.bootes.lib.report.blockrep.ui;

import java.awt.Graphics2D;

import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.bootes.lib.report.blockrep.IBlockReport;
import ru.prolib.bootes.lib.report.blockrep.ITimeIndexMapper;

public interface BlockReportRenderer {
	
	/**
	 * Paint report on screen.
	 * <p>
	 * @param context - display context
	 * @param graphics - graphics device
	 * @param report - report to display
	 * @param tim - time index mapper
	 * @return true if report shown, false - if painting was skipped due some reasons
	 */
	boolean paintReport(BCDisplayContext context,
			Graphics2D graphics,
			IBlockReport report,
			ITimeIndexMapper tim);
	
}
