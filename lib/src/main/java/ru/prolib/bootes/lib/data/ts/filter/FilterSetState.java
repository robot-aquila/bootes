package ru.prolib.bootes.lib.data.ts.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
		StringBuilder sb1 = new StringBuilder().append("FS["), sb2 = new StringBuilder();
		int count = states.size(), last = count - 1;
		for ( int i = 0; i < count; i ++ ) {
			IFilterState state = states.get(i);
			String x = state.isApproved() ? "A" : "D";
			sb1.append(x);
			sb2.append(state.getID()).append("=").append(x);
			if ( i == last ) {
				sb1.append(" ");
			} else {
				sb2.append(",");
			}
		}
		return sb1.append(sb2.toString()).append("]").toString();
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
