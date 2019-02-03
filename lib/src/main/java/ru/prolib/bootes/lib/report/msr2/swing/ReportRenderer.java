package ru.prolib.bootes.lib.report.msr2.swing;

import java.awt.Graphics2D;

import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.bootes.lib.report.msr2.IReport;
import ru.prolib.bootes.lib.report.msr2.ITimeIndexMapper;

public interface ReportRenderer {
	void paintReport(BCDisplayContext context,
			Graphics2D graphics,
			IReport report,
			ITimeIndexMapper tim);
}
