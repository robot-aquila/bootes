package ru.prolib.bootes.lib.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.difflib.patch.AbstractDelta;

public class STRBCmpResult {
	private final STRBHeader header;
	private final STRBCmpResultType type;
	private final String descr;
	private final List<AbstractDelta<String>> deltas;
	
	public STRBCmpResult(STRBHeader header, STRBCmpResultType type, String descr, List<AbstractDelta<String>> deltas) {
		this.header = header;
		this.type = type;
		this.descr = descr;
		this.deltas = deltas;
	}
	
	public STRBCmpResult(STRBHeader header, STRBCmpResultType type, String descr) {
		this(header, type, descr, new ArrayList<>());
	}
	
	public STRBHeader getHeader() {
		return header;
	}
	
	public STRBCmpResultType getType() {
		return type;
	}
	
	public String getDescription() {
		return descr;
	}
	
	public List<AbstractDelta<String>> getDeltas() {
		return deltas;
	}
	
	public boolean identical() {
		return type == STRBCmpResultType.IDENTICAL;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(220147909, 91)
				.append(header)
				.append(type)
				.append(descr)
				.append(deltas)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != STRBCmpResult.class ) {
			return false;
		}
		STRBCmpResult o = (STRBCmpResult) other;
		return new EqualsBuilder()
				.append(o.header, header)
				.append(o.type, type)
				.append(o.descr, descr)
				.append(o.deltas, deltas)
				.build();
	}
	
}
