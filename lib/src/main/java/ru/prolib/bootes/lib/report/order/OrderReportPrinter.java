package ru.prolib.bootes.lib.report.order;

import java.io.PrintStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.bootes.lib.report.IReportBlockPrinter;

public class OrderReportPrinter implements IReportBlockPrinter {
	public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Moscow");
	public static final String DEFAULT_TITLE = "Default";
	public static final String REPORT_ID = "OrderReport_v0.1.0";
	
	private static final String MKT_PRICE = "MKT";
	private static final String NOT_AVAILABLE = "N/A";
	private static final String EMPTY = "";
	private final ZoneId zoneID;
	private final String title;
	private final OrderReport report;
	private final DateTimeFormatter timeFormat;
	
	public OrderReportPrinter(OrderReport report, ZoneId zone_id, String title) {
		this.report = report;
		this.title = title;
		this.zoneID = zone_id;
		this.timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(zone_id);
	}
	
	public OrderReportPrinter(OrderReport report, ZoneId zone_id) {
		this(report, zone_id, DEFAULT_TITLE);
	}
	
	public OrderReportPrinter(OrderReport report, String title) {
		this(report, DEFAULT_ZONE_ID, title);
	}
	
	public OrderReportPrinter(OrderReport report) {
		this(report, DEFAULT_ZONE_ID, DEFAULT_TITLE);
	}
	
	public OrderReport getReport() {
		return report;
	}
	
	public ZoneId getZoneId() {
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
	
	static String toString(String value) {
		return value == null ? NOT_AVAILABLE : value;
	}
	
	static String toString(OrderAction action) {
		return action.toString();
	}
	
	static String toString(Symbol symbol) {
		return symbol.toString();
	}
	
	static String toStringPrice(CDecimal value) {
		return value == null ? MKT_PRICE : value.toString();
	}
	
	static String toString(CDecimal value) {
		return value == null ? NOT_AVAILABLE : value.toString();
	}
	
	static void maxColWidths(String cols[], int col_width[]) {
		for ( int col = 0; col < Math.min(cols.length, col_width.length); col ++ ) {
			if ( cols[col] != null ) {
				col_width[col] = Math.max(col_width[col], cols[col].length());
			}
		}
	}
	
	static String formLine(String row_data[], int col_width[]) {
		int cols = row_data.length;
		int last_col = cols - 1;
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < cols; i ++ ) {
			String x = StringUtils.leftPad(row_data[i],col_width[i]);
			sb.append(" " + x + " ");
			if ( i < last_col ) {
				sb.append(i < 2 ? " " : "|");
			}
		}
		return sb.toString();
	}

	@Override
	public void print(PrintStream stream) {
		String cols[] = {
				"Action",
				"Symbol",
				"Num/#",
				"Time",
				"Price",
				"Qty",
				"Value",
				"Ext.ID"
		};
		int col_width[] = new int[cols.length];
		maxColWidths(cols, col_width);
		
		Collection<OrderInfo> orders = report.getOrders();
		List<String[]> rows = new ArrayList<>();
		LinkedList<Integer> separator_positions = new LinkedList<>();
		for ( OrderInfo order_info : orders ) {
			separator_positions.addLast(rows.size());
			String h_row[] = {
					toString(order_info.getAction()),
					toString(order_info.getSymbol()),
					Long.toString(order_info.getNum()),
					timeFormat.format(order_info.getTime()),
					toStringPrice(order_info.getPrice()),
					toString(order_info.getQty()),
					toString(order_info.getValue()),
					toString(order_info.getExternalID())
			};
			rows.add(h_row);
			maxColWidths(h_row, col_width);
			for ( OrderExecInfo exec_info : order_info.getExecutions() ) {
				String e_row[] = {
					EMPTY,
					EMPTY,
					"#" + Long.toString(exec_info.getNum()),
					timeFormat.format(exec_info.getTime()),
					toString(exec_info.getPrice()),
					toString(exec_info.getQty()),
					toString(exec_info.getValue()),
					toString(exec_info.getExternalID())
				};
				rows.add(e_row);
				maxColWidths(e_row, col_width);
			}
		}
		int total_width = cols.length - 1;
		for ( int col = 0; col < col_width.length; col ++ ) {
			total_width += col_width[col] + 2;
		}
		String full_line = StringUtils.repeat("-", total_width);
		stream.println(full_line);
		stream.println(formLine(cols, col_width));
		Integer separator_line = separator_positions.pollFirst();
		for ( int row = 0; row < rows.size(); row ++ ) {
			if ( separator_line != null && row == separator_line ) {
				stream.println(full_line);
				separator_line = separator_positions.pollFirst();
			}
			stream.println(formLine(rows.get(row), col_width));
		}
		stream.println(full_line);
	}

}
