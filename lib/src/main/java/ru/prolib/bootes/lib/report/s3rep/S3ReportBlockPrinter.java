package ru.prolib.bootes.lib.report.s3rep;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.bootes.lib.report.IReportBlockPrinter;

public class S3ReportBlockPrinter implements IReportBlockPrinter {
	public static final String DEFAULT_TITLE = "Default";
	public static final String REPORT_ID = "S3Report_v0.1.0";
	private final String title;
	private final IS3Report report;
	private final ZoneId zoneID;
	private final DateTimeFormatter tf, dtf;
	
	public S3ReportBlockPrinter(IS3Report report, String title, ZoneId zoneID) {
		this.title = title;
		this.report = report;
		this.zoneID = zoneID;
		this.tf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(zoneID);
		this.dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(zoneID);
	}
	
	public S3ReportBlockPrinter(IS3Report report, ZoneId zoneID) {
		this(report, DEFAULT_TITLE, zoneID);
	}
	
	public IS3Report getReport() {
		return report;
	}
	
	public ZoneId getZoneID() {
		return zoneID;
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
				"id",
				"type",
				"en.time",
				"ex.time",
				"en.pr.",
				"ex.pr.",
				"qty",
				"tp",
				"sl",
				"be",
				"pl",
				"mins"
		}; 
		String rows[][] = null;
		String zero_msec_tpl = ".000";
		String price_unit = null, money_unit = null;
		boolean has_ent_msec = false, has_ext_msec = false;
		synchronized ( report ) {
			int count = report.getRecordCount();
			rows = new String[count][12];
			for ( int i = 0; i < count; i ++ ) {
				S3RRecord rec = report.getRecord(i);
				Instant en_time = rec.getEntryTime(), ex_time = rec.getExitTime();
				String row[] = rows[i];
				row[ 0] = Integer.toString(rec.getID());
				row[ 1] = rec.getType().toString();
				String ent_time = row[ 2] = dtf.format(en_time);
				if ( ! ent_time.endsWith(zero_msec_tpl) ) {
					has_ent_msec = true;
				}
				if ( ex_time != null ) {
					String ext_time = row[3] = tf.format(ex_time);
					if ( ! ext_time.endsWith(zero_msec_tpl) ) {
						has_ext_msec = true;
					}
				} else {
					row[3] = "";
				}
				row[ 4] = toString(rec.getEntryPrice());
				if ( price_unit == null ) {
					price_unit = rec.getEntryPrice().getUnit();
				}
				row[ 5] = toString(rec.getExitPrice());
				row[ 6] = toString(rec.getQty());
				row[ 7] = toString(rec.getTakeProfit());
				row[ 8] = toString(rec.getStopLoss());
				row[ 9] = toString(rec.getBreakEven());
				row[10] = toString(rec.getProfitAndLoss());
				if ( money_unit == null && rec.getProfitAndLoss() != null ) {
					money_unit = rec.getProfitAndLoss().getUnit();
				}
				row[11] = rec.getDurationMinutes() == null
						? "" : rec.getDurationMinutes().toString();
			}
		}
		if ( ! has_ent_msec ) {
			for ( int i = 0; i < rows.length; i ++ ) {
				String x = rows[i][2];
				rows[i][2] = x.substring(0, x.length() - 4);
			}
		}
		if ( ! has_ext_msec ) {
			for ( int i = 0; i < rows.length; i ++ ) {
				String x = rows[i][3];
				if ( x.length() > 0 ) {
					rows[i][3] = x.substring(0, x.length() - 4);
				}
			}
		}
		
		// Determine max width of values in appropriate column
		int max_len[] = new int[12];
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
		total_width += 11;
		String full_line = StringUtils.repeat("-", total_width);
		stream.print("PU: " + (price_unit == null ? "N/A" : price_unit) + "    ");
		stream.print("MU: " + (money_unit == null ? "N/A" : money_unit) + "    ");
		stream.println("TZ: " + zoneID);
		stream.println(full_line);
		for ( int i = 0; i < cols.length; i ++ ) {
			String x = StringUtils.leftPad(cols[i], max_len[i]);
			if ( i < 11 ) {
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
				if ( j < 11 ) {
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
