package ru.prolib.bootes.lib.report;

import java.io.IOException;
import java.text.ParseException;

public interface STRComparator {
	STRCmpResult diff(TextLineReader expected, TextLineReader actual) throws IOException, ParseException;
}
