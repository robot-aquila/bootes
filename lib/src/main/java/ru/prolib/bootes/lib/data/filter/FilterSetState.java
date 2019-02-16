package ru.prolib.bootes.lib.data.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FilterSetState implements IFilterSetState {
	private final List<IFilterState> states;
	
	public FilterSetState(List<IFilterState> states) {
		this.states = states;
	}
	
	public FilterSetState() {
		this(new ArrayList<>());
	}

	@Override
	public boolean hasApproved() {
		boolean approved = false;
		for ( IFilterState state : states ) {
			if ( state.isApproved() ) {
				approved = true;
			}
		}
		return approved;
	}

	@Override
	public boolean hasDeclined() {
		boolean declined = false;
		for ( IFilterState state : states ) {
			if ( state.isDeclined() ) {
				declined = true;
			}
		}
		return declined;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1987221, 551)
				.append(states)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FilterSetState.class ) {
			return false;
		}
		FilterSetState o = (FilterSetState) other;
		return new EqualsBuilder()
				.append(o.states, states)
				.build();
	}

}
