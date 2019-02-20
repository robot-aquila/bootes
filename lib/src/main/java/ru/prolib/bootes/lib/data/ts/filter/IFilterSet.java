package ru.prolib.bootes.lib.data.ts.filter;

public interface IFilterSet<ArgType> {
	IFilterSet<ArgType> addFilter(IFilter<ArgType> filter);
	IFilterSet<ArgType> removeFilted(String filterID);
	IFilterSetState approve(ArgType arg);
}
