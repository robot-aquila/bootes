package ru.prolib.bootes.lib.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.difflib.patch.AbstractDelta;

/**
 * Does nothing. Just returns an empty list of deltas. Do not forget skip lines in case of next block presence.
 */
public class STRBComparatorStub implements STRBComparator {
	private final String reportID;
	
	public STRBComparatorStub(String report_id) {
		this.reportID = report_id;
	}
	
	@Override
	public List<AbstractDelta<String>> diff(List<TextLine> expected, List<TextLine> actual) throws IOException {
		return new ArrayList<>();
	}

	@Override
	public String getReportID() {
		return reportID;
	}

}
