package ru.prolib.bootes.tsgr001a.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.Account;

public class TSGR001AInstConfig {
	private final Account account;
	private final String title, filterDefs, reportHeader;
	
	public TSGR001AInstConfig(Account account,
						  String title,
						  String filterDefs,
						  String reportHeader)
	{
		this.account = account;
		this.title = title;
		this.filterDefs = filterDefs;
		this.reportHeader = reportHeader;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getFilterDefs() {
		return filterDefs;
	}
	
	public String getReportHeader() {
		return reportHeader;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TSGR001AInstConfig.class ) {
			return false;
		}
		TSGR001AInstConfig o = (TSGR001AInstConfig) other;
		return new EqualsBuilder()
				.append(o.account, account)
				.append(o.title, title)
				.append(o.filterDefs, filterDefs)
				.append(o.reportHeader, reportHeader)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(18274929, 5263)
				.append(account)
				.append(title)
				.append(filterDefs)
				.append(reportHeader)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
