package ru.prolib.bootes.lib.report.msr2;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public interface Block {
	String getTypeID();
	CDecimal getPrice();
	Instant getTime();
}
