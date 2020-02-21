package ru.prolib.bootes.lib.report;

import java.text.ParseException;

public class NoBlocksFoundException extends ParseException {
	private static final long serialVersionUID = -8407525604661263224L;

	public NoBlocksFoundException(String s, int errorOffset) {
		super(s, errorOffset);
	}

}
