package ru.prolib.bootes.lib.report;

import java.io.IOException;
import java.util.List;

import com.github.difflib.patch.AbstractDelta;

public interface STRBComparator {
	
	String getReportID();

	/**
	 * Get differences between two reports.
	 * <p>
	 * @param expected - expected report lines
	 * @param actual - actual report lines
	 * @return list of differences (may be empty if no differencies)
	 * @throws IOExceptio an error occurred
	 */
	List<AbstractDelta<String>> diff(List<TextLine> expected, List<TextLine> actual) throws IOException;

}
