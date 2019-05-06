package ru.prolib.bootes.lib.data.ts.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
		filters.put(id, filter);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FilterSet.class ) {
			return false;
		}
		FilterSet o = (FilterSet) other;
		Map<String, IFilter> my_filters, other_filters;
		synchronized ( this ) {
			my_filters = new LinkedHashMap<>(filters);
		}
		synchronized ( o ) {
			other_filters = new LinkedHashMap<>(o.filters);
		}
		return new EqualsBuilder()
				.append(other_filters, my_filters)
				.build();
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(78127, 915)
				.append(filters)
				.build();
	}

}
