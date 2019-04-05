package ru.prolib.bootes.lib.report.summarep;

import java.io.PrintStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.bootes.lib.report.IReportBlockPrinter;

public class SummaryReportBlockPrinter implements IReportBlockPrinter {
	
	static String toString(CDecimal value) {
		return value == null ? "N/A" : value.toString();
	}
	
	static class Entry {
		private final int groupID;
		private final String title, value, optional;
		
		public Entry(int groupID, String title, String value, String optional) {
			this.groupID = groupID;
			this.title = title;
			this.value = value;
			this.optional = optional;
		}
		
		public Entry(int groupID, String title, int value) {
			this(groupID, title, Integer.toString(value), null);
		}
		
		public Entry(int groupID, String title, CDecimal value) {
			this(groupID, title, SummaryReportBlockPrinter.toString(value), null);
		}
		
		public Entry(int groupID, String title, SRTradeSSI x) {
			this(groupID,
				 title,
				 SummaryReportBlockPrinter.toString(x.getPnL()),
				 Integer.toString(x.getCount())
				);
		}
		
	}
	
	public static final String DEFAULT_TITLE = "Default";
	public static final String REPORT_ID = "SummaryReport_v0.1.0";
	private final String title;
	private final ISummaryReport report;
	private final ZoneId zoneID;
	
	public SummaryReportBlockPrinter(ISummaryReport report,
									 String title,
									 ZoneId zoneID)
	{
		this.title = title;
		this.report = report;
		this.zoneID = zoneID;
	}
	
	public SummaryReportBlockPrinter(ISummaryReport report, ZoneId zoneID) {
		this(report, DEFAULT_TITLE, zoneID);
	}
	
	public ISummaryReport getReport() {
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
		List<Entry> entries = new ArrayList<>();
		entries.add(new Entry(0, "Total net profit", report.getTotalNetProfit()));
		entries.add(new Entry(0, "Gross profit", report.getGrossProfit()));
		entries.add(new Entry(0, "Gross loss", report.getGrossLoss()));
		entries.add(new Entry(1, "Profit factor", report.getProfitFactor()));
		entries.add(new Entry(0, "Expected payoff", report.getExpectedPayoff()));
		entries.add(new Entry(0, "Absolute drawdown", report.getAbsoluteDrawdown()));
		entries.add(new Entry(0, "Maximal drawdown", report.getMaximalDrawdown()));
		entries.add(new Entry(2, "Total trades", report.getTotalTrades()));
		entries.add(new Entry(2, "Short positions", report.getShortPositions()));
		entries.add(new Entry(2, "Short winners", report.getWinningShortPositions()));
		entries.add(new Entry(2, "Long positions", report.getLongPositions()));
		entries.add(new Entry(2, "Long winners", report.getWinningLongPositions()));
		entries.add(new Entry(2, "Profit trades", report.getProfitTrades()));
		entries.add(new Entry(2, "Lost trades", report.getLossTrades()));
		entries.add(new Entry(0, "Largest profit trade", report.getLargestProfitTrade()));
		entries.add(new Entry(0, "Largest loss trade", report.getLargestLossTrade()));
		entries.add(new Entry(0, "Average profit trade", report.getAverageProfitTrade()));
		entries.add(new Entry(0, "Average loss trade", report.getAverageLossTrade()));
		entries.add(new Entry(3, "Max.cons.wins/cnt", report.getMaximumConsecutiveWins()));
		entries.add(new Entry(3, "Max.cons.losses/cnt", report.getMaximumConsecutiveLosses()));
		entries.add(new Entry(3, "Mxml.cons.profit/cnt", report.getMaximalConsecutiveProfit()));
		entries.add(new Entry(3, "Mxml.cons.loss/cnt", report.getMaximalConsecutiveLoss()));
		entries.add(new Entry(2, "Avg.cons.wins", report.getAverageConsecutiveWins()));
		entries.add(new Entry(2, "Avg.cons.losses", report.getAverageConsecutiveLosses()));
		
		int max_len_group[] = new int[4];
		int max_len_title = 0;
		for ( int i = 0; i < entries.size(); i ++ ) {
			Entry e = entries.get(i);
			max_len_title = Math.max(max_len_title, e.title.length());
			max_len_group[e.groupID] = Math.max(max_len_group[e.groupID], e.value.length());
		}
		for ( int i = 0; i < entries.size(); i ++ ) {
			Entry e = entries.get(i);
			StringBuilder sb = new StringBuilder()
					.append(StringUtils.leftPad(e.title, max_len_title))
					.append(": ")
					.append(StringUtils.leftPad(e.value, max_len_group[e.groupID]));
			if ( e.optional != null ) {
				sb.append("/").append(e.optional);
			}
			stream.println(sb.toString());
		}
	}

}
