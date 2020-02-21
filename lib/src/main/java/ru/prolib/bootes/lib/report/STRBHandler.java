package ru.prolib.bootes.lib.report;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class STRBHandler<HandlerType> {
	private final STRBHeader header;
	private final HandlerType handler;
	
	public STRBHandler(STRBHeader header, HandlerType handler) {
		this.header = header;
		this.handler = handler;
	}
	
	public STRBHandler(String reportID, String title, HandlerType handler) {
		this(new STRBHeader(reportID, title), handler);
	}
	
	public STRBHeader getHeader() {
		return header;
	}
	
	public HandlerType getHandler() {
		return handler;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(97315003, 37)
				.append(header)
				.append(handler)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != STRBHandler.class ) {
			return false;
		}
		STRBHandler<?> o = (STRBHandler<?>) other;
		return new EqualsBuilder()
				.append(o.header, header)
				.append(o.handler, handler)
				.build();
	}

}
