package ru.prolib.bootes.lib.data.ts.filter.impl;

import ru.prolib.bootes.lib.data.ts.filter.IFilter;

public abstract class AbstractFilter<ArgType> implements IFilter<ArgType> {
	protected final String id;
	
	public AbstractFilter(String id) {
		this.id = id;
	}
	
	@Override
	public String getID() {
		return id;
	}

}
