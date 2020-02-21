package ru.prolib.bootes.lib.report;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Text line reader implementation based on {@link java.io.BufferedReader}
 */
public class TextLineReaderBRImpl implements TextLineReader {
	private final BufferedReader reader;
	private int lineNo;
	
	public TextLineReaderBRImpl(BufferedReader reader, int start_line_no) {
		this.reader = reader;
		this.lineNo = start_line_no;
	}
	
	public TextLineReaderBRImpl(BufferedReader reader) {
		this(reader, 0);
	}
	
	public BufferedReader getReader() {
		return reader;
	}
	
	@Override
	public int getNextLineNo() {
		return lineNo;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public TextLine readLine() throws IOException {
		String line_text = reader.readLine();
		return line_text == null ? null : new TextLine(lineNo ++, line_text);
	}

}
