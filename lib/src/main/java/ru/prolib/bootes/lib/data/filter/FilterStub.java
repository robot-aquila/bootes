package ru.prolib.bootes.lib.data.filter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FilterStub implements IFilter {
	private final String id;
	private final boolean result;
	
	public FilterStub(String id, boolean result) {
		this.id = id;
		this.result = result;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean checkState() {
		return result;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(199873, 4009)
				.append(id)
				.append(result)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FilterStub.class ) {
			return false;
		}
		FilterStub o = (FilterStub) other;
		return new EqualsBuilder()
				.append(o.id, id)
				.append(o.result, result)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
