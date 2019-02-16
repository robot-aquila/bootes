package ru.prolib.bootes.lib.data.filter;

public interface IFilterSet {
	IFilterSet addFilter(IFilter filter);
	IFilterSet removeFilted(String filterID);
	IFilterSetState checkState();
}
