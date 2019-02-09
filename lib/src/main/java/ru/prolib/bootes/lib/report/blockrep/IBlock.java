package ru.prolib.bootes.lib.report.blockrep;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public interface IBlock {
	String getTypeID();
	CDecimal getPrice();
	Instant getTime();
}
