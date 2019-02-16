package ru.prolib.bootes.lib.data.ts.filter;

import ru.prolib.bootes.lib.data.ts.TradeSignal;

public interface IFilterSet {
	IFilterSet addFilter(IFilter filter);
	IFilterSet removeFilted(String filterID);
	IFilterSetState approve(TradeSignal signal);
}
