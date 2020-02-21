package ru.prolib.bootes.lib.report;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class STRBHeader {
	private final String reportID, title;
	
	public STRBHeader(String report_id, String title) {
		this.reportID = report_id;
		this.title = title;
	}
	
	public String getReportID() {
		return reportID;
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(5551327, 3)
				.append(reportID)
				.append(title)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != STRBHeader.class ) {
			return false;
		}
		STRBHeader o = (STRBHeader) other;
		return new EqualsBuilder()
				.append(o.reportID, reportID)
				.append(o.title, title)
				.build();
	}

}
