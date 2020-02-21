package ru.prolib.bootes.lib.report;

import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

public class ConditionStopAtEmptyLine implements Validator<TextLine> {
	private static final ConditionStopAtEmptyLine instance = new ConditionStopAtEmptyLine();
	
	public static ConditionStopAtEmptyLine getInstance() {
		return instance;
	}

	@Override
	public boolean validate(TextLine line) throws ValidatorException {
		return "".equals(line.getLineText().trim());
	}
	
	@Override
	public int hashCode() {
		return 1886443081;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ConditionStopAtEmptyLine.class ) {
			return false;
		}
		return true;
	}

}
