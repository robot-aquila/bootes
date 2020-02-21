package ru.prolib.bootes.lib.report;

import java.io.IOException;

import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

public class TextLineReaderConditionalStop implements TextLineReader {
	private final TextLineReader reader;
	private final Validator<TextLine> stopCondition;
	private boolean stopped = false;
	
	public TextLineReaderConditionalStop(TextLineReader reader, Validator<TextLine> stop_condition) {
		this.reader = reader;
		this.stopCondition = stop_condition;
	}
	
	/**
	 * Test whether reader has been stopped or not.
	 * <p>
	 * @return true if it was stopped by condition, by closing or by reaching the end of data
	 */
	public boolean stopped() {
		return stopped;
	}

	@Override
	public void close() throws IOException {
		try {
			reader.close();
		} finally {
			stopped = true;
		}
	}

	@Override
	public TextLine readLine() throws IOException {
		if ( stopped ) {
			return null;
		}
		TextLine line = reader.readLine();
		if ( line != null ) {
			try {
				if ( stopCondition.validate(line) ) {
					stopped = true;
					return null;
				}
			} catch ( ValidatorException e ) {
				throw new IOException("Validation failed", e);
			}
		} else {
			stopped = true;
		}
		return line;
	}

	@Override
	public int getNextLineNo() {
		return reader.getNextLineNo();
	}
	
	/**
	 * Read lines until condition met.
	 * <p>
	 * @throws IOException an error occurred
	 */
	public void skipUntilStopped() throws IOException {
		while ( stopped() == false ) {
			readLine();
		}
	}

}
