package ru.prolib.bootes.lib.report.equirep;

import java.io.PrintStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.OHLCScalableSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.bootes.lib.report.IReportBlockPrinter;

public class EquityReportBlockPrinter implements IReportBlockPrinter {
	public static final String DEFAULT_TITLE = "Default";
	public static final String REPORT_ID = "EquityReport_v0.1.0";
	private final String title;
	private final OHLCScalableSeries report;
	private final DateTimeFormatter daysFormat, hoursFormat, minutesFormat;
	
	public EquityReportBlockPrinter(OHLCScalableSeries report, String title) {
		this.title = title;
		this.report = report;
		ZoneId zone_id = report.getTimeFrame().getZoneID();
		daysFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(zone_id);
		hoursFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH'h'").withZone(zone_id);
		minutesFormat = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm").withZone(zone_id);
	}
	
	public EquityReportBlockPrinter(OHLCScalableSeries report) {
		this(report, DEFAULT_TITLE);
	}
	
	public OHLCScalableSeries getReport() {
		return report;
	}

	@Override
	public String getReportID() {
		return REPORT_ID;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void print(PrintStream stream) {
		// Columns:
		String cols[] = {
				"time",
				"high",
				"low",
				"close"
		};
		String rows[][] = null;
		String money_unit = null;
		Candle rec = null;
		DateTimeFormatter time_format = minutesFormat;
		String open = null;
		ZTFrame tf = null;
		report.lock();
		try {
			int count = report.getLength();
			tf = report.getTimeFrame();
			rows = new String[count][4];
			if ( tf.getUnit() == ChronoUnit.DAYS ) {
				time_format = daysFormat;
			} else if ( tf.getUnit() == ChronoUnit.HOURS ) {
				time_format = hoursFormat;
			}
			for ( int i = 0; i < count; i ++ ) {
				try {
					rec = report.get(i);
				} catch ( ValueException e ) {
					throw new IllegalStateException("Unexpected exception", e);
				}
				String row[] = rows[i];
				row[0] = time_format.format(rec.getStartTime());
				row[1] = toString(rec.getHigh());
				row[2] = toString(rec.getLow());
				row[3] = toString(rec.getClose());
				if ( i == 0 ) {
					open = toString(rec.getOpen());
					money_unit = rec.getOpen().getUnit();
				}
			}
		} finally {
			report.unlock();
		}
		
		// Determine max width of values in appropriate column
		int max_len[] = new int[4];
		for ( int i = 0; i < cols.length; i ++ ) {
			max_len[i] = cols[i].length();
		}
		for ( int i = 0; i < rows.length; i ++ ) {
			for ( int j = 0; j < max_len.length; j ++ ) {
				max_len[j] = Math.max(max_len[j], rows[i][j].length());
			}
		}
		
		int total_width = 0;
		for ( int i = 0; i < max_len.length; i ++ ) {
			total_width += max_len[i] + 2;
		}
		total_width += 3;
		String full_line = StringUtils.repeat("-", total_width);
		stream.print("open: " + open + "    ");
		stream.print("MU: " + (money_unit == null ? "N/A" : money_unit) + "    ");
		stream.println("TF: " + tf);
		stream.println(full_line);
		int last_col = cols.length - 1;
		for ( int i = 0; i < cols.length; i ++ ) {
			String x = StringUtils.leftPad(cols[i], max_len[i]);
			if ( i < last_col ) {
				stream.print(" " + x + " |");
			} else {
				stream.println(" " + x + " ");
			}
		}
		stream.println(full_line);
		for ( int i = 0; i < rows.length; i ++ ) {
			String row[] = rows[i];
			for ( int j = 0; j < cols.length; j ++ ) {
				String x = StringUtils.leftPad(row[j], max_len[j]);
				if ( j < last_col ) {
					stream.print(" " + x + " |");
				} else {
					stream.println(" " + x + " ");
				}
			}
		}
		stream.println(full_line);
		
	}
	
	private String toString(CDecimal val) {
		return val == null ? "" : val.toAbstract().toString();
	}

}
