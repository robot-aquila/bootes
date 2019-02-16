package ru.prolib.bootes.lib.data.filter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FilterState implements IFilterState {
	private final String id;
	private final boolean approved;
	
	public FilterState(String id, boolean approved) {
		this.id = id;
		this.approved = approved;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean isApproved() {
		return approved;
	}

	@Override
	public boolean isDeclined() {
		return ! approved;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(4788123, 903)
				.append(id)
				.append(approved)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FilterState.class ) {
			return false;
		}
		FilterState o = (FilterState) other;
		return new EqualsBuilder()
				.append(o.id, id)
				.append(o.approved, approved)
				.build();
	}

}
