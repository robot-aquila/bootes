package ru.prolib.bootes.lib.data.ts.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilterSet<ArgType> implements IFilterSet<ArgType> {
	private Map<String, IFilter<ArgType>> filters;
	
	public FilterSet(Map<String, IFilter<ArgType>> filters) {
		this.filters = filters;
	}
	
	public FilterSet() {
		this(new LinkedHashMap<>());
	}

	@Override
	public synchronized IFilterSet<ArgType> addFilter(IFilter<ArgType> filter) {
		String id = filter.getID();
		if ( filters.containsKey(id) ) {
			throw new IllegalArgumentException("Filter already exists: " + id);
		}
		filters.put(filter.getID(), filter);
		return this;
	}

	@Override
	public synchronized IFilterSet<ArgType> removeFilted(String filterID) {
		filters.remove(filterID);
		return this;
	}

	@Override
	public synchronized IFilterSetState approve(ArgType arg) {
		List<IFilterState> states = new ArrayList<>();
		Iterator<Map.Entry<String, IFilter<ArgType>>> it = filters.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<String, IFilter<ArgType>> pair = it.next();
			states.add(new FilterState(pair.getKey(), pair.getValue().approve(arg)));
		}
		return new FilterSetState(states);
	}

}
