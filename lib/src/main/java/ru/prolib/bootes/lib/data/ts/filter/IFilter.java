package ru.prolib.bootes.lib.data.ts.filter;

import ru.prolib.bootes.lib.data.ts.TradeSignal;

public interface IFilter {
	String getID();
	boolean approve(TradeSignal signal);
}
