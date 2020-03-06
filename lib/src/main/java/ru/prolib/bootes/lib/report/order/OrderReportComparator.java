package ru.prolib.bootes.lib.report.order;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.bootes.lib.report.STRBComparatorDumb;

public class OrderReportComparator extends STRBComparatorDumb {
	private final static Pattern p1 = Pattern.compile("^(\\s*#\\d+\\s*)\\|");
	private final static Pattern p2 = Pattern.compile("\\|([^\\|]+)$");

	public OrderReportComparator(String report_id) {
		super(report_id);
	}
	
	static private String maskExecutionNum(String line) {
		Matcher m = p1.matcher(line);
		if ( m.find() ) {
			int pos = m.group(1).length();
			String replacement = StringUtils.repeat("?", pos);
			line = replacement + line.substring(pos);
		}
		return line;
	}
	
	static private String maskExternalID(String line) {
		Matcher m = p2.matcher(line);
		if ( m.find() ) {
			int rep_len = m.group(1).length();
			int pos = line.length() - rep_len;
			String replacement = StringUtils.repeat("?", rep_len);
			line = line.substring(0, pos) + replacement;
		}
		return line;
	}
	
	private List<String> fixLines(List<String> lines) {
		for ( int i = 0; i < lines.size(); i ++ ) {
			lines.set(i, maskExecutionNum(maskExternalID(lines.get(i))));
		}
		return lines;
	}
	
	@Override
	protected List<String> fixExpectedLines(List<String> lines) {
		return fixLines(lines);
	}
	
	@Override
	protected List<String> fixActualLines(List<String> lines) {
		return fixLines(lines);
	}

}
