package ru.prolib.bootes.lib.report;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TextLine {
	private final int lineNo;
	private final String lineText;
	
	public TextLine(int line_no, String line_text) {
		this.lineNo = line_no;
		this.lineText = line_text;
	}
	
	public int getLineNo() {
		return lineNo;
	}
	
	public String getLineText() {
		return lineText;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(66518901, 41)
				.append(lineNo)
				.append(lineText)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TextLine.class ) {
			return false;
		}
		TextLine o = (TextLine) other;
		return new EqualsBuilder()
				.append(o.lineNo, lineNo)
				.append(o.lineText, lineText)
				.build();
	}

}
