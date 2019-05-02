package ru.prolib.bootes.lib;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Information of an account.
 */
public class AccountInfo {
	private final Account account;
	private final CDecimal balance;
	
	public AccountInfo(Account account, CDecimal balance) {
		this.account = account;
		this.balance = balance;
	}
	
	public AccountInfo(Account account) {
		this(account, null);
	}
	
	public Account getAccount() {
		return account;
	}
	
	public CDecimal getBalance() {
		return balance;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(76242347, 991)
				.append(account)
				.append(balance)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AccountInfo.class ) {
			return false;
		}
		AccountInfo o = (AccountInfo) other;
		return new EqualsBuilder()
				.append(o.account, account)
				.append(o.balance, balance)
				.build();
	}
	
}
