package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class TerminalConfig {
	private final Account qfTestAccount;
	private final CDecimal qfTestBalance;
	private final File qfSymbolDir, qfL1Dir;
	
	public TerminalConfig(Account qfTestAccount,
						  CDecimal qfTestBalance,
						  File qfSymbolDir,
						  File qfL1Dir)
	{
		this.qfTestAccount = qfTestAccount;
		this.qfTestBalance = qfTestBalance;
		this.qfSymbolDir = qfSymbolDir;
		this.qfL1Dir = qfL1Dir;
	}

	public Account getQForstTestAccount() {
		return qfTestAccount;
	}
	
	public CDecimal getQForstTestBalance() {
		return qfTestBalance;
	}
	
	public File getQFortsSymbolDirectory() {
		return qfSymbolDir;
	}
	
	public File getQFortsL1Directory() {
		return qfL1Dir;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("qfTestAccount", qfTestAccount)
			.append("qfTestBalance", qfTestBalance)
			.append("qfSymbolDir", qfSymbolDir)
			.append("qfL1Dir", qfL1Dir)
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(83427, 200505)
			.append(qfTestAccount)
			.append(qfTestBalance)
			.append(qfSymbolDir)
			.append(qfL1Dir)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TerminalConfig.class ) {
			return false;
		}
		TerminalConfig o = (TerminalConfig) other;
		return new EqualsBuilder()
			.append(o.qfTestAccount, qfTestAccount)
			.append(o.qfTestBalance, qfTestBalance)
			.append(o.qfSymbolDir, qfSymbolDir)
			.append(o.qfL1Dir, qfL1Dir)
			.isEquals();
	}
	
}
