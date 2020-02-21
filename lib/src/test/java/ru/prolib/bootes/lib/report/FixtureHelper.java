package ru.prolib.bootes.lib.report;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;

public class FixtureHelper {

	static final String LN = System.lineSeparator();
	
	static TextLineReader createReader(String data) {
		return new TextLineReaderBRImpl(new BufferedReader(new StringReader(data)));
	}
	
	static TextLineReader createReader(StringBuilder sb) {
		return createReader(sb.toString());
	}
	
	static StringBuilder fixture_report1(StringBuilder sb) {
		return sb
			.append("# ReportID=SummaryReport_v0.1.0 Title=Summary").append(LN)
			.append("Total net profit:  -185953.59386 RUB").append(LN)
			.append("    Gross profit:  2581053.29428 RUB").append(LN)
			.append("      Gross loss: -2767006.88814 RUB").append(LN)
			.append(LN);
	}
	
	static StringBuilder fixture_report1() {
		return fixture_report1(new StringBuilder());
	}
	
	static StringBuilder fixture_report2_1(StringBuilder sb) {
		return sb
			.append("# ReportID=S3Report_v0.1.0 Title=Trades").append(LN)
			.append("PU: N/A    MU: RUB    TZ: Europe/Moscow").append(LN)
			.append("Seed: 33324").append(LN)
			.append("------------------------------------------------------------------------------").append(LN)
			.append("  id |  type |  ex.time | en.pr. | ex.pr. | qty |     tp |           pl | mins").append(LN)
			.append("------------------------------------------------------------------------------").append(LN)
			.append("   0 | SHORT | 17:01:54 | 118927 | 119113 |  45 | 117607 | -10173.08536 |   26").append(LN)
			.append("   1 |  LONG | 18:30:00 | 119181 | 119714 |  45 | 120501 |  29135.32800 |   69").append(LN)
			.append("   2 |  LONG | 12:25:27 | 117648 | 117640 |  42 | 119138 |   -411.37552 |   90").append(LN)
			.append("------------------------------------------------------------------------------").append(LN)
			.append(LN);
	}
	
	static StringBuilder fixture_report2_1() {
		return fixture_report2_1(new StringBuilder());
	}

	static StringBuilder fixture_report2_2(StringBuilder sb) {
		return sb
			.append("# ReportID=S3Report_v0.1.0 Title=Trades").append(LN)
			.append("PU: N/A    MU: USD    TZ: America/New York").append(LN) // change
			.append("Seed: 2597").append(LN)
			.append("------------------------------------------------------------------------------").append(LN)
			.append("  id |  type |  ex.time | en.pr. | ex.pr. | qty |     tp |           pl | mins").append(LN)
			.append("------------------------------------------------------------------------------").append(LN)
			.append("  -1 |  LONG | 12:00:00 | 100000 | 110000 |  10 |  98000 |    -24.00000 |    1").append(LN) // ins
			.append("   0 | SHORT | 17:01:54 | 118927 | 119113 |  45 | 117607 | -10173.08536 |   26").append(LN)
			.append("   1 |  LONG | 18:30:00 | 119181 | 119714 |  45 | 120501 |  29135.32800 |   69").append(LN) // del
			.append("------------------------------------------------------------------------------").append(LN)
			.append(LN);
	}
	
	static StringBuilder fixture_report2_2() {
		return fixture_report2_2(new StringBuilder());
	}
	
	static StringBuilder fixture_report3(StringBuilder sb) {
		return sb
			.append("# ReportID=EquityReport_v0.1.0 Title=Equity").append(LN)
			.append("open: 1000000.00    MU: RUB    TF: D4[Europe/Moscow]").append(LN)
			.append("---------------------------------------------------").append(LN)
			.append("       time |       high |        low |      close").append(LN)
			.append("---------------------------------------------------").append(LN)
			.append(" 2017-01-01 | 1065810.77 |  988697.92 | 1019125.73").append(LN)
			.append(" 2017-01-05 | 1030518.59 |  933684.28 |  933873.81").append(LN)
			.append(" 2017-01-09 | 1040722.46 |  918015.64 |  985612.70").append(LN)
			.append("---------------------------------------------------").append(LN)
			.append(LN);
	}
	
	static StringBuilder fixture_report_all() {
		StringBuilder sb = new StringBuilder();
		fixture_report1(sb);
		fixture_report2_1(sb);
		fixture_report3(sb);
		return sb;
	}
	
	static List<TextLine> read(TextLineReader reader) throws Exception {
		List<TextLine> text_lines = new ArrayList<>();
		TextLine text_line;
		while ( (text_line = reader.readLine()) != null ) {
			text_lines.add(text_line);
		}
		return text_lines;
	}
	
	static TextLineReader skipLines(TextLineReader reader, int num_to_skip) throws Exception {
		for ( int i = 0; i < num_to_skip; i ++ ) reader.readLine();
		return reader;
	}
	
	static List<String> toStringList(TextLineReader reader) throws Exception {
		List<String> lines = new ArrayList<>();
		for ( TextLine text_line : read(reader) ) {
			lines.add(text_line.getLineText());
		}
		return lines;
	}
	
	static List<String> toStringList(String data) throws Exception {
		return toStringList(createReader(data));
	}
	
	static List<String> toStringList(StringBuilder sb) throws Exception {
		return toStringList(sb.toString());
	}

	static List<AbstractDelta<String>> diff(StringBuilder expected, StringBuilder actual) throws Exception {
		return DiffUtils.diff(toStringList(expected), toStringList(actual)).getDeltas();
	}

}
