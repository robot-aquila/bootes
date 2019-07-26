package ru.prolib.bootes.lib.config;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;

@Deprecated
public class TerminalConfig {
	private final String driverID;
	private final Account qfTestAccount;
	private final CDecimal qfTestBalance;
	private final File qfDataDir;
	private final File tqLogPath;
	private final int tqLogLevel;
	private final String tqLogin,tqPassword,tqHost;
	private final int tqPort;
	
	public TerminalConfig(String driverID,
						  Account qfTestAccount,
						  CDecimal qfTestBalance,
						  File qfDataDir,
						  File tq_log_path,
						  int tq_log_level,
						  String tq_login,
						  String tq_password,
						  String tq_host,
						  int tq_port)
	{
		this.driverID = driverID;
		this.qfTestAccount = qfTestAccount;
		this.qfTestBalance = qfTestBalance;
		this.qfDataDir = qfDataDir;
		this.tqLogPath = tq_log_path;
		this.tqLogLevel = tq_log_level;
		this.tqLogin = tq_login;
		this.tqPassword = tq_password;
		this.tqHost = tq_host;
		this.tqPort = tq_port;
	}
	
	public String getDriverID() {
		return driverID;
	}

	public Account getQForstTestAccount() {
		return qfTestAccount;
	}
	
	public CDecimal getQForstTestBalance() {
		return qfTestBalance;
	}
	
	public File getQFortsDataDirectory() {
		return qfDataDir;
	}
	
	public File getTransaqLogPath() {
		return tqLogPath;
	}
	
	public int getTransaqLogLevel() {
		return tqLogLevel;
	}
	
	public String getTransaqLogin() {
		return tqLogin;
	}
	
	public String getTransaqPassword() {
		return tqPassword;
	}
	
	public String getTransaqHost() {
		return tqHost;
	}
	
	public int getTransaqPort() {
		return tqPort;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("driverID", driverID)
			.append("qfTestAccount", qfTestAccount)
			.append("qfTestBalance", qfTestBalance)
			.append("qfDataDir", qfDataDir)
			.append("tqLogPath", tqLogPath)
			.append("tqLogLevel", tqLogLevel)
			.append("tqLogin", tqLogin)
			.append("tqPassword", "***")
			.append("tqHost", tqHost)
			.append("tqPort", tqPort)
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(83427, 200505)
			.append(driverID)
			.append(qfTestAccount)
			.append(qfTestBalance)
			.append(qfDataDir)
			.append(tqLogPath)
			.append(tqLogLevel)
			.append(tqLogin)
			.append(tqPassword)
			.append(tqHost)
			.append(tqPort)
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
			.append(o.driverID, driverID)
			.append(o.qfTestAccount, qfTestAccount)
			.append(o.qfTestBalance, qfTestBalance)
			.append(o.qfDataDir, qfDataDir)
			.append(o.tqLogPath, tqLogPath)
			.append(o.tqLogLevel, tqLogLevel)
			.append(o.tqLogin, tqLogin)
			.append(o.tqPassword, tqPassword)
			.append(o.tqHost, tqHost)
			.append(o.tqPort, tqPort)
			.isEquals();
	}
	
}
