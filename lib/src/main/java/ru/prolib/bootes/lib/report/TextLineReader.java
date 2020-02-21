package ru.prolib.bootes.lib.report;

import java.io.Closeable;
import java.io.IOException;

public interface TextLineReader extends Closeable {

	/**
	 * Read next line.
	 * <p>
	 * @return line or null if no more lines
	 * @throws IOException if an error occurred
	 */
	TextLine readLine() throws IOException;
	
	/**
	 * Get next line number.
	 * <p>
	 * @return line number
	 */
	int getNextLineNo();

}
