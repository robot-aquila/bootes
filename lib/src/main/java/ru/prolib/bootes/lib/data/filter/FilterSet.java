package ru.prolib.bootes.lib.data.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilterSet implements IFilterSet {
	private Map<String, IFilter> filters;
	
	public FilterSet(Map<String, IFilter> filters) {
		this.filters = filters;
	}
	
	public FilterSet() {
		this(new LinkedHashMap<>());
	}

	@Override
	public synchronized IFilterSet addFilter(IFilter filter) {
		String id = filter.getID();
		if ( filters.containsKey(id) ) {
			throw new IllegalArgumentException("Filter already exists: " + id);
		}
		filters.put(filter.getID(), filter);
		return this;
	}

	@Override
	public synchronized IFilterSet removeFilted(String filterID) {
		filters.remove(filterID);
		return this;
	}

	@Override
	public synchronized IFilterSetState checkState() {
		List<IFilterState> states = new ArrayList<>();
		Iterator<Map.Entry<String, IFilter>> it = filters.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<String, IFilter> pair = it.next();
			states.add(new FilterState(pair.getKey(), pair.getValue().checkState()));
		}
		return new FilterSetState(states);
	}

}
