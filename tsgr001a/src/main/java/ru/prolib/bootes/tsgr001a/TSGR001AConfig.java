package ru.prolib.bootes.tsgr001a;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;

public class TSGR001AConfig {
	private final Account account;
	private final String title, filterDefs, reportHeader;
	
	public TSGR001AConfig(Account account,
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
		if ( other == null || other.getClass() != TSGR001AConfig.class ) {
			return false;
		}
		TSGR001AConfig o = (TSGR001AConfig) other;
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

}
