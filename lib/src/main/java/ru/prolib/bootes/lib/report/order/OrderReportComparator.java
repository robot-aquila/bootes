package ru.prolib.bootes.lib.report.order;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.bootes.lib.report.STRBComparatorDumb;

public class OrderReportComparator extends STRBComparatorDumb {
	private final static Pattern p1 = Pattern.compile("^(\\s*#\\d+\\s*)\\|");

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
	
	static int getLengthOfLastCol(List<String> lines) {
		String[] chunks = StringUtils.split(lines.get(1), "|");
		if ( chunks.length < 2 ) {
			throw new IllegalArgumentException("Unexpected format");
		}
		return chunks[chunks.length - 1].length();
	}
	
	static private String stripExternalID(String line, int tail_length) {
		int pos = line.length() - tail_length;
		line = line.substring(0, pos);
		return line;
	}
	
	private List<String> fixLines(List<String> lines) {
		lines = new ArrayList<>(lines);
		int last_len = getLengthOfLastCol(lines);
		for ( int i = 0; i < lines.size(); i ++ ) {
			lines.set(i, maskExecutionNum(stripExternalID(lines.get(i), last_len)));
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
