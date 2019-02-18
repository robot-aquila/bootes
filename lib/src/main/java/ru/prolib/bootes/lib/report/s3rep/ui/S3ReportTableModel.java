package ru.prolib.bootes.lib.report.s3rep.ui;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.bootes.lib.report.s3rep.IS3Report;
import ru.prolib.bootes.lib.report.s3rep.IS3ReportListener;
import ru.prolib.bootes.lib.report.s3rep.S3RRecord;

public class S3ReportTableModel extends AbstractTableModel implements ITableModel, IS3ReportListener {
	private static final long serialVersionUID = 1L;
	public static final int CID_RECORD_ID = 1;
	public static final int CID_RECORD_TYPE = 2;
	public static final int CID_DATE = 3;
	public static final int CID_ENTRY_DATE_TIME = 4;
	public static final int CID_ENTRY_PRICE = 5;
	public static final int CID_QTY = 6;
	public static final int CID_TAKE_PROFIT = 7;
	public static final int CID_STOP_LOSS = 8;
	public static final int CID_BREAK_EVEN = 9;
	public static final int CID_EXIT_TIME = 10;
	public static final int CID_EXIT_PRICE = 11;
	public static final int CID_PROFIT_AND_LOSS = 12;
	
	private final List<Integer> columnIndexToColumnID;
	private final Map<Integer, MsgID> columnIDToColumnHeader;
	private final IMessages messages;
	private final DateTimeFormatter dateFormat, timeFormat, dateTimeFormat;
	private final IS3Report report;
	private boolean subscribed = false;
	
	public S3ReportTableModel(IMessages messages,
			DateTimeFormatter dateFormat,
			DateTimeFormatter timeFormat,
			DateTimeFormatter dateTimeFormat,
			IS3Report report)
	{
		columnIndexToColumnID = getColumnIDList();
		columnIDToColumnHeader = getColumnIDToHeaderMap();
		this.messages = messages;
		this.dateFormat = dateFormat;
		this.timeFormat = timeFormat;
		this.dateTimeFormat = dateTimeFormat;
		this.report = report;
	}
	
	public S3ReportTableModel(IMessages messages,
			ZoneId zoneID,
			IS3Report report)
	{
		this(messages,
			DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(zoneID),
			DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(zoneID),
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(zoneID),
			report);
	}
	
	/**
	 * Get list of columns to display.
	 * <p>
	 * Not all of attributes may displayed by default.
	 * Override this method to add or modify column list. 
	 * <p>
	 * @return list of columns
	 */
	protected List<Integer> getColumnIDList() {
		List<Integer> cols = new ArrayList<>();
		cols.add(CID_RECORD_ID);
		cols.add(CID_RECORD_TYPE);
		cols.add(CID_ENTRY_DATE_TIME);
		cols.add(CID_EXIT_TIME);
		cols.add(CID_ENTRY_PRICE);
		cols.add(CID_EXIT_PRICE);
		cols.add(CID_QTY);
		cols.add(CID_TAKE_PROFIT);
		cols.add(CID_STOP_LOSS);
		cols.add(CID_BREAK_EVEN);
		cols.add(CID_PROFIT_AND_LOSS);
		return cols;
	}
	
	/**
	 * Get map of columns mapped to its titles.
	 * <p>
	 * Override this method to add or modify column titles.
	 * <p>
	 * @return map of column titles
	 */
	protected Map<Integer, MsgID> getColumnIDToHeaderMap() {
		Map<Integer, MsgID> head = new HashMap<>();
		head.put(CID_RECORD_ID, S3ReportMsg.RECORD_ID);
		head.put(CID_RECORD_TYPE, S3ReportMsg.RECORD_TYPE);
		head.put(CID_DATE, S3ReportMsg.DATE);
		head.put(CID_ENTRY_DATE_TIME, S3ReportMsg.ENTRY_DATE_TIME);
		head.put(CID_ENTRY_PRICE, S3ReportMsg.ENTRY_PRICE);
		head.put(CID_QTY, S3ReportMsg.QTY);
		head.put(CID_TAKE_PROFIT, S3ReportMsg.TAKE_PROFIT);
		head.put(CID_STOP_LOSS, S3ReportMsg.STOP_LOSS);
		head.put(CID_BREAK_EVEN, S3ReportMsg.BREAK_EVEN);
		head.put(CID_EXIT_TIME, S3ReportMsg.EXIT_TIME);
		head.put(CID_EXIT_PRICE, S3ReportMsg.EXIT_PRICE);
		head.put(CID_PROFIT_AND_LOSS, S3ReportMsg.PROFIT_AND_LOSS);
		return head;
	}

	@Override
	public int getColumnCount() {
		return columnIndexToColumnID.size();
	}

	@Override
	public int getRowCount() {
		return report.getRecordCount();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if ( row >= getRowCount() ) {
			return null;
		}
		return getColumnValue(report.getRecord(row), getColumnID(col));
	}
	
	protected Object getColumnValue(S3RRecord rec, int columnID) {
		switch ( columnID ) {
		case CID_RECORD_ID:
			return rec.getID();
		case CID_RECORD_TYPE:
			return rec.getType();
		case CID_DATE:
			return dateFormat.format(rec.getEntryTime());
		case CID_ENTRY_DATE_TIME:
			return dateTimeFormat.format(rec.getEntryTime());
		case CID_ENTRY_PRICE:
			return rec.getEntryPrice();
		case CID_QTY:
			return rec.getQty();
		case CID_TAKE_PROFIT:
			return rec.getTakeProfit();
		case CID_STOP_LOSS:
			return rec.getStopLoss();
		case CID_BREAK_EVEN:
			return rec.getBreakEven();
		case CID_EXIT_TIME:
			return rec.getExitTime() == null ? null : timeFormat.format(rec.getExitTime());
		case CID_EXIT_PRICE:
			return rec.getExitPrice();
		case CID_PROFIT_AND_LOSS:
			return rec.getProfitAndLoss();
		default:
			return null;
		}
	}

	@Override
	public int getColumnIndex(int columnID) {
		return columnIndexToColumnID.indexOf(columnID);
	}

	@Override
	public int getColumnID(int columnIndex) {
		return columnIndexToColumnID.get(columnIndex);
	}
	
	@Override
	public String getColumnName(int col) {
		MsgID id = columnIDToColumnHeader.get(columnIndexToColumnID.get(col));
		if ( id == null ) {
			return "NULL_ID#" + col; 
		}
		return messages.get(id);
	}

	@Override
	public void close() {
		
	}

	@Override
	public void startListeningUpdates() {
		if ( subscribed ) {
			return;
		}
		report.addListener(this);
		subscribed = true;
	}

	@Override
	public void stopListeningUpdates() {
		if ( ! subscribed ) {
			return;
		}
		report.removeListener(this);
		subscribed = false;
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch ( getColumnID(col) ) {
		case CID_ENTRY_PRICE:
		case CID_QTY:
		case CID_TAKE_PROFIT:
		case CID_STOP_LOSS:
		case CID_BREAK_EVEN:
		case CID_EXIT_PRICE:
		case CID_PROFIT_AND_LOSS:
			return CDecimal.class;
		default:
			return super.getColumnClass(col);
		}
	}

	@Override
	public void recordCreated(S3RRecord record) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				fireTableRowsInserted(record.getID(), record.getID());
			}
		});
	}

	@Override
	public void recordUpdated(S3RRecord record) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				fireTableRowsUpdated(record.getID(), record.getID());
			}
		});
	}

}
