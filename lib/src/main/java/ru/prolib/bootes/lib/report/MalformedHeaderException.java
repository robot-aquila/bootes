package ru.prolib.bootes.lib.report;

import java.text.ParseException;

public class MalformedHeaderException extends ParseException {
	private static final long serialVersionUID = 4856570853984559304L;

	public MalformedHeaderException(String s, int errorOffset) {
		super(s, errorOffset);
	}

}
