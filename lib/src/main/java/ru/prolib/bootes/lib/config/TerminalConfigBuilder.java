package ru.prolib.bootes.lib.config;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.config.ConfigException;

@Deprecated
public class TerminalConfigBuilder {
	private String driverID = "default";
	private Account qfTestAccount = new Account("TEST-ACCOUNT");
	private CDecimal qfTestBalance = CDecimalBD.ofRUB2("1000000");
	private File qfDataDir;
	private File tqLogPath;
	private int tqLogLevel;
	private String tqLogin, tqPassword, tqHost;
	private int tqPort = 3900;

	public TerminalConfig build(BasicConfig basicConfig) throws ConfigException {
		switch ( driverID ) {
		case "transaq":
			preBuildCheck_Transaq();
			break;
		case "default":
		case "qforts":
			preBuildCheck_Default();
			break;
		default:
			throw new ConfigException("Driver unsupported: " + driverID);
		}
		return new TerminalConfig(
				driverID,
				qfTestAccount,
				qfTestBalance,
				qfDataDir,
				tqLogPath,
				tqLogLevel,
				tqLogin,
				tqPassword,
				tqHost,
				tqPort
			);
	}
	
	private void preBuildCheck_Transaq() throws ConfigException {
		if ( tqLogPath == null ) {
			throw new ConfigException("Transaq log path was not defined");
		}
		if ( tqLogin == null ) {
			throw new ConfigException("Transaq login was not defined");
		}
		if ( tqPassword == null ) {
			throw new ConfigException("Transaq password was not defined");
		}
		if ( tqHost == null ) {
			throw new ConfigException("Transaq host was not defined");
		}
	}
	
	private void preBuildCheck_Default() throws ConfigException {
		if ( qfTestAccount == null ) {
			throw new ConfigException("Test account was not specified");
		}
		if ( qfTestBalance == null ) {
			throw new ConfigException("Test balance was not specified");
		}
		if ( qfTestBalance.getScale() != 2 ) {
			throw new ConfigException("Expected scale of test balance is 2 but: " + qfTestBalance.getScale());
		}
		if ( ! CDecimalBD.RUB.equals(qfTestBalance.getUnit()) ) {
			throw new ConfigException("Expected currency of test balance is RUB but: " + qfTestBalance.getUnit());
		}
		if ( qfDataDir == null ) {
			throw new ConfigException("Data directory was not specified");
		}
	}
	
	public TerminalConfigBuilder withDriverID(String driverID) {
		this.driverID = driverID;
		return this;
	}
	
	public TerminalConfigBuilder withQFortsTestAccount(Account account) {
		this.qfTestAccount = account;
		return this;
	}
	
	public TerminalConfigBuilder withQFortsTestBalance(CDecimal balance) {
		this.qfTestBalance = balance;
		return this;
	}
	
	public TerminalConfigBuilder withQFortsDataDirectory(File dir) {
		this.qfDataDir = dir;
		return this;
	}

	public TerminalConfigBuilder withTransaqLogPath(File path) {
		this.tqLogPath = path;
		return this;
	}

	public TerminalConfigBuilder withTransaqLogLevel(int level) {
		this.tqLogLevel = level;
		return this;
	}

	public TerminalConfigBuilder withTransaqLogin(String login) {
		this.tqLogin = login;
		return this;
	}

	public TerminalConfigBuilder withTransaqPassword(String pass) {
		this.tqPassword = pass;
		return this;
	}

	public TerminalConfigBuilder withTransaqHost(String host) {
		this.tqHost = host;
		return this;
	}

	public TerminalConfigBuilder withTransaqPort(int port) {
		this.tqPort = port;
		return this;
	}
	
}
