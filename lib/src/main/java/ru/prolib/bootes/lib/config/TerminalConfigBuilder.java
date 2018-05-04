package ru.prolib.bootes.lib.config;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class TerminalConfigBuilder {
	private Account qfTestAccount = new Account("TEST-ACCOUNT");
	private CDecimal qfTestBalance = CDecimalBD.ofRUB2("1000000");
	private File qfDataDir;

	public TerminalConfig build(BasicConfig basicConfig) throws ConfigException {
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
		return new TerminalConfig(
				qfTestAccount,
				qfTestBalance,
				qfDataDir
			);
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
	
}
